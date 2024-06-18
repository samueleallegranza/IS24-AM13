package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Abstract class for the GUI controllers (the classes implementing the logic behind the various scenes)
 */
public abstract class ViewGUIController {

    protected Stage stage;
    protected Scene scene;
    protected NetworkHandler networkHandler;

    public Scene getScene() {
        return scene;
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }
    public void setScene(Scene scene) {
        this.scene = scene;
    }
    public void setNetworkHandler(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }

    public abstract void setThisPlayer(PlayerLobby player);
    public abstract void setGameState(GameState state);

    /**
     * Shows the scene associated to this controller, setting also the scene title
     */
    public void switchToScene() {
        Platform.runLater(() -> {
            stage.setTitle(getSceneTitle());
            stage.setScene(scene);
            stage.show();
        });
    }



    public abstract String getSceneTitle();
    public abstract void showException(Exception e);
    public abstract void showPlayerDisconnected(PlayerLobby player);
    public abstract void showPlayerReconnected(PlayerLobby player);

}
