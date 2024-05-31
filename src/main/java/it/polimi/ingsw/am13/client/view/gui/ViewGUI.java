package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.ClientMain;
import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.view.View;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.GameStatus;
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

//todo nota per Matteo: quando toglierai la gerarchia dei controller, andranno anche tolte tutte le parti in cui si aggiorna il controller (e vari setter saranno da chiamare una volta sola)

/**
 * This class is the JavaFX application, and it implements the View interface.
 * It initializes the stage when it is called by ClientMain.
 * Then, every time a show method is called, it changes the scene by using the corresponding fxml file if it is
 * necessary, and it calls the corresponding method on the controller.
 */
public class ViewGUI extends Application implements View {

    /**
     * If true, execute in debug mode
     */
    public static final boolean DEBUG_MODE = true;
    /**
     * Number of players for the debug mode
     */
    public static final int DEBUG_NPLAYERS = 3;

    private ViewGUIController viewGUIController;
    private NetworkHandler networkHandler;

    private GameState state;
    /**
     * The player corresponding to a specific instance of {@link ViewGUI}.
     * It is initialized as null, set whenever a room is joined or created, and set to null whenever they leave
     * a room.
     */
    private PlayerLobby thisPlayer;
    private Stage stage;

    private boolean reachedWinnerPhase;
    private final static int sceneWidth=1820;
    private final static int sceneHeight=980;
    private final static boolean FULLSCREEN_MODE = false;

    /**
     * This method is executed when this application is launched by {@link ClientMain}.
     * It sets the stage, and it loads the first scene, which is the one where players can see, join, create, reconnect
     * to rooms. It also calls the getRooms method on the networkHandler.
     * @param stage the stage of this application (which will never be changed throughout the execution)
     * @throws Exception if loading the scene fails
     */
    @Override
    public void start(Stage stage) throws Exception {
        boolean isSocket = Boolean.parseBoolean(getParameters().getUnnamed().get(0));
        String ip = getParameters().getUnnamed().get(1);
        int port = Integer.parseInt(getParameters().getUnnamed().get(2));

        this.stage=stage;
        thisPlayer =null;
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

        showStartupScreen(true, ip, port);
        networkHandler.getRooms();
        reachedWinnerPhase=false;
    }

    /**
     * Calls the corresponding method on viewGUIController
     * @param networkHandler Handler of the network
     */
    @Override
    public void setNetworkHandler(NetworkHandler networkHandler) {
        viewGUIController.setNetworkHandler(networkHandler);
    }

    /**
     * It calls the corresponding method on the controller.
     * @param isSocket true if the connection is via socket
     * @param ip of the connection
     * @param port of the connection
     */
    @Override
    public synchronized void showStartupScreen(boolean isSocket, String ip, int port) {
        viewGUIController.showStartupScreen(isSocket, ip, port);
    }

    /**
     * Shows a generic exception in the view (specific exception could be handled in different
     * ways, also depending on the phase or the state of game)
     * @param e Exception to be shown
     */
    @Override
    public synchronized void showException(Exception e) {
        Platform.runLater(() -> viewGUIController.showException(e));
    }

    /**
     *
     * @param msg Message to be shown
     */
    @Override
    public synchronized void showGenericLogMessage(String msg) {

    }

    /**
     * Shows the list of rooms returned by the server, which the player can join/reconnect to.
     * It is called every time a player creates, leaves or joins a room, in order to keep the view up to date.
     * If it is called after this player has joined a room, it is used to initialise the table of players inside
     * the room with this player and all the other players who joined the room before.
     * @param rooms List of rooms
     */
    @Override
    public synchronized void showRooms(List<RoomIF> rooms) {
        for(RoomIF room : rooms)
            if(room.getPlayers().contains(thisPlayer))
                viewGUIController.setRoom(room);
        viewGUIController.showRooms(rooms);
    }

