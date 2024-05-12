package it.polimi.ingsw.am13.network.socket;

import it.polimi.ingsw.am13.controller.GameController;
import it.polimi.ingsw.am13.controller.Lobby;
import it.polimi.ingsw.am13.controller.LobbyException;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.network.socket.message.command.*;
import it.polimi.ingsw.am13.network.socket.message.response.MsgResponseGetRooms;

import java.io.*;
import java.net.Socket;
import java.util.List;

// TODO: Discuss about Exceptions. Should we add a new one for this class?

public class ClientRequestsHandler extends Thread {

    /**
     * Socket opened with the connected client
     */
    private final Socket clientSocket;

    /**
     * Socket input stream generated from the current socket
     */
    private final ObjectInputStream inputStream;

    /**
     * Socket output stream generated from the current socket
     */
    private final ObjectOutputStream outputStream;

    /**
     * Instance of the Lobby which exposes the main methods for match init & disconnection
     */
    private final Lobby lobby;


    /**
     * PlayerLobby of the current socket
     */
    private PlayerLobby player;

    /**
     * GameController associated to the match the current player is playing (if he's in a match)
     */
    private GameController gameController;


    /**
     * GameListener instance associated to the current player
     */
    private GameListenerServerSocket gameListener;


    /**
     * Constructor for the ClientRequestsHandler class. It retrieves the existing Lobby instance and gets the Socket's
     * input and output streams.
     * @param clientSocket Opened Socket of the newly connected client
     */
    public ClientRequestsHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.lobby = Lobby.getInstance();

        this.player = null;
        this.gameController = null;
        this.gameListener = null;

