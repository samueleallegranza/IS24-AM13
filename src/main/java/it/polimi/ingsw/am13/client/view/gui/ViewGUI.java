package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.ClientMain;
import it.polimi.ingsw.am13.client.chat.Chat;
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

/**
 * This class is the JavaFX application, and it implements the View interface.
 * It initializes the stage when it is called by ClientMain.
 * Then, every time a show method is called, it changes the scene by using the corresponding fxml file if it is
 * necessary, and it calls the corresponding method on the controller.
 */
public class ViewGUI extends Application implements View {

    private final static boolean FULLSCREEN_MODE = false;
    private final static int sceneWidth = 1920;
    private final static int sceneHeight = 1080;

    /**
     * Controller of the 'Rooms' scene, where the rooms are listed and the user can create/join/reconnect to a room
     */
    private ViewGUIControllerRooms roomsController;
    /**
     * Controller of 'JoinedRoom' scene, where player can wait for the room to get full or can leave the room
     */
    private ViewGUIControllerJoinedRoom joinedRoomController;
    /**
     * Controller of 'Init' scene, where the player must complete the initialization part of the game
     */
    private ViewGUIControllerInit initController;
    /**
     * Controller of 'Match' scene, where player can actually play the most of the game
     */
    private ViewGUIControllerMatch matchController;
    /**
     * Controller of 'Winner' scene, where the final points and the winner are shown
     */
    private ViewGUIControllerWinner winnerController;

    /**
     * Controller currently active
     */
    private ViewGUIController currentController;

    /**
     * The stage on which the application is displayed
     */
    private Stage stage;

    /**
     * The network handler which is used to communicate with the server
     */
    private NetworkHandler networkHandler;

    /**
     * The representation of the state of the game
     */
    private GameState state;
    /**
     * The player corresponding to a specific instance of {@link ViewGUI}.
     * It is initialized as null, set whenever a room is joined or created, and set to null whenever they leave
     * a room.
     */
    private PlayerLobby thisPlayer;

    /**
     * This variable if the client is connected via socket, false otherwise
     */
    private boolean isSocket;

    /**
     * IP address of the server
     */
    private String ip;
    /**
     * Port of the server
     */
    private int port;

