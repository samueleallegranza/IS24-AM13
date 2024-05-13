package it.polimi.ingsw.am13.client;

import it.polimi.ingsw.am13.ServerMain;
import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.network.rmi.NetworkHandlerRMI;
import it.polimi.ingsw.am13.client.network.socket.NetworkHandlerSocket;
import it.polimi.ingsw.am13.client.view.View;
import it.polimi.ingsw.am13.client.view.tui.ViewTUI;
import it.polimi.ingsw.am13.client.view.tui.ViewTUIHandler;
import it.polimi.ingsw.am13.network.rmi.LobbyRMI;

import java.io.IOException;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;

public class ClientMain {

    // TODO: Create a common constant class for these
    public static final int  SOCKET_DEFAULT_PORT = 25566;
    public static final int RMI_DEFAULT_PORT = 25567;

    public static final String  SOCKET_DEFAULT_IP = "localhost";
    public static final String RMI_DEFAULT_IP = "localhost";

    public static final String LOBBY_RMI_NAME = "lobby_rmi";


    public static void main(String[] args) {

        // Parse args given

        boolean isSocket = true;
        boolean isTUI = true;
        String ip = null;
        Integer port = null;

        // Step 1: check TUI or GUI {--tui, --gui}
        if(args.length < 1 || (!Objects.equals(args[0], "--tui") && !Objects.equals(args[0], "--gui"))) {
            System.out.println("[Error] Specify 1st argument --tui for TUI or --gui for GUI.");
            return;
        } else if(args[0] == "--tui") {
            isTUI = true;
            // TODO: Start with TUI
        } else if(args[0] == "--gui") {
            isTUI = false;
            // TODO: Start with GUI
        }

        // Step 2: check socket or rmi {--socket, --rmi}
        if(args.length < 2 || (!Objects.equals(args[1], "--socket") && !Objects.equals(args[1], "--rmi"))) {
            System.out.println("[Error] Specify 2nd argument --socket for socket connection or --rmi for RMI connection.");
            return;
        } else if(args[1] == "--socket") {
            isSocket = true;
            // TODO: Start with Socket
        } else if(args[1] == "--rmi") {
            isSocket = false;
            // TODO: Start with RMI
        }

        // Step 3: check ip (will be for socket or rmi based on user selection in step 2)
        //         if not given, we will assume 'localhost' ip
        int arg_port = 2;
        if(args.length >= 4 && Objects.equals(args[2], "--ip")) {
            // user passed custom ip
            ip = args[3];
            arg_port = 4;
        } else {
            ip = (isSocket ? SOCKET_DEFAULT_IP : RMI_DEFAULT_IP);
        }

        // Step 4: check port (will be for socket or rmi based on user selection in step 2)
        //         if not given, we will assume default port for rmi/socket
        if(args.length >= arg_port+2 && Objects.equals(args[arg_port], "--port")) {
            // user passed custom port
            port = Integer.parseInt(args[arg_port+1]);
        } else {
            port = (isSocket ? SOCKET_DEFAULT_PORT : RMI_DEFAULT_PORT);
        }


        // check that ip and port are valid
        InetAddress ipInet = null;
        try {
            ipInet = new InetSocketAddress(ip, port).getAddress();
        } catch (Exception e) {
            System.out.println("[Error] Given ip or port not valid, please try again.");
            return;
        }

        // start view (TUI/GUI)
        View view = null;
        if(isTUI) {
            view = new ViewTUIHandler();
        } else {
            // TODO: Implement GUI
        }
        assert(view) != null; // FIXME: Remove when GUI will be implemented

        view.showStartupScreen(isSocket, isSocket, ip, port);

        // open connection with Server via RMI or Socket
        if(isSocket) {
            // open socket connection with ip:port
            System.out.println("[Client] Connecting to server " + ip + ":" + port);
            Socket socket = null;
            try {
                socket = new Socket(ipInet, port);
            } catch (IOException e) {
                System.out.println("[Error] Something went wrong while trying to reach server at " + ip + ":" + port);
                return;
            }
            System.out.println("[Client] Connected to server");
        } else {
            // open RMI connection with ip:port
            // TODO: Implement Server connection with RMI here
        }

    }
    public static void oldmain(String[] args) throws InterruptedException {
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
                networkHandler = new NetworkHandlerRMI();
            } catch (RemoteException | NotBoundException e) {
                System.err.println("Error: Could not connect (rmi) to server at " + serverAddress + ":" + port);
                throw new RuntimeException(e);
            }
        }

        //TODO: Come continuare? fai qualcosa con network handle forse...
    }
}
