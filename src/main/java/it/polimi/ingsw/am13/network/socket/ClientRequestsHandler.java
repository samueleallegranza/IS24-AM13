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

public class ClientRequestsHandler extends Thread {

    private final Socket clientSocket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    private final Lobby lobby;


    private PlayerLobby player;
    private GameController gameController;


    private GameListenerServerSocket gameListener;


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

    @Override
    public void run() {
        System.out.printf("[Socket][Client:%d] Started listening for new requests\n", clientSocket.getPort());

        MsgCommand newCommand;
        while (true) {
            // Read new Command sent from client (blocking function, until received)
            try {
                newCommand = (MsgCommand) this.inputStream.readObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                // throw away this request as it's not a Command
                System.out.printf("[Socket][Client:%d] Unexpected message received (expected a Command)\n", clientSocket.getPort());
                continue;
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

                default -> System.out.printf("[Socket][Client:%d] Unexpected message received (Command type not found)\n", clientSocket.getPort());
            }
        }
    }

    private void logCommand(String cmd) {
        System.out.printf("[Socket][Client:%d] Received Command %s\n", clientSocket.getPort(), cmd);
    }

    private boolean assertGameController() {
        return this.gameController != null;
    }

    // TODO: Implement this function in GameListenerServerSocket, it needs to be called in the appropriate update
    public void handleStartGame(GameController newGameController) {
        this.gameController = newGameController;
    }

    // TODO: Implement this function in GameListenerServerSocket, it needs to be called in the appropriate update
    public void handleEndGame() {
        // reset PlayerLobby, GameListener and GameController as they are not useful anymore
        this.player = null;
        this.gameListener = null;
        this.gameController = null;
    }

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

    private void handlePing(MsgCommandPing command) {
        logCommand("ping");

        // TODO: What do we do when the gameController is null? Note that this is replicated in many other handlers.
        if(assertGameController()) {
            // update last ping
            this.gameController.updatePing(this.player);
        }
    }


    // TODO: This command does not handle player reconnection when has crashed because of connection issues.
    // in that case a new socket is created (this thread will keep existing as a ghost)
    private void handleReconnectGame(MsgCommandReconnectGame command) {
        logCommand("playerReconnect");

        // try the reconnection of the player linked to the current socket
        // if a game listener does not exist, this should be handled inside the method Lobby.reconnectPlayer()
        try {
            lobby.reconnectPlayer(this.gameListener);
        } catch (   LobbyException |
                    ConnectionException |
                    GameStatusException exc
        ) {
            this.gameListener.sendError(exc);
        }
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
