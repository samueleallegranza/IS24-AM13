package it.polimi.ingsw.am13.client;

import it.polimi.ingsw.am13.ServerMain;
import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.network.rmi.NetworkHandlerRMI;
import it.polimi.ingsw.am13.client.network.socket.NetworkHandlerSocket;
import it.polimi.ingsw.am13.network.rmi.LobbyRMI;

import java.io.IOException;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain {
    public static void main(String[] args) throws InterruptedException {
        String serverAddress = "localhost";         //TODO parametrizza serverAddress e port!!!
        int port = 25566;
        boolean isSocket = true; //if true, use socket; otherwise, use RMI (they will be modified in the following code)
        boolean isTui = true; //if true, use TUI; otherwise use GUI

        Thread.sleep(500); //todo a cosa serve?\
        //TODO se il parametro è help stampare i parametri richiesti
        if(args.length == 2) {
            isSocket=(args[0].equals("socket"));
            isTui=(args[1].equals("tui"));
        }

        NetworkHandler networkHandler;
        if(isSocket){
            try {
                // Create a socket connection to the server
                Socket socket = new Socket(serverAddress, port);

                // Connection successful
                System.out.println("Connected to server at " + serverAddress + ":" + port);
                networkHandler = new NetworkHandlerSocket(socket);

            } catch (IOException e) {
                // Handle connection errors
                System.err.println("Error: Could not connect (socket) to server at " + serverAddress + ":" + port);
                throw new RuntimeException(e);
            }
        } else {    // RMI Connection
            Registry registry;
            try {
                registry = LocateRegistry.getRegistry(ServerMain.RMI_DEFAULT_PORT);
                LobbyRMI lobby = (LobbyRMI) registry.lookup(ServerMain.LOBBY_RMI_NAME);
                networkHandler = new NetworkHandlerRMI(lobby);
            } catch (RemoteException | NotBoundException e) {
                System.err.println("Error: Could not connect (rmi) to server at " + serverAddress + ":" + port);
                throw new RuntimeException(e);
            }
        }

        //TODO: Come continuare? fai qualcosa con network handle forse...
    }
}
