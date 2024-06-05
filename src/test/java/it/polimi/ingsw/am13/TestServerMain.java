package it.polimi.ingsw.am13;

import it.polimi.ingsw.am13.network.socket.message.command.MsgCommand;
import it.polimi.ingsw.am13.network.socket.message.command.MsgCommandChat;
import it.polimi.ingsw.am13.network.socket.message.command.MsgCommandGetRooms;
import it.polimi.ingsw.am13.network.socket.message.command.MsgCommandPing;
import it.polimi.ingsw.am13.network.socket.message.response.MsgResponse;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

public class TestServerMain {

    //TODO: da rivedere (lo faila)

//    @Test
//    void testSocket() throws InterruptedException {
//        String serverAddress = "localhost";
//        int port = 25566;
//
//        Thread.sleep(500);
//
//        try {
//            // Create a socket connection to the server
//            Socket socket = new Socket(serverAddress, port);
//
//            // Connection successful
//            System.out.println("Connected to server at " + serverAddress + ":" + port);
//
//            // Prepare socket input buffer
//            ObjectInputStream in;
//            try {
//                in = new ObjectInputStream(socket.getInputStream());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            // Prepare socket output buffer
//            ObjectOutputStream out;
//            try {
//                out = new ObjectOutputStream(socket.getOutputStream());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            // send hello message to server
//            MsgCommandGetRooms msgCommandGetRooms=new MsgCommandGetRooms();
//            out.writeObject(msgCommandGetRooms);
//            out.flush();
//            out.reset();
//
//            System.out.println("AA");
//            // read the response from server
//            MsgResponse msgResponse;
//            try {
//                msgResponse = (MsgResponse) in.readObject();
//                System.out.println("Message from client: " + msgResponse.getType());
////                message = in.readLine();
////                System.out.println("Message from client: " + message);
//            } catch (IOException | ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//
//            // Close the socket when done
//            socket.close();
//
//        } catch (IOException e) {
//            // Handle connection errors
//            System.err.println("Error: Could not connect to server at " + serverAddress + ":" + port);
//            throw new RuntimeException(e);
//        }
//    }
}
