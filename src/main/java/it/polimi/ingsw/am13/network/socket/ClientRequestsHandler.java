package it.polimi.ingsw.am13.network.socket;

import java.io.*;
import java.net.Socket;

public class ClientRequestsHandler extends Thread {
    private Socket clientSocket;
    private GameListenerServerSocket gameListenerServerSocket;

    public ClientRequestsHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        System.out.printf("[Socket][Client:%d] Started listening for new requests\n", clientSocket.getPort());

        // Prepare socket input buffer
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Prepare socket output buffer
        //FIXME: need to get the player from the client somehow
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            new GameListenerServerSocket(out, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TODO: Reimplement with real messages. This is just a test!
        while (true) {
        }
    }
}
