package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.HelloController;
import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.view.View;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

public class ViewGui  extends Application implements View{
    private ViewGuiController viewGuiController;
    //private FXMLLoader fxmlLoader;

    public ViewGui() {
        //viewGuiController=null;
    }

    @Override
    public void start(Stage stage) throws Exception {
        File file = null;
        try {
            file = new File(HelloController.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String currentDirectory = file.getParent();
        File fatherFolder = new File(new File(currentDirectory).getParent());
        String path=fatherFolder.getPath()+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"it"+File.separator+"polimi"+File.separator+"ingsw"+File.separator+"am13"+File.separator+"ViewGuiRooms.fxml";
        System.out.println(path);
        FXMLLoader fxmlLoader = new FXMLLoader(ViewGui.class.getResource("ViewGuiRooms.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 920, 1080);
        stage.setTitle("Codex");
        stage.setScene(scene);
        stage.show();
        viewGuiController=fxmlLoader.getController();
        viewGuiController.setStage(stage);
    }


    @Override
    public void setNetworkHandler(NetworkHandler networkHandler) {
        viewGuiController.setNetworkHandler(networkHandler);
    }

    @Override
    public void showStartupScreen(boolean isSocket, String ip, int port) {
        viewGuiController.showStartupScreen(isSocket, ip, port);
    }

    @Override
    public void showException(Exception e) {

    }

    @Override
    public void showGenericLogMessage(String msg) {

    }

    @Override
    public void showRooms(List<RoomIF> rooms) {
        viewGuiController.showRooms(rooms);
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
    public void showStartGameReconnected(GameState state) {

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
}
