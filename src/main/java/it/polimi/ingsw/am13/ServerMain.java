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

    // TODO: Create a common constant class for these
    public static final String SERVER_DEFAULT_IP = "localhost";
    public static final int  SOCKET_DEFAULT_PORT = 25566;
    public static final int RMI_DEFAULT_PORT = 25567;
    public static final String LOBBY_RMI_NAME = "lobby_rmi";

    public static void main(String[] args) {

        int socket_port = -1;
        int rmi_port = -1;
        String server_ip = null;

        // ### 1. Check if arguments passed are valid (will replace default ports and default ip)
        for(int i=0 ; i<args.length ; i++) {
            if(Objects.equals(args[i], "--ip"))
                server_ip = args[i+1];
            else if(Objects.equals(args[i], "--rmi"))
                rmi_port = Integer.parseInt(args[i+1]);
            else if(Objects.equals(args[i], "--socket"))
                socket_port = Integer.parseInt(args[i+1]);
        }
        // set default ports/ip if none are given as args.
        if(socket_port == -1)
            socket_port = SOCKET_DEFAULT_PORT;
        if(rmi_port == -1)
            rmi_port = RMI_DEFAULT_PORT;
        if(server_ip == null)
            server_ip = SERVER_DEFAULT_IP;

        System.out.println("Socket port\t\t\t: " + socket_port);
        System.out.println("RMI port\t\t\t: " + rmi_port);
        System.out.println("Server's IP address\t: " + server_ip);

        // ### 2. Instantiate Socket handler. Quit if port is invalid
        try {
            ServerSocketHandler serverSocket = new ServerSocketHandler(socket_port);
            serverSocket.start();
        } catch (IOException e) {
            throw new RuntimeException(e); // invalid port
        }

        // ### 3. Binding of RMI registry
       try {
            System.setProperty("java.rmi.server.hostname", server_ip);
            Registry registry = LocateRegistry.createRegistry(RMI_DEFAULT_PORT);
            registry.bind(LOBBY_RMI_NAME, new LobbyRMI());
        } catch (RemoteException e) {
            //TODO: capisci come gestire
            throw new RuntimeException(e);
        } catch (AlreadyBoundException e) { // Should never happen, unless ServerMain is run more than once
            throw new RuntimeException(e);
        }

    }
}
