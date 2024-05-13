package it.polimi.ingsw.am13;

import it.polimi.ingsw.am13.network.rmi.LobbyRMI;
import it.polimi.ingsw.am13.network.socket.ServerSocketHandler;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {

    // TODO: Create a common constant class for these
    public static final int  SOCKET_DEFAULT_PORT = 25566;
    public static final int RMI_DEFAULT_PORT = 25567;
    public static final String LOBBY_RMI_NAME = "lobby_rmi";

    public static void main(String[] args) {

        int socket_port;
        int rmi_port;

        // ### 1. Check if arguments passed are valid (will replace default ports)
        if(args.length == 2) {
            try {
                socket_port = Integer.parseInt(args[0]);
                rmi_port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid custom ports");
            }
        } else {
            // set default ports if none are given as args.
            socket_port = SOCKET_DEFAULT_PORT;
            rmi_port = RMI_DEFAULT_PORT;
        }

        System.out.println("Socket port : " + socket_port);
        System.out.println("RMI port    : " + rmi_port);

        // ### 2. Instantiate Socket handler. Quit if port is invalid
        try {
            ServerSocketHandler serverSocket = new ServerSocketHandler(socket_port);
            serverSocket.start();
        } catch (IOException e) {
            throw new RuntimeException(e); // invalid port
        }

        // ### 3. Binding of RMI registry
        try {
            Registry registry = LocateRegistry.getRegistry(RMI_DEFAULT_PORT);
            //TODO: capisci come prendere/creare il registro RMI su quella porta
            registry.bind(LOBBY_RMI_NAME, new LobbyRMI());
        } catch (RemoteException e) {
            //TODO: capisci come gestire
            throw new RuntimeException(e);
        } catch (AlreadyBoundException e) { // Should never happen, unless ServerMain is run more than once
            throw new RuntimeException(e);
        }

    }
}
