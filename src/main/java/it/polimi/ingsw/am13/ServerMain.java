package it.polimi.ingsw.am13;

import it.polimi.ingsw.am13.network.rmi.LobbyRMI;
import it.polimi.ingsw.am13.network.socket.ServerSocketHandler;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Objects;

public class ServerMain {

    public static void main(String[] args) {

        // ### 1. Check if arguments passed are valid (will replace default ports and default ip)
        if(List.of(args).contains("--help")) {
            System.out.println(generateHelp());
            System.exit(0);
        }
        for(int i=0 ; i<args.length ; i++) {
            if(Objects.equals(args[i], "--ip"))
                ParametersServer.SERVER_IP = args[i+1];
            else if(Objects.equals(args[i], "--rmi"))
                ParametersServer.RMI_PORT = Integer.parseInt(args[i+1]);
            else if(Objects.equals(args[i], "--socket"))
                ParametersServer.SOCKET_PORT = Integer.parseInt(args[i+1]);
            else if(Objects.equals(args[i], "--points"))
                ParametersServer.POINTS_FOR_FINAL_PHASE = Integer.parseInt(args[i+1]);
            else if(Objects.equals(args[i], "--no_requirements"))
                ParametersServer.CHECK_REQUIREMENTS = false;
            else if(Objects.equals(args[i], "--no_timeout_reconnection"))
                ParametersServer.alonePlayerWin = false;

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
        return """
                Help for game Codex Naturalis (server)
                Accepted commands:
                \t--ip <ip>:\t\t\t\t\tIp address of the server, to which the clients must connect
                \t--rmi <rmi port>:\t\t\tSets the rmi port number
                \t--socket <socket port>:\t\tSets the socket port number
                \t--points <point>\t\t\tSets the number of points to reach in order to trigger the final phase (20 by default, as in the rule book)
                \t--no_requirements:\t\t\tDisables the check for the requirements for the objective card (not possible for the rule book)
                \t--no_timeout_reconnection:\t\t\tDisables the check to make a player remained alone win
                """;
    }
}