        // Set the input stream of the socket.
        try {
            this.inputStream = new ObjectInputStream(this.clientSocket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Set the output stream of the socket.
        try {
            this.outputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Main method for the Thread execution
     */
    @Override
    public void run() {
        System.out.printf("[Socket][Client:%d] Started listening for new requests\n", clientSocket.getPort());

        MsgCommand newCommand;
        while (true) {
            // Read new Command sent from client
            try {
                // readObject() is a blocking method!
                newCommand = (MsgCommand) this.inputStream.readObject();
            } catch (IOException e) {
                /*
                    TODO: Refine Exception management at this point. Here is what I guess should happen:
                        ->  If the inputStream gets closed in handleDisconnection(), then a EOFException is thrown, which
                            is a subclass of IOException. When this happens, we have to close the socket and end the Thread
                        ->  If something strange happens, we should (only after EOFException management!) catch the
                            general IOException. What to do when something like this happens needs to be discussed.
                            (maybe a new Exception class for this strange behaviours?)
                */
                // When this happens, we suppose that the client always has been disconnected (not true, see "TODOs" above)
                // Break out of the while true cycle.
                break;
            } catch (ClassNotFoundException e) {
                // throw away this request as it's not a Command
                System.out.printf("[Socket][Client:%d] Unexpected message received (expected a Command)\n", clientSocket.getPort());
                continue; // skip command switching
            }

            // Understand which command has been sent and act accordingly
            switch (newCommand) {
                case MsgCommandGetRooms command ->                  handleGetRooms(command);
                case MsgCommandCreateRoom command ->                handleCreateRoom(command);
                case MsgCommandJoinRoom command ->                  handleJoinRoom(command);
                case MsgCommandLeaveRoom command ->                 handleLeaveRoom(command);
                case MsgCommandPing command ->                      handlePing(command);
                case MsgCommandPlayStarter command ->               handlePlayStarter(command);
                case MsgCommandChoosePersonalObjective command ->   handleChoosePersonalObjective(command);
                case MsgCommandPlayCard command ->                  handlePlayCard(command);
                case MsgCommandPickCard command ->                  handlePickCard(command);
                case MsgCommandReconnectGame command ->             handleReconnectGame(command);

                default -> System.out.printf("[Socket][Client:%d] Unexpected message received (Command type not found)\n", clientSocket.getPort());
            }
        }

        // Client is disconnected
        try {
            this.clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // run() returns, the Thread ends its life.
    }

    /**
     * Logging function which notifies the command which has been received on the server console .
     * @param cmd Command name
     */
    private void logCommand(String cmd) {
        System.out.printf("[Socket][Client:%d] Received Command %s\n", clientSocket.getPort(), cmd);
    }

    /**
     * Checks if the game controller has been set. This method should be called by all the handlers associated to
     * Commands related to the match.
     * @return True if GameController is set, False otherwise.
     */
    private boolean assertGameController() {
        // TODO: maybe should be handled differently (Exception for this class?)
        return this.gameController != null;
    }

    /**
     * Handler which should be called from the GameListener when the match is started.
     * It sets the GameController of the new mach the player is playing in.
     * @param newGameController GameController of the new match
     */
    public void handleStartGame(GameController newGameController) {
        this.gameController = newGameController;
    }

    /**
     * Handler which should be called from the GameListener when the match is ended.
     * It unsets all the attributes related to the ended match.
     */
    public void handleEndGame() {
        // reset PlayerLobby, GameListener and GameController as they are not useful anymore
        this.player = null;
        this.gameListener = null;
        this.gameController = null;
    }

    // TODO: Implement this function in GameListenerServerSocket, it needs to be called in the appropriate update
    /**
     * Handler which should be called from the GameListener when the player results disconnected (connection crash or
     * closed the application). When this happens, the inputStream associated with the client gets closed.
     * The current ClientRequestsHandler should be "destroyed" (references are removed). <br>
     * Please note that we close the socket no matter the type of disconnection. This enables a uniformed way of
     * handling disconnections, even when the opened Socket could be reused.
     */
    public void handleDisconnection() {
        // should arrive when a socket has already been opened
        // destroy this ClientRequestsHandler object, as it's not useful anymore.

        // close the input stream.
        try {
            this.inputStream.close();
        } catch (IOException e) {
            // TODO: Investigate more on this matter
            System.out.printf("[Socket][Client:%d] " +
                    "handleDisconnection() called but IOException occurred when closing Input Stream." +
                    "Please investigate (socket already closed?)\n", clientSocket.getPort());
            return;
        }

        // dereference all client-related attributes for safety.
        this.player = null;
        this.gameController = null;
        this.gameListener = null;
    }


    // --------------------------------------------------------------------------
    // ----------------------------- > COMMANDS < -------------------------------
    // --------------------------------------------------------------------------

    /**
     * Command handler for "reconnectGame" command. This command should be the first one received from a client which
     * wants to reconnect after a disconnection. Will be checked if a player with given command's information exists,
     * otherwise an error response is sent back to client.
     * @param command reconnectGame command
     */
    public void handleReconnectGame(MsgCommandReconnectGame command) {
        logCommand("reconnectGame");

        // create a hypothetical PlayerLobby
        PlayerLobby hypotPlayerLobby = new PlayerLobby(
                command.getNickname(),
                command.getToken()
        );

        // instantiate a hypothetical GameListener
        GameListenerServerSocket hypotGameListener = new GameListenerServerSocket(this, this.outputStream, hypotPlayerLobby);

        // try reconnecting the player to its match
        try {
            // if information given are valid, the "ghost" player is found and successfully reconnected
            this.gameController = this.lobby.reconnectPlayer(hypotGameListener);
        } catch (LobbyException | ConnectionException | GameStatusException exc) {
            // if no "ghost" player is found or other game-related exceptions are thrown, send error message back to client
            this.gameListener.sendError(exc);
            return; // [!] important!
        }

        // reconnection handled successfully, link attributes recovering previous client state
        this.player = hypotPlayerLobby;
        this.gameListener = hypotGameListener;
    }

    /**
     * Command handler for "ping" command. Updates the game controller about the last ping received.
     * @param command MsgCommandPing command
     */
    private void handlePing(MsgCommandPing command) {
        logCommand("ping");

        // TODO: What do we do when the gameController is null? Note that this is replicated in many other handlers.
        if(assertGameController()) {
            // update last ping
            this.gameController.updatePing(this.player);
        }
    }

    /**
     * Command handler for "getRooms" command. This command is handled differently compared with others as no player is
     * associated with the request.
     * @param command MsgCommandGetRooms command
     */
    private void handleGetRooms(MsgCommandGetRooms command) {
        // [!] Special case message: client has no PlayerLobby linked to it.
        //     Response is custom managed here.

        logCommand("getRooms");

        // Retrieve rooms and build the Response
        List<RoomIF> rooms = lobby.getRooms();
        MsgResponseGetRooms response = new MsgResponseGetRooms(rooms);

        // Send the Response
        try {
            this.outputStream.writeObject(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Command handler for "createRoom" command. The client sends its chosen nickname and token color. If the given
     * information are valid for the creation of a new Room, it gets created. Otherwise, an error is sent back to client.
     * @param command
     */
    private void handleCreateRoom(MsgCommandCreateRoom command) {
        logCommand("createRoom");

        // create a hypothetical PlayerLobby
        PlayerLobby hypotPlayerLobby = new PlayerLobby(
                command.getChosenNickname(),
                command.getToken()
        );

        // instantiate a hypothetical GameListener
        GameListenerServerSocket hypotGameListener = new GameListenerServerSocket(this, this.outputStream, hypotPlayerLobby);

        // try creating a new Room.
        try {
            lobby.createRoom(
                    hypotGameListener,
                    command.getPlayers()
            );
        } catch (LobbyException exc) {
            // the chosen nickname is invalid (another player has it!). Send an error back.
            hypotGameListener.sendError(exc);
            return; // [!] important
        }

        // Room has been created successfully. Response MsgResponseJoinRoom has been handled by the game listener.
        // the hypothetical PlayerLobby & GameListener can be set as official ones of this client handler.
        this.player = hypotPlayerLobby;
        this.gameListener = hypotGameListener;
    }

    private void handleJoinRoom(MsgCommandJoinRoom command) {
        logCommand("joinRoom");

        // create a hypothetical PlayerLobby
        PlayerLobby hypotPlayerLobby = new PlayerLobby(
                command.getChosenNickname(),
                command.getToken()
        );

        // instantiate a hypothetical GameListener
        GameListenerServerSocket hypotGameListener = new GameListenerServerSocket(this, this.outputStream, hypotPlayerLobby);


        // try joining an existing Room.
        try {
            lobby.joinRoom(
                    command.getGameId(),
                    hypotGameListener
            );
        } catch (LobbyException exc) {
            // Error on joinRoom. Send an error back.
            hypotGameListener.sendError(exc);
            return; // [!] important
        }

        // Room has been joined successfully. Response MsgResponseJoinRoom has been handled by the game listener.
        // the hypothetical PlayerLobby & GameListener can be set as official ones of this client handler.
        this.player = hypotPlayerLobby;
        this.gameListener = hypotGameListener;
    }

    private void handleLeaveRoom(MsgCommandLeaveRoom command) {
        logCommand("leaveRoom");

        // try to leave the current Room
        try {
            lobby.leaveRoom(this.gameListener);
        } catch (LobbyException exc) {
            this.gameListener.sendError(exc);
            return; // [!] important
        }

        // update PlayerLobby & GameListener of this client handler. The player has not them anymore.
        this.player = null;
        this.gameListener = null;
    }

    private void handlePlayStarter(MsgCommandPlayStarter command) {
        logCommand("playStarter");

        if(assertGameController()) {
            // play the starter card
            try {
                gameController.playStarter(
                        this.player,
                        command.getSide()
                );
            } catch (   InvalidPlayerException |
                        InvalidPlayCardException |
                        GameStatusException exc
            ) {
                gameListener.sendError(exc);
            }
        }
    }

    private void handleChoosePersonalObjective(MsgCommandChoosePersonalObjective command) {
        logCommand("choosePersonalObjective");

        if(assertGameController()) {
            // choose the personal objectives
            try {
                gameController.choosePersonalObjective(
                        this.player,
                        command.getCard()
                );
            } catch (   InvalidPlayerException |
                        InvalidChoiceException |
                        VariableAlreadySetException |
                        GameStatusException exc
            ) {
                gameListener.sendError(exc);
            }
        }
    }

    private void handlePlayCard(MsgCommandPlayCard command) {
        logCommand("playCard");

        if(assertGameController()) {
            // try to place the given card on specified coordinates
            try {
                gameController.playCard(
                        this.player,
                        command.getCard(),
                        command.getSide(),
                        command.getCoords()
                );
            } catch (   InvalidPlayerException |
                        RequirementsNotMetException |
                        InvalidPlayCardException |
                        GameStatusException exc)
            {
                gameListener.sendError(exc);
            }
        }
    }

    private void handlePickCard(MsgCommandPickCard command) {
        logCommand("pickCard");

        if(assertGameController()) {
            // try to pick the given card from the table
            try {
                gameController.pickCard(
                        this.player,
                        command.getCard()
                );
            } catch (   InvalidDrawCardException |
                        GameStatusException |
                        InvalidPlayerException exc
            ) {
                gameListener.sendError(exc);
            }
        }
    }
}
