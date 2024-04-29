package it.polimi.ingsw.am13.network.socket;

import it.polimi.ingsw.am13.network.socket.message.command.MessageCommand;
import it.polimi.ingsw.am13.network.socket.message.command.MessageGetRooms;

import java.io.*;
import java.net.Socket;

public class ClientRequestsHandler extends Thread {
    Socket clientSocket;

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
        PrintWriter out = null;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            new GameListenerServerSocket(out, null, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TODO: Reimplement with real messages. This is just a test!
        while(true) {
            while (true) {
                // read a message from client
                String message;
                try {
                    if ((message = in.readLine()) == null) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.printf("[Socket][Client:%d] Message from client: %s\n", clientSocket.getPort(), message);

                // send a test response message
                String jsonMsg = new MessageCommand("sneaky_peaky", new MessageGetRooms("xyz", 123)).toJson();

                // Write the JSON string to the socket's output stream
                out.println(jsonMsg);
            }
        }
    }
}
