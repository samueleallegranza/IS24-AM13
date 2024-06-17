package it.polimi.ingsw.am13;

import it.polimi.ingsw.am13.network.rmi.LobbyRMI;
import it.polimi.ingsw.am13.network.socket.ServerSocketHandler;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;

public class ServerMain {

    public static void main(String[] args) {

        // ### 1. Check if arguments passed are valid (will replace default ports and default ip)
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
}
