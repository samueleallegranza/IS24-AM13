package it.polimi.ingsw.am13.network.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class listens for new socket connection requests coming from the client.
 * Whenever a new connection is accepted, it starts a new {@link ClientRequestsHandler} thread
 */
public class ServerSocketHandler {
    private final int socket_port;

    private ServerSocket serverSocket;

    public ServerSocketHandler(int socket_port) {
        this.socket_port = socket_port;
    }

    public void start() throws IOException {
        // open server socket and start listening for new connection requests
        this.serverSocket = new ServerSocket(this.socket_port);
        System.out.println("[Socket] Opened public port " + this.socket_port + ".");

        Thread serverSocketThread = new Thread() {
            public void run() {
                Socket newClientSocket;

                System.out.println("[Socket] Started listening for new client connection requests.");

                while(true) {
                    // listen for a new connection request to the server
                    try{
                        newClientSocket = serverSocket.accept();
                        System.out.println("[Socket] Got new client connection request.");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // start a new thread to handle the new client socket
                    new ClientRequestsHandler(newClientSocket).start();
                }
            }
        };
        serverSocketThread.start();

    }

}
