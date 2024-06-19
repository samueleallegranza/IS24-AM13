package it.polimi.ingsw.am13.client;

import it.polimi.ingsw.am13.ParametersClient;
import it.polimi.ingsw.am13.ParametersServer;
import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.network.rmi.NetworkHandlerRMI;
import it.polimi.ingsw.am13.client.network.socket.NetworkHandlerSocket;
import it.polimi.ingsw.am13.client.view.View;
import it.polimi.ingsw.am13.client.view.gui.ViewGUI;
import it.polimi.ingsw.am13.client.view.tui.ViewTUI;
import it.polimi.ingsw.am13.network.rmi.LobbyRMIIF;
import javafx.application.Application;

import java.io.IOException;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Objects;

public class ClientMain {

    public static void main(String[] args) {

        List<String> argsList = List.of(args);
        if(argsList.contains("--tui"))
            ParametersClient.IS_TUI = true;
        if(argsList.contains("--rmi"))
            ParametersClient.IS_SOCKET = false;
        if(argsList.contains("--skip_init"))
            ParametersClient.SKIP_INIT = true;
        if(argsList.contains("--skip_turns"))
            ParametersClient.SKIP_TURNS = true;
        if(argsList.contains("--no_sounds"))
            ParametersClient.SOUND_ENABLE = false;


        for(int i=0 ; i<args.length ; i++) {
            if(Objects.equals(args[i], "--ip"))
                ParametersClient.SERVER_IP = args[i+1];
            else if(Objects.equals(args[i], "--port"))
                ParametersClient.SERVER_PORT = Integer.parseInt(args[i+1]);
            else if(Objects.equals(args[i], "--skip_room")) {
                ParametersClient.SKIP_ROOM = true;
                ParametersClient.DEBUG_NPLAYERS = Integer.parseInt(args[i + 1]);
            }
        }
        ParametersClient.checkServerPort();

        // check that ip and port are valid
        InetAddress ipInet;
        try {
            ipInet = new InetSocketAddress(ParametersClient.SERVER_IP, ParametersClient.SERVER_PORT).getAddress();
        } catch (Exception e) {
            System.out.println("[Error] Given ip or port not valid, please try again.");
            return;
        }
        if(ipInet == null) {
            System.out.println("[Error] Given ip or port not valid, please try again.");
            return;
        }

        View view;
        NetworkHandler networkHandler;
        if (ParametersClient.IS_TUI) {
            view = new ViewTUI();
            networkHandler = initConnection(view);
            // Sets the network handler for the view, in order to allow it to send commands/messages to the server
            view.setNetworkHandler(networkHandler);
            view.showStartupScreen(ParametersClient.IS_SOCKET, ParametersClient.SERVER_IP, ParametersClient.SERVER_PORT);
            networkHandler.getRooms();
        }
        else{
            Application.launch(ViewGUI.class, Boolean.toString(ParametersClient.IS_SOCKET),
                    ParametersClient.SERVER_IP, Integer.toString(ParametersClient.SERVER_PORT));
        }
    }

    public static NetworkHandler initConnection(View view) {
        NetworkHandler networkHandler;
        if(ParametersClient.IS_SOCKET) {
            // open socket connection with ip:port
            System.out.println("[Client] Connecting to server " + ParametersClient.SERVER_IP + ":" + ParametersClient.SERVER_PORT);
            Socket socket;
            try {
                socket = new Socket(ParametersClient.SERVER_IP, ParametersClient.SERVER_PORT);
            } catch (IOException e) {
                System.out.println("[Error] Something went wrong while trying to reach server at " + ParametersClient.SERVER_IP + ":" + ParametersClient.SERVER_PORT);
                throw new RuntimeException(e);
            }
            System.out.println("[Client] Connected to server");

            networkHandler = new NetworkHandlerSocket(socket, view);
        } else {
            Registry registry;
            try {
                //TODO: rivedi meglio questo setup x rmi
                registry = LocateRegistry.getRegistry(ParametersClient.SERVER_IP, ParametersServer.RMI_PORT);
                LobbyRMIIF lobby = (LobbyRMIIF) registry.lookup(ParametersServer.LOBBY_RMI_NAME);
                networkHandler = new NetworkHandlerRMI(lobby, view);
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        }
        return networkHandler;
    }
}
