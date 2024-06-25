package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Abstract class for the GUI controllers (the classes implementing the logic behind the various scenes)
 */
public abstract class ViewGUIController {

    /**
     * The stage on which the application is displayed
     */
    protected Stage stage;
    /**
     * The scene that this controller manages
     */
    protected Scene scene;

    /**
     * The network handler which is used to communicate with the server
     */
    protected NetworkHandler networkHandler;

    /**
     *
     * @return the scene that this controller manages
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Method that sets the stage
     * @param stage on which the application is displayed
     */
    public void setStage(Stage stage){
        this.stage = stage;
    }

    /**
     * Method that sets the scene
     * @param scene the scene that this controller manages
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    /**
     * Method that sets the network handler
     * @param networkHandler the network handler which is used to communicate with the server
     */
    public void setNetworkHandler(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }

    /**
     * Method that sets the player
     * @param player the player associated to this {@link ViewGUIController}
     */
    public abstract void setThisPlayer(PlayerLobby player);

    /**
     * Method that sets the gameState
     * @param state the representation of the state of the game
     */
    public abstract void setGameState(GameState state);

    /**
     * Shows the scene associated to this controller, setting also the scene title
     */
    public void switchToScene() {
        Platform.runLater(() -> {
            stage.setTitle(getSceneTitle());
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am13/client/view/gui/style/img/codex-logo.png"))));
            stage.setScene(scene);
            stage.show();
        });
    }


    /**
     *
     * @return the title of the scene
     */
    public abstract String getSceneTitle();
    /**
     * Shows a generic exception in the view (specific exception could be handled in different
     * ways, also depending on the phase or the state of game)
     * @param e Exception to be shown
     */
    public abstract void showException(Exception e);

    /**
     * It shows that a player disconnected from an ongoing game by calling the corresponding method on {@link ViewGUIController}.
     * @param player Player who disconnected
     */
    public abstract void showPlayerDisconnected(PlayerLobby player);

    /**
     * It shows that a player reconnected to an ongoing game by calling the corresponding method on {@link ViewGUIController}.
     * @param player Player who reconnected
     */
    public abstract void showPlayerReconnected(PlayerLobby player);

    /**
     * Force closing the app. It should be used to end the app for anomalous reasons
     */
    public abstract void forceCloseApp();
}
