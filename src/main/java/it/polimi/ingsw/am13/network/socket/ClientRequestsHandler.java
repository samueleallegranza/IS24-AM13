package it.polimi.ingsw.am13.network.socket;

import it.polimi.ingsw.am13.client.gamestate.PlayerClient;
import it.polimi.ingsw.am13.controller.GameController;
import it.polimi.ingsw.am13.controller.Lobby;
import it.polimi.ingsw.am13.controller.LobbyException;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.network.socket.message.command.*;
import it.polimi.ingsw.am13.network.socket.message.response.MsgResponseGetRooms;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientRequestsHandler extends Thread {

    private final Socket clientSocket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    private final Lobby lobby;


    private PlayerLobby player;
    private GameListenerServerSocket gameListener;


    public ClientRequestsHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.lobby = Lobby.getInstance();

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
                case MsgCommandGetRooms command ->      handleGetRooms(command);
                case MsgCommandCreateRoom command ->    handleCreateRoom(command);
                case MsgCommandJoinRoom command ->      handleJoinRoom(command);
                case MsgCommandLeaveRoom command ->      handleLeaveRoom(command);

                default -> System.out.printf("[Socket][Client:%d] Unexpected message received (Command type not found)\n", clientSocket.getPort());
            }
        }
    }

    public void logCommand(String cmd) {
        System.out.printf("[Socket][Client:%d] Received Command %s\n", clientSocket.getPort(), cmd);
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
        GameListenerServerSocket hypotGameListener = new GameListenerServerSocket(this.outputStream, hypotPlayerLobby);

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
        GameListenerServerSocket hypotGameListener = new GameListenerServerSocket(this.outputStream, hypotPlayerLobby);


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

}
