package it.polimi.ingsw.am13.client;

import it.polimi.ingsw.am13.ParametersClient;
import it.polimi.ingsw.am13.ParametersServer;
import it.polimi.ingsw.am13.PromptCommand;
import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.network.rmi.NetworkHandlerRMI;
import it.polimi.ingsw.am13.client.network.socket.NetworkHandlerSocket;
import it.polimi.ingsw.am13.client.view.View;
import it.polimi.ingsw.am13.client.view.gui.ViewGUI;
import it.polimi.ingsw.am13.client.view.tui.ViewTUI;
import it.polimi.ingsw.am13.network.rmi.LobbyRMIIF;
import javafx.application.Application;

import java.io.IOException;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class ClientMain {

    public static void main(String[] args) {

        List<String> argsList = List.of(args);
        if(argsList.contains("--help")) {
            System.out.println(generateHelp());
            System.exit(0);
        }

        Queue<String> argsQueue = new LinkedList<>(argsList);
        while(!argsQueue.isEmpty()) {
            String commandKey = argsQueue.poll();
            PromptCommand command = commands.get(commandKey);
            if(command == null) {
                System.out.println("The arguments list is wrong (parameter '" + commandKey + "' is not accepted)\nType --help for the list of possible commands");
                System.exit(-1);
            }

            List<String> commandArgs = new ArrayList<>();
            while(!argsQueue.isEmpty() && !argsQueue.peek().startsWith("--"))
                commandArgs.add(argsQueue.poll());
            command.getAction().accept(commandArgs);
        }

        // check that ip and port are valid
        InetAddress ipInet;
        try {
            ipInet = new InetSocketAddress(ParametersClient.SERVER_IP, ParametersClient.SERVER_PORT).getAddress();
        } catch (Exception e) {
            System.out.println("[Error] Given ip or port not valid, please try again.");
            return;
        }
        if(ipInet == null) {
            System.out.println("[Error] Given ip or port not valid, please try again.");
            return;
        }

        View view;
        NetworkHandler networkHandler;
        if (ParametersClient.IS_TUI) {
            view = new ViewTUI();
            networkHandler = initConnection(view);
            // Sets the network handler for the view, in order to allow it to send commands/messages to the server
            view.setNetworkHandler(networkHandler);
            view.showStartupScreen(ParametersClient.IS_SOCKET, ParametersClient.SERVER_IP, ParametersClient.SERVER_PORT);
            networkHandler.getRooms();
        }
        else{
            Application.launch(ViewGUI.class, Boolean.toString(ParametersClient.IS_SOCKET),
                    ParametersClient.SERVER_IP, Integer.toString(ParametersClient.SERVER_PORT));
        }
    }

    public static NetworkHandler initConnection(View view) {
        NetworkHandler networkHandler;
        if(ParametersClient.IS_SOCKET) {
            // open socket connection with ip:port
            System.out.println("[Client] Connecting to server " + ParametersClient.SERVER_IP + ":" + ParametersClient.SERVER_PORT);
            Socket socket;
            try {
                socket = new Socket(ParametersClient.SERVER_IP, ParametersClient.SERVER_PORT);
            } catch (IOException e) {
                System.out.println("[Error] Something went wrong while trying to reach server at " + ParametersClient.SERVER_IP + ":" + ParametersClient.SERVER_PORT);
                throw new RuntimeException(e);
            }
            System.out.println("[Client] Connected to server");

            networkHandler = new NetworkHandlerSocket(socket, view);
        } else {
            Registry registry;
            try {
                //TODO: rivedi meglio questo setup x rmi
                // server hostname should be the ip of this client (the correct network address)

                System.setProperty("java.rmi.server.hostname", ParametersClient.CLIENT_IP);
                registry = LocateRegistry.getRegistry(ParametersClient.SERVER_IP, ParametersServer.RMI_PORT);
                LobbyRMIIF lobby = (LobbyRMIIF) registry.lookup(ParametersServer.LOBBY_RMI_NAME);
                networkHandler = new NetworkHandlerRMI(lobby, view);
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        }
        return networkHandler;
    }

    /**
     * Generates a help string to be printed via command line
     * @return Help string for the client app
     */
    private static String generateHelp() {
        StringBuilder sb = new StringBuilder("Help for game Codex Naturalis (client)\nAccepted commands:\n");
        for(PromptCommand command : commandsList)
            sb.append(String.format("%-15s: %s\n", command.getCommand(), command.getDescription()));
        return sb.toString();
    }

    /**
     * List of accepted commands from the client command line
     */
    private static final List<PromptCommand> commandsList = List.of(
            new PromptCommand("tui",
                    "Starts the application with Text User Interface (default is with Graphical User Interface)",
                    args -> ParametersClient.IS_TUI = true),
            new PromptCommand("rmi",
                    "Starts the application by using rmi to connect to the server (default is with socket)",
                    args -> {
                        ParametersClient.IS_SOCKET = false;
                        ParametersClient.SERVER_PORT = ParametersClient.RMI_DEFAULT_PORT;
                    }),
            new PromptCommand("skip_room",
                    "(<number of players>) Skips the room phase, with the specified number of players for the room (debug purposes)",
                    args -> {
                        ParametersClient.SKIP_ROOM = true;
                        ParametersClient.DEBUG_NPLAYERS = Integer.parseInt(args.getFirst());
                    }),
            new PromptCommand("skip_init",
                    "Makes the choices for the initialization phase automatic (debug purposes)",
                    args -> ParametersClient.SKIP_INIT = true),
            new PromptCommand("skip_turns",
                    "Makes the choices for the turn-based phase automatic (debug purposes)",
                    args -> ParametersClient.SKIP_TURNS = true),
            new PromptCommand("no_sounds",
                    "Disables all the sounds during the game",
                    args -> ParametersClient.SOUND_ENABLE = false),
            new PromptCommand("ip",
                    "(<ip) Sets the IP address of the server",
                    args -> ParametersClient.SERVER_IP = args.getFirst()),
            new PromptCommand("port",
                    "(<port>) Sets the port number of the server",
                    args -> ParametersClient.SERVER_PORT = Integer.parseInt(args.getFirst())),
            new PromptCommand("client_ip",
                    "(<ip>) Sets the IP address of the client",
                    args -> ParametersClient.CLIENT_IP = args.getFirst())
    );

    /**
     * Map associating the command key to the command itself
     */
    private final static Map<String, PromptCommand> commands;
    static {
        commands = new HashMap<>();
        for(PromptCommand command : commandsList)
            commands.put(command.getCommand(), command);
    }
}
