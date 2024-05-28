package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.ClientMain;
import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.view.View;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ViewGUI extends Application implements View {

    public static final boolean DEBUG_MODE = true;
    public static final int DEBUG_NPLAYERS = 2;

    private ViewGUIController viewGUIController;
    private NetworkHandler networkHandler;
    private List<RoomIF> rooms;

    private GameState state;
    private PlayerLobby player;
    private Stage stage;
    private final static int sceneWidth=1820;
    private final static int sceneHeight=980;
    private final static boolean FULLSCREEN_MODE = false;

    @Override
    public void start(Stage stage) throws Exception {
        boolean isSocket = Boolean.parseBoolean(getParameters().getUnnamed().get(0));
        String ip = getParameters().getUnnamed().get(1);
        int port = Integer.parseInt(getParameters().getUnnamed().get(2));

        this.stage=stage;
        player=null;
        networkHandler = ClientMain.initConnection(isSocket, ip, port, this);

        FXMLLoader fxmlLoader = new FXMLLoader(ViewGUI.class.getResource("ViewGUIRooms.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), sceneWidth, sceneHeight);
        stage.setTitle("Codex");
        stage.setScene(scene);

        // full-screen mode
        if(FULLSCREEN_MODE)
            // real full-screen mode
            stage.setFullScreen(true);
        else {
            // windowed full-screen mode (for testing purposes)
            javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
        }

        stage.show();

        viewGUIController = fxmlLoader.getController();
        viewGUIController.setStage(stage);

        setNetworkHandler(networkHandler);

        showStartupScreen(true, "localhost", 25566);
        networkHandler.getRooms();
    }


    @Override
    public void setNetworkHandler(NetworkHandler networkHandler) {
        viewGUIController.setNetworkHandler(networkHandler);
    }

    @Override
    public void showStartupScreen(boolean isSocket, String ip, int port) {
        viewGUIController.showStartupScreen(isSocket, ip, port);
    }

    @Override
    public void showException(Exception e) {
        Platform.runLater(() -> {
            viewGUIController.showException(e);
        });
    }

    @Override
    public void showGenericLogMessage(String msg) {

    }

    @Override
    public void showRooms(List<RoomIF> rooms) {
        this.rooms = rooms;
//        boolean found=false;
        for(RoomIF room : rooms)
            if(room.getPlayers().contains(player)) {
//                found=true;
                viewGUIController.setRoom(room);
            }
        //if(found)
        viewGUIController.showRooms(rooms);
    }

    @Override
    public void showPlayerJoinedRoom(PlayerLobby player) {
        Platform.runLater(() -> {
            if (this.player == null) {
                this.player = player;
                FXMLLoader fxmlLoader = new FXMLLoader(ViewGUI.class.getResource("ViewGUIJoinedRoom.fxml"));
                Scene scene;
                try {
                    scene = new Scene(fxmlLoader.load(), sceneWidth, sceneHeight);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                viewGUIController = fxmlLoader.getController();
                viewGUIController.setStage(stage);
                setNetworkHandler(networkHandler);
                networkHandler.getRooms();

                stage.setTitle("Codex");
                stage.setScene(scene);
                stage.show();
            }
            else
                viewGUIController.showPlayerJoinedRoom(player);
        });

    }
    @Override
    public void showPlayerLeftRoom(PlayerLobby player) {
        if(this.player.equals(player)){
            Platform.runLater(() -> {
                FXMLLoader fxmlLoader = new FXMLLoader(ViewGUI.class.getResource("ViewGUIRooms.fxml"));
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load(), sceneWidth, sceneHeight);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                stage.setTitle("Codex");
                stage.setScene(scene);
                stage.show();

                viewGUIController = fxmlLoader.getController();
                viewGUIController.setStage(stage);
                viewGUIController.setGameState(state);
                setNetworkHandler(networkHandler);

                showStartupScreen(true, "localhost", 25566);
                this.player=null;
                networkHandler.getRooms();
            });

        }
        else
            networkHandler.getRooms();
    }

    @Override
    public void showStartGame(GameState state) {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(ViewGUI.class.getResource("ViewGUIInit.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load(), sceneWidth, sceneHeight);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setTitle("Choose a side of the starter card");
            stage.setScene(scene);
            stage.show();

            this.state=state;

            viewGUIController = fxmlLoader.getController();
            viewGUIController.setStage(stage);
            setNetworkHandler(networkHandler);
            viewGUIController.setThisPlayer(player);
            viewGUIController.setGameState(state);
            viewGUIController.showStartGame(state);
        });
    }

    @Override
    public void showStartGameReconnected(GameState state, PlayerLobby thisPlayer) {

    }

    @Override
    public void showPlayedStarter(PlayerLobby player) {
        if(this.player.equals(player)) {
            Platform.runLater(() -> {
                stage.setTitle("Choose your personal objective");
                stage.show();
                viewGUIController.showPlayedStarter(player);
            });
        }
        else {
            viewGUIController.showPlayedStarter(player);
        }
    }

    @Override
    public void showChosenPersonalObjective(PlayerLobby player) {
        viewGUIController.showChosenPersonalObjective(player);
    }

    @Override
    public void showInGame() {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(ViewGUI.class.getResource("ViewGUIMatch.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load(), sceneWidth, sceneHeight);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setTitle("Turn based phase");
            stage.setScene(scene);
            stage.show();

            viewGUIController = fxmlLoader.getController();
            viewGUIController.setStage(stage);
            viewGUIController.setThisPlayer(player);
            viewGUIController.setGameState(state);
            setNetworkHandler(networkHandler);
            viewGUIController.showInGame();
        });
    }

    @Override
    public void showPlayedCard(PlayerLobby player, Coordinates coord) {
        viewGUIController.showPlayedCard(player,coord);
    }

    @Override
    public void showPickedCard(PlayerLobby player) {
        viewGUIController.showPickedCard(player);
    }

    @Override
    public void showNextTurn() {
        viewGUIController.showNextTurn();
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
