package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.HelloController;
import it.polimi.ingsw.am13.ServerMain;
import it.polimi.ingsw.am13.client.ClientMain;
import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.network.rmi.NetworkHandlerRMI;
import it.polimi.ingsw.am13.client.network.socket.NetworkHandlerSocket;
import it.polimi.ingsw.am13.client.view.View;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.network.rmi.LobbyRMI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

public class ViewGUI extends Application implements View{
    private ViewGUIController viewGUIController;
    NetworkHandler networkHandler;

    @Override
    public void start(Stage stage) throws Exception {
        boolean isSocket = Boolean.parseBoolean(getParameters().getUnnamed().get(0));
        String ip = getParameters().getUnnamed().get(1);
        int port = Integer.parseInt(getParameters().getUnnamed().get(2));

        networkHandler = ClientMain.initConnection(isSocket, ip, port, this);

        FXMLLoader fxmlLoader = new FXMLLoader(ViewGUI.class.getResource("ViewGuiRooms.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Codex");
        stage.setScene(scene);
        stage.show();

        viewGUIController = fxmlLoader.getController();
        viewGUIController.setStage(stage);

        viewGUIController.setNetworkHandler(networkHandler);

        showStartupScreen(true, "localhost", 25566);
        networkHandler.getRooms();
    }


    @Override
    public void setNetworkHandler(NetworkHandler networkHandler) {
        //viewGUIController.setNetworkHandler(networkHandler);
        //this.networkHandler = networkHandler;
        //launch();
    }

    @Override
    public void showStartupScreen(boolean isSocket, String ip, int port) {
        viewGUIController.showStartupScreen(isSocket, ip, port);
    }

    @Override
    public void showException(Exception e) {

    }

    @Override
    public void showGenericLogMessage(String msg) {

    }

    @Override
    public void showRooms(List<RoomIF> rooms) {
        viewGUIController.showRooms(rooms);
    }

    @Override
    public void showPlayerJoinedRoom(PlayerLobby player) {

    }

    @Override
    public void showPlayerLeftRoom(PlayerLobby player) {

    }

    @Override
    public void showStartGame(GameState state) {

    }

    @Override
    public void showStartGameReconnected(GameState state, PlayerLobby thisPlayer) {

    }

    @Override
    public void showPlayedStarter(PlayerLobby player) {

    }

    @Override
    public void showChosenPersonalObjective(PlayerLobby player) {

    }

    @Override
    public void showInGame() {

    }

    @Override
    public void showPlayedCard(PlayerLobby player, Coordinates coord) {

    }

    @Override
    public void showPickedCard(PlayerLobby player) {

    }

    @Override
    public void showNextTurn() {

    }

    @Override
    public void showFinalPhase() {

    }

    @Override
    public void showUpdatePoints() {

    }

    @Override
    public void showWinner() {

    }

    @Override
    public void showEndGame() {

    }

    @Override
    public void showPlayerDisconnected(PlayerLobby player) {

    }

    @Override
    public void showPlayerReconnected(PlayerLobby player) {

    }

    public static void main(String[] args) {
        launch();
    }
}