    /**
     * This method is executed when this application is launched by {@link ClientMain}.
     * It sets the stage, and it loads the first scene, which is the one where players can see, join, create, reconnect
     * to rooms. It also calls the getRooms method on the networkHandler.
     * @param stage the stage of this application (which will never be changed throughout the execution)
     * @throws Exception if loading the scene fails
     */
    @Override
    public void start(Stage stage) throws Exception {
        isSocket = Boolean.parseBoolean(getParameters().getUnnamed().get(0));
        ip = getParameters().getUnnamed().get(1);
        port = Integer.parseInt(getParameters().getUnnamed().get(2));

        this.stage = stage;
        thisPlayer = null;
        networkHandler = ClientMain.initConnection(this);

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

        // Creation/initialization of all the scenes that will be used
        roomsController = createScene(ViewGUIControllerRooms.class, "ViewGUIRooms.fxml");

        showStartupScreen(isSocket, ip, port);
        networkHandler.getRooms();

        // Handle window close request
        stage.setOnCloseRequest(event -> {
            System.out.println("Game closed");
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Utility function to load the FXML file for a scene of the game and load its controller
     * @param controllerClass Class of the controller associated to the scene
     * @param fxmlName Name of the FXML file for the scene
     * @return Controller loaded to handle the scene
     * @param <T> Class type of the controller, specified as parameter
     * @throws IOException ???
     */
    private <T extends ViewGUIController> T createScene(Class<T> controllerClass, String fxmlName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ViewGUI.class.getResource(fxmlName));
        Scene scene = new Scene(fxmlLoader.load(), sceneWidth, sceneHeight);
        T controller = fxmlLoader.getController();
        controller.setStage(stage);
        controller.setScene(scene);
        controller.setNetworkHandler(networkHandler);
        return controller;
    }

    /**
     * Switches a scene, showing another scene based on the associated controller.
     * It effectively shows the scene, sets the scene title, and sets the parameters <code>thisPlayer</code> and <code>state</code>
     * The current controller is updated, too.
     * @param controller Controller associated to the scene to switch to
     */
    private void switchToScene(ViewGUIController controller) {
        controller.switchToScene();
        controller.setThisPlayer(thisPlayer);
        controller.setGameState(state);
        this.currentController = controller;
    }

    /**
     * Calls the corresponding method on viewGUIController
     * @param networkHandler Handler of the network
     */
    @Override
    public void setNetworkHandler(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }

    /**
     * It calls the corresponding method on the controller.
     * @param isSocket true if the connection is via socket
     * @param ip of the connection
     * @param port of the connection
     */
    @Override
    public synchronized void showStartupScreen(boolean isSocket, String ip, int port) {
        thisPlayer = null;
        state = null;
        try {
            joinedRoomController = createScene(ViewGUIControllerJoinedRoom.class, "ViewGUIJoinedRoom.fxml");
            initController = createScene(ViewGUIControllerInit.class, "ViewGUIInit.fxml");
            matchController = createScene(ViewGUIControllerMatch.class, "ViewGUIMatch.fxml");
            winnerController = createScene(ViewGUIControllerWinner.class, "ViewGUIWinner.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        switchToScene(roomsController);
        roomsController.showStartupScreen();
    }

    /**
     * Shows a generic exception in the view (specific exception could be handled in different
     * ways, also depending on the phase or the state of game)
     * @param e Exception to be shown
     */
    @Override
    public synchronized void showException(Exception e) {
        Platform.runLater(() -> currentController.showException(e));
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
        if(thisPlayer == null) {
            roomsController.showRooms(rooms);
        } else {
            // This should be the response after invocation of getRooms() for thisPlayer joining a room or
            // one of the players in that room leaving it
            for(RoomIF room : rooms)
                if(room.getPlayers().contains(thisPlayer)) {
                    joinedRoomController.setRoom(room);
                    break;
                }
            joinedRoomController.showRooms(rooms);
        }
    }

    /**
     * If thisPlayer is null, it sets it, changes the scene to the joined room scene and calls getRooms.
     * Otherwise, it calls the corresponding method in viewGUIController.
     * @param player Player who joined the room
     */
    @Override
    public synchronized void showPlayerJoinedRoom(PlayerLobby player) {
        Platform.runLater(() -> {
            if (thisPlayer == null) {
                // The update is my ACK for command joinRoom --> I switch to joinedRoom view
                thisPlayer = player;

                switchToScene(joinedRoomController);
                networkHandler.getRooms();
            }
            else
                joinedRoomController.showPlayerJoinedRoom(player);
        });

    }

    /**
     * If the player who left the room is thisPlayer, it switches the scene to the rooms' scene.
     * Otherwise, it calls the getRooms method so that the rooms are updated.
     * @param player Player who left the room
     */
    @Override
    public synchronized void showPlayerLeftRoom(PlayerLobby player) {
        if(player.equals(thisPlayer)) {
            Platform.runLater(() -> {
                switchToScene(roomsController);
                showStartupScreen(isSocket, ip, port);
                thisPlayer = null;
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
    public synchronized void showStartGame(GameState state, Chat chat) {
        this.state = state;
        Platform.runLater(() -> {
            switchToScene(matchController);
            matchController.init(initController, winnerController, chat);
            matchController.showStartGame();
        });
    }

    /**
     * It sets state and thisPlayer.
     * Then, depending on the {@link GameStatus}, it calls the appropriate show method.
     * @param state GameState of the started match
     * @param thisPlayer Player linked to this client which is reconnecting to the match
     */
    @Override
    public synchronized void showStartGameReconnected(GameState state, PlayerLobby thisPlayer, Chat chat) {
        // set ViewGUI base attributes
        this.state = state;
        this.thisPlayer = thisPlayer;

        try {
            if(state.getGameStatus() == GameStatus.INIT) {
                showStartGame(state,chat); // sets the init visualization screen
                showChosenPersonalObjective(thisPlayer); // instantly skips to the waiting page
            } else if(state.getGameStatus()==GameStatus.IN_GAME || state.getGameStatus()==GameStatus.FINAL_PHASE) {
                showStartGame(state,chat);
                Thread.sleep(200);
                showInGame();
            } else if(state.getGameStatus()==GameStatus.ENDED) {
                showStartGame(state,chat);
                Thread.sleep(200);
                matchController.showUpdatePoints();
                matchController.showWinner();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * It displays that a player chose the side for his starter card, by calling the corresponding method
     * on {@link ViewGUIController}
     * @param player Player who played their starter card
     */
    @Override
    public synchronized void showPlayedStarter(PlayerLobby player) {
        Platform.runLater(() -> matchController.showPlayedStarter(player));
    }

    /**
     * It displays that a player chose his personal objective, by calling the corresponding method
     * on {@link ViewGUIController}
     * @param player Player who chose their personal objective card
     */
    @Override
    public synchronized void showChosenPersonalObjective(PlayerLobby player) {
        Platform.runLater(() -> matchController.showChosenPersonalObjective(player));
    }

    /**
     * It shows the game entering the turn-based phase, by changing the scene and calling the corresponding method
     * on {@link ViewGUIController}
     */
    @Override
    public synchronized void showInGame() {
        Platform.runLater(() -> matchController.showInGame());
    }

    /**
     * Shows a player placing one of their hand cards on the field, by calling the corresponding method on
     * {@link ViewGUIController}
     * @param player Player who placed the card on field
     * @param coord Coordinates of the field where the card has been placed
     */
    @Override
    public synchronized void showPlayedCard(PlayerLobby player, Coordinates coord) {
        matchController.showPlayedCard(player,coord);
    }

    /**
     * It shows a player picking a card, by calling the corresponding method on {@link ViewGUIController}
     * @param player Player who picked a card
     */
    @Override
    public synchronized void showPickedCard(PlayerLobby player) {
        matchController.showPickedCard(player);
    }

    /**
     * It shows the game moving on to the next turn by calling the corresponding method on {@link ViewGUIController}
     */
    @Override
    public synchronized void showNextTurn() {
        matchController.showNextTurn();
    }

    @Override
    public synchronized void showFinalPhase() {
        matchController.showFinalPhase();
    }

    /**
     * It shows the updated points after the turn-based phase is finished by calling the corresponding method on {@link ViewGUIController}
     */
    @Override
    public synchronized void showUpdatePoints() {
            matchController.showUpdatePoints();
    }

    /**
     * It shows the winner by calling the corresponding method on {@link ViewGUIController}.
     * It also calls showUpdatePoints() if it has not already been called (which might happen if thisPlayer
     * won because he was the only one connected for the defined time).
     */
    @Override
    public synchronized void showWinner() {
        matchController.showWinner();
    }

    /**
     * It calls the corresponding method on {@link ViewGUIController}.
     */
    @Override
    public synchronized void showEndGame() {
        matchController.showEndGame();
    }

    /**
     * It shows that a player disconnected from the game by calling the corresponding method on {@link ViewGUIController}.
     * @param player Player who disconnected
     */
    @Override
    public synchronized void showPlayerDisconnected(PlayerLobby player) {
        currentController.showPlayerDisconnected(player);
    }

    /**
     * It shows that a player reconnected to the game by calling the corresponding method on {@link ViewGUIController}.
     * @param player Player who reconnected
     */
    @Override
    public synchronized void showPlayerReconnected(PlayerLobby player) {
        currentController.showPlayerReconnected(player);
    }

    /**
     * Shows a chat message
     *
     * @param sender    of the message
     * @param receivers of the message
     */
    @Override
    public void showChatMessage(PlayerLobby sender, List<PlayerLobby> receivers) {
        matchController.showChatMessage(sender,receivers);
    }

    /**
     * Force closing the app. It should be used to end the app for anomalous reasons
     */
    @Override
    public void forceCloseApp() {
        currentController.forceCloseApp();
    }

    public static void main(String[] args) {
        launch();
    }
}
