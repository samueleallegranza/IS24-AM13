package it.polimi.ingsw.am13;

import it.polimi.ingsw.am13.network.rmi.LobbyRMI;
import it.polimi.ingsw.am13.network.socket.ServerSocketHandler;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class ServerMain {

    public static void main(String[] args) {

        // ### 1. Check if arguments passed are valid (will replace default ports and default ip)
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
            try {
                command.executeAction(commandArgs);
            } catch (IllegalArgumentException e) {
                System.out.println("For command " + commandKey + " only " + command.getParameterList().size() + " must be present");
                System.exit(-1);
            }
        }

        System.out.println("Socket port\t\t\t: " + ParametersServer.SOCKET_PORT);
        System.out.println("RMI port\t\t\t: " + ParametersServer.RMI_PORT);
        System.out.println("Server's IP address\t: " + ParametersServer.SERVER_IP);

        // ### 2. Instantiate Socket handler. Quit if port is invalid
        try {
            ServerSocketHandler serverSocket = new ServerSocketHandler(ParametersServer.SOCKET_PORT);
            serverSocket.start();
        } catch (IOException e) {
            throw new RuntimeException(e); // invalid port
        }

        // ### 3. Binding of RMI registry
       try {
            System.setProperty("java.rmi.server.hostname", ParametersServer.SERVER_IP);
            Registry registry = LocateRegistry.createRegistry(ParametersServer.RMI_PORT);
            registry.bind(ParametersServer.LOBBY_RMI_NAME, new LobbyRMI());
        } catch (RemoteException e) {
            //TODO: capisci come gestire
            throw new RuntimeException(e);
        } catch (AlreadyBoundException e) { // Should never happen, unless ServerMain is run more than once
            throw new RuntimeException(e);
        }

    }

    /**
     * Generates a help string to be printed via command line
     * @return Help string for the server app
     */
    private static String generateHelp() {
        StringBuilder sb = new StringBuilder("Help for game Codex Naturalis (server)\nAccepted commands:\n");
        for(PromptCommand command : commandsList)
            sb.append(command.generateHelpString());
        return sb.toString();
    }

    /**
     * List of accepted commands from the client command line
     */
    private static final List<PromptCommand> commandsList = List.of(
            new PromptCommand("ip",
                    "Sets the IP address of the server",
                    args -> ParametersServer.SERVER_IP = args.removeFirst(),
                    "ip"),
            new PromptCommand("rmi",
                    "Sets the RMI port",
                    args -> ParametersServer.RMI_PORT = Integer.parseInt(args.getFirst()),
                    "rmi port"),
            new PromptCommand("socket",
                    "Sets the socket port",
                    args -> ParametersServer.SOCKET_PORT = Integer.parseInt(args.getFirst()),
                    "rmi port"),
            new PromptCommand("points",
                    "Sets the points needed for the final phase",
                    args -> ParametersServer.POINTS_FOR_FINAL_PHASE = Integer.parseInt(args.getFirst()),
                    "number of points"),
            new PromptCommand("no_requirements",
                    "Disables the requirement checks",
                    args -> ParametersServer.CHECK_REQUIREMENTS = false),
            new PromptCommand("no_timeout_reconnection",
                    "Disables the timeout for reconnection",
                    args -> ParametersServer.alonePlayerWin = false),
            new PromptCommand("no_pings",
                    "Disables the check of the clients' ping",
                    args -> ParametersServer.checkPings = false)
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
