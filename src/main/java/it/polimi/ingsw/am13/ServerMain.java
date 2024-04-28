package it.polimi.ingsw.am13;

import it.polimi.ingsw.am13.network.socket.ServerSocketHandler;

import java.io.IOException;

public class ServerMain {
    private static int  SOCKET_DEFAULT_PORT = 25566;
    private static int RMI_DEFAULT_PORT = 25567;
    private int socket_port;
    private int rmi_port;

    public static void main(String[] args) {
        // default ports for socket & RMI
        int SOCKET_DEFAULT_PORT = 25566;
        int RMI_DEFAULT_PORT = 25567;

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


    }
}
