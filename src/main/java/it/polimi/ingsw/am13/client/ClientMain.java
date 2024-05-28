package it.polimi.ingsw.am13.client;

import it.polimi.ingsw.am13.ServerMain;
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

    // TODO: Create a common constant class for these
    public static final int  SOCKET_DEFAULT_PORT = 25566;
    public static final int RMI_DEFAULT_PORT = 25567;

    public static final String  SOCKET_DEFAULT_IP = "localhost";
    public static final String RMI_DEFAULT_IP = "localhost";

//    public static final String LOBBY_RMI_NAME = "lobby_rmi";


    public static void main(String[] args) {

        // Parse args given

        boolean isSocket = true;
        boolean isTUI = true;
        String ip = null;
        Integer port = null;


        List<String> argsList = List.of(args);
        if(argsList.contains("--gui"))
            isTUI = false;
        if(argsList.contains("--rmi"))
            isSocket = false;

        for(int i=0 ; i<args.length ; i++) {
            if(Objects.equals(args[i], "--ip"))
                ip = args[i+1];
            else if(Objects.equals(args[i], "--port"))
                port = Integer.parseInt(args[i+1]);
        }
        if(ip == null)
            ip = (isSocket ? SOCKET_DEFAULT_IP : RMI_DEFAULT_IP);
        if(port == null)
            port = (isSocket ? SOCKET_DEFAULT_PORT : RMI_DEFAULT_PORT);

        // check that ip and port are valid
        InetAddress ipInet;
        try {
            ipInet = new InetSocketAddress(ip, port).getAddress();
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
        if (isTUI){
            view = new ViewTUI();
            networkHandler = initConnection(isSocket, ip, port, view);
            // Sets the network handler for the view, in order to allow it to send commands/messages to the server
            view.setNetworkHandler(networkHandler);
            view.showStartupScreen(isSocket, ip, port);
            networkHandler.getRooms();
        }
        else{
            Application.launch(ViewGUI.class, Boolean.toString(isSocket), ip, port.toString());
        }

    }

    public static NetworkHandler initConnection(boolean isSocket, String ip, int port, View view){
        NetworkHandler networkHandler;
        if(isSocket) {
            // open socket connection with ip:port
            System.out.println("[Client] Connecting to server " + ip + ":" + port);
            Socket socket;
            try {
                socket = new Socket(ip, port);
            } catch (IOException e) {
                System.out.println("[Error] Something went wrong while trying to reach server at " + ip + ":" + port);
                throw new RuntimeException(e);
            }
            System.out.println("[Client] Connected to server");

            networkHandler = new NetworkHandlerSocket(socket, view);
        } else {
            Registry registry;
            try {
                //TODO: rivedi meglio questo setup x rmi
                registry = LocateRegistry.getRegistry(ip, ServerMain.RMI_DEFAULT_PORT);
                LobbyRMIIF lobby = (LobbyRMIIF) registry.lookup(ServerMain.LOBBY_RMI_NAME);
                networkHandler = new NetworkHandlerRMI(lobby, view);
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        }
        return networkHandler;
    }
}