    /**
     * If thisPlayer is null, it sets it, changes the scene to the joined room scene and calls getRooms.
     * Otherwise, it calls the corresponding method in viewGUIController.
     * @param player Player who joined the room
     */
    @Override
    public synchronized void showPlayerJoinedRoom(PlayerLobby player) {
        Platform.runLater(() -> {
            if (this.thisPlayer == null) {
                this.thisPlayer = player;
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

    /**
     * If the player who left the room is thisPlayer, it switches the scene to the rooms' scene.
     * Otherwise, it calls the getRooms method so that the rooms are updated.
     * @param player Player who left the room
     */
    @Override
    public synchronized void showPlayerLeftRoom(PlayerLobby player) {
        if(this.thisPlayer.equals(player)){
            Platform.runLater(() -> {
                FXMLLoader fxmlLoader = new FXMLLoader(ViewGUI.class.getResource("ViewGUIRooms.fxml"));
                Scene scene;
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
                this.thisPlayer =null;
                networkHandler.getRooms();
            });

        }
        else
            networkHandler.getRooms();
    }

    /**
     * It switches the scene to the init scene, where each player can choose the side of his starter card and his
     * personal objective.
     * Then it calls the corresponding method in the {@link ViewGUIController}.
     * @param state Reference to the game's state which is kept up to date
     */
    @Override
    public synchronized void showStartGame(GameState state) {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(ViewGUI.class.getResource("ViewGUIInit.fxml"));
            Scene scene;
            try {
                scene = new Scene(fxmlLoader.load(), sceneWidth, sceneHeight);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setTitle("Initial phase");
            stage.setScene(scene);
            stage.show();

            this.state=state;

            viewGUIController = fxmlLoader.getController();
            viewGUIController.setStage(stage);
            setNetworkHandler(networkHandler);
            viewGUIController.setThisPlayer(thisPlayer);
            viewGUIController.setGameState(state);
            viewGUIController.showStartGame(state);
        });
    }

    /**
     * It sets state and thisPlayer.
     * Then, depending on the {@link GameStatus}, it calls the appropriate show method.
     * @param state GameState of the started match
     * @param thisPlayer Player linked to this client which is reconnecting to the match
     */
    //todo pensare meglio a come gestire le eccezioni per reconnect, al momento se clicco reconnect e il player non era in una room prima dà eccezione il server, il server si blocca e il client non vede nulla
    @Override
    public synchronized void showStartGameReconnected(GameState state, PlayerLobby thisPlayer) {
        // set ViewGUI base attributes
        this.state = state;
        this.thisPlayer = thisPlayer;

        switch (state.getGameStatus()) {
            case GameStatus.INIT -> {
                showStartGame(state); // sets the init visualization screen
                showChosenPersonalObjective(thisPlayer); // instantly skips to the waiting page
            }
            case GameStatus.IN_GAME -> {
                showInGame();
            }
        }
    }


    /**
     * It displays that a player chose the side for his starter card, by calling the corresponding method
     * on {@link ViewGUIController}
     * @param player Player who played their starter card
     */
    @Override
    public synchronized void showPlayedStarter(PlayerLobby player) {
        Platform.runLater(() -> viewGUIController.showPlayedStarter(player));
    }

    /**
     * It displays that a player chose his personal objective, by calling the corresponding method
     * on {@link ViewGUIController}
     * @param player Player who chose their personal objective card
     */
    @Override
    public synchronized void showChosenPersonalObjective(PlayerLobby player) {
        viewGUIController.showChosenPersonalObjective(player);
    }

    /**
     * It shows the game entering the turn-based phase, by changing the scene and calling the corresponding method
     * on {@link ViewGUIController}
     */
    @Override
    public synchronized void showInGame() {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(ViewGUI.class.getResource("ViewGUIMatch.fxml"));
            Scene scene;
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
            viewGUIController.setThisPlayer(thisPlayer);
            viewGUIController.setGameState(state);
            setNetworkHandler(networkHandler);
            viewGUIController.showInGame();
        });
    }

    /**
     * Shows a player placing one of their hand cards on the field, by calling the corresponding method on
     * {@link ViewGUIController}
     * @param player Player who placed the card on field
     * @param coord Coordinates of the field where the card has been placed
     */
    @Override
    public synchronized void showPlayedCard(PlayerLobby player, Coordinates coord) {
        viewGUIController.showPlayedCard(player,coord);
    }

    /**
     * It shows a player picking a card, by calling the corresponding method on {@link ViewGUIController}
     * @param player Player who picked a card
     */
    @Override
    public synchronized void showPickedCard(PlayerLobby player) {
        viewGUIController.showPickedCard(player);
    }

    /**
     * It shows the game moving on to the next turn by calling the corresponding method on {@link ViewGUIController}
     */
    @Override
    public synchronized void showNextTurn() {
        viewGUIController.showNextTurn();
    }

    @Override
    public synchronized void showFinalPhase() {
        viewGUIController.showFinalPhase();
    }

    /**
     * It shows the updated points after the turn-based phase is finished by calling the corresponding method on {@link ViewGUIController}
     */
    @Override
    public synchronized void showUpdatePoints() {

        Platform.runLater(() -> {
            reachedWinnerPhase=true;
            FXMLLoader fxmlLoader = new FXMLLoader(ViewGUI.class.getResource("ViewGUIWinner.fxml"));
            Scene scene;
            try {
                scene = new Scene(fxmlLoader.load(), sceneWidth, sceneHeight);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setTitle("Winner phase");
            stage.setScene(scene);
            stage.show();

            viewGUIController = fxmlLoader.getController();
            viewGUIController.setStage(stage);
            viewGUIController.setThisPlayer(thisPlayer);
            viewGUIController.setGameState(state);
            setNetworkHandler(networkHandler);
            viewGUIController.showUpdatePoints();
        });
    }

    /**
     * It shows the winner by calling the corresponding method on {@link ViewGUIController}.
     * It also calls showUpdatePoints() if it has not already been called (which might happen if thisPlayer
     * won because he was the only one connected for the defined time).
     */
    @Override
    public synchronized void showWinner() {
        if(!reachedWinnerPhase) {
            //todo forse si potrebbe visualizzare in qualche modo che ha vinto perché è l'unico giocatore rimasto
            //(si dovrebbe entrare in questo if solo in quel caso)
            showUpdatePoints();
        }
        Platform.runLater(() -> viewGUIController.showWinner());
    }

    /**
     * It calls the corresponding method on {@link ViewGUIController}.
     */
    //todo potremmo voler stampare qualche messaggio (al momento in controller non viene fatto niente)
    @Override
    public synchronized void showEndGame() {
        viewGUIController.showEndGame();
    }

    /**
     * It shows that a player disconnected from the game by calling the corresponding method on {@link ViewGUIController}.
     * @param player Player who disconnected
     */
    @Override
    public synchronized void showPlayerDisconnected(PlayerLobby player) {
        viewGUIController.showPlayerDisconnected(player);
    }

    /**
     * It shows that a player reconnected to the game by calling the corresponding method on {@link ViewGUIController}.
     * @param player Player who reconnected
     */
    @Override
    public synchronized void showPlayerReconnected(PlayerLobby player) {
        viewGUIController.showPlayerReconnected(player);
    }


    public static void main(String[] args) {
        launch();
    }
}
