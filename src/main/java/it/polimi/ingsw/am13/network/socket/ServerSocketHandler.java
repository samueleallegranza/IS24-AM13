package it.polimi.ingsw.am13.network.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class listens for new socket connection requests coming from the client.
 * Whenever a new connection is accepted, it starts a new {@link ClientRequestsHandler} thread
 */
public class ServerSocketHandler {
    /**
     * The socket port of the server
     */
    private final int socket_port;

    /**
     * The server socket of this server
     */
    private ServerSocket serverSocket;

    /**
     * Assign the value to the socket port attribute
     * @param socket_port the socket port of the server
     */
    public ServerSocketHandler(int socket_port) {
        this.socket_port = socket_port;
    }

    /**
     * Initialise and start the threat that listens for new connection requests
     * @throws IOException if an I/ O error occurs when opening the socket
     */
    public void start() throws IOException {
        // open server socket and start listening for new connection requests
        this.serverSocket = new ServerSocket(this.socket_port);
        System.out.println("[Socket] Opened public port " + this.socket_port + ".");

        Thread serverSocketThread = new Thread(() -> {
            Socket newClientSocket;

            System.out.println("[Socket] Started listening for new client connection requests.");

            while(true) {
                // listen for a new connection request to the server
                try{
                    newClientSocket = serverSocket.accept();
                    newClientSocket.setSoTimeout(14000000);
                    System.out.println("[Socket] Got new client connection request.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // start a new thread to handle the new client socket
                new ClientRequestsHandler(newClientSocket).start();
            }
        });
        serverSocketThread.start();

    }

}
