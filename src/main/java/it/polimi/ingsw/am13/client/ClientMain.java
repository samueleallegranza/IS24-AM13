package it.polimi.ingsw.am13.client;

import it.polimi.ingsw.am13.client.network.socket.NetworkHandlerSocket;

import java.io.IOException;
import java.net.*;
import java.util.Objects;

public class ClientMain {
    public static void main(String[] args) throws InterruptedException {
        String serverAddress = "localhost";
        int port = 25566;
        Boolean isSocket=true; //if true, use socket; otherwise, use RMI (they will be modified in the following code)
        Boolean isTui=true; //if true, use TUI; otherwise use GUI

        Thread.sleep(500); //todo a cosa serve?\
        //TODO se il parametro è help stampare i parametri richiesti
        if(args.length == 2) {
            isSocket=(args[0].equals("socket"));
            isTui=(args[1].equals("tui"));
        }
        if(isSocket){
            try {
                // Create a socket connection to the server
                Socket socket = new Socket(serverAddress, port);

                // Connection successful
                System.out.println("Connected to server at " + serverAddress + ":" + port);
                new NetworkHandlerSocket(socket);

            } catch (IOException e) {
                // Handle connection errors
                System.err.println("Error: Could not connect to server at " + serverAddress + ":" + port);
                e.printStackTrace();
            }
        }
    }
}
