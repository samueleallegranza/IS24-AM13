package it.polimi.ingsw.am13;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TestServerMain {
    @Test
    public void testSocket() throws InterruptedException {
        String serverAddress = "localhost";
        int port = 25566;

        Thread.sleep(500);

        try {
            // Create a socket connection to the server
            Socket socket = new Socket(serverAddress, port);

            // Connection successful
            System.out.println("Connected to server at " + serverAddress + ":" + port);

            // Prepare socket input buffer
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Prepare socket output buffer
            PrintWriter out = null;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // send hello message to server
            String hiMsg = "Hello";
            out.println(hiMsg);

            // read the response from server
            String message;
            try {
                message = in.readLine();
                System.out.println("Message from client: " + message);
                message = in.readLine();
                System.out.println("Message from client: " + message);
//                message = in.readLine();
//                System.out.println("Message from client: " + message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Close the socket when done
            socket.close();

        } catch (IOException e) {
            // Handle connection errors
            System.err.println("Error: Could not connect to server at " + serverAddress + ":" + port);
            e.printStackTrace();
        }
    }
}
