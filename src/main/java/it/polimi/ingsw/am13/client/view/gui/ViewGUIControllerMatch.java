package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.ParametersClient;
import it.polimi.ingsw.am13.client.chat.Chat;
import it.polimi.ingsw.am13.client.chat.ChatMessage;
import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.model.GameStatus;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.RequirementsNotMetException;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;

/**
 * Controller of 'Match' scene, where player can actually play the most of the game
 */
public class ViewGUIControllerMatch extends ViewGUIController {

    // ----------------------------------------------------------------
    // UPPER HALF OF THE SCREEN
    // ----------------------------------------------------------------

    /**
     * Container for the labels displaying the players in game (to the left of the screen)
     */
    @FXML
    private GridPane playersContainer;
    /**
     * {@link ScrollPane} containing the field.
     * For the scroll to work, the fieldContainer contained must be bigger than this container
     */
    @FXML
    private ScrollPane fieldScrollPane;
    /**
     * Container where to stack the cards to display for the player's field
     * {@link ImageView} and {@link Rectangle} for card placed and available coords are added to this container for the field
     */
    @FXML
    private StackPane fieldContainer;
    /**
     * Label displaying the current game action to perform
     */
    @FXML
    private Label actionLabel;
    /**
     * Label displaying the name of the player whose field is currently being displayed
     */
    @FXML
    private Label displayPlayerLabel;
    /**
     * Label for counter visible during final phase, displaying the number of turns left to the end of the game
     */
    @FXML
    private Label turnsCounterLabel;
    /**
     * Background image ofr counter of turns to the end of the game
     */
    @FXML
    private ImageView turnsCounterBackground;

    /**
     * Label for counter of plant resources in the field of displayPlayer
     */
    @FXML
    private Label plantCounterLabel;
    /**
     * Label for counter of animal resources in the field of displayPlayer
     */
    @FXML
    private Label animalCounterLabel;
    /**
     * Label for counter of fungus resources in the field of displayPlayer
     */
    @FXML
    private Label fungusCounterLabel;
    /**
     * Label for counter of manuscript resources in the field of displayPlayer
     */
    @FXML
    private Label manuscriptCounterLabel;
    /**
     * Label for counter of insect resources in the field of displayPlayer
     */
    @FXML
    private Label insectCounterLabel;
    /**
     * Label for counter of quill resources in the field of displayPlayer
     */
    @FXML
    private Label quillCounterLabel;
    /**
     * Label for counter of inkwell resources in the field of displayPlayer
     */
    @FXML
    private Label inkwellCounterLabel;
    @FXML
    private ImageView scoreTrackerView;
    @FXML
    private StackPane scoreTrackerContainer;

    // ----------------------------------------------------------------
    // BOTTOM HALF OF THE SCREEN
    // ----------------------------------------------------------------

    @FXML
    private ImageView handCard0;
    @FXML
    private Button flipButton0;
    @FXML
    private ImageView handCard1;
    @FXML
    private Button flipButton1;
    @FXML
    private ImageView handCard2;
    @FXML
    private Button flipButton2;
    @FXML
    private ImageView handObjective;


    @FXML
    private Pane pickablesContainer;
    @FXML
    private ImageView resDeck;
    @FXML
    private ImageView resPick1;
    @FXML
    private ImageView resPick2;
    @FXML
    private ImageView gldDeck;
    @FXML
    private ImageView gldPick1;
    @FXML
    private ImageView gldPick2;
    @FXML
    private ImageView objDeck;
    @FXML
    private ImageView commonObj1;
    @FXML
    private ImageView commonObj2;


    //TAB PANE

    @FXML
    private ChoiceBox<List<PlayerLobby>> chatChoice;
    @FXML
    private TextArea chatArea;
    @FXML
    private TextField chatField;

    /**
     * Area of non-editable text for showing logs
     */
    @FXML
    private TextArea logArea;

    @FXML
    private TextArea guideArea;

    // ----------------------------------------------------------------
    // PRIVATE STATE VARIABLE FOR CONTROLLER'S LOGIC USE
    // ----------------------------------------------------------------


    //Chat (all the chat messages)
    private Chat chat;
    /**
     * Game's state. Information about state of game are uniquely taken from here
     */
    private GameState state;
    /**
     * Player associated to the client the GUI was created by
     */
    private PlayerLobby thisPlayer;
    /**
     * Player whose field and back of hand cards are currently being displayed
     */
    private PlayerLobby displayPlayer;
    /**
     * Handler of the logs
     */
    private LogGUI log;

    /**
     * The sides on which each of the hand cards are being displayed
     */
    private List<Side> handCardSides;
    /**
     * True if it is the turn of thisPlayer and he has played a card, false otherwise
     */
    private boolean flowCardPlaced;
    /**
     * The ImageView of the hand card thisPlayer has played, before he receives confirmation from the server
     * of the fact that the card was played successfully
     */
    private ImageView attemptedToPlayCardHand;
    /**
     * The button used to flip the hand card thisPlayer has played, before he receives confirmation from the server
     * of the fact that the card was played successfully
     */
    private Button attemptedToPlayFlipButton;
    /**
     * The ImageView of the card on the field that thisPlayer has played, before he receives confirmation from the server
     * of the fact that the card was played successfully
     */
    private ImageView attemptedToPlayCardField;
    /**
     * The card box in which thisPlayer has placed a card, before he receives confirmation from the server
     * of the fact that the card was played successfully
     */
    private Rectangle attemptedToPlayCardBox;
    /**
     * The playable cards in the hand of displayPlayer
     */
    private List<CardPlayableIF> handPlayable;
    /**
     * A map storing the playable cards in the hand of each player
     */
    private Map<PlayerLobby,List<CardPlayableIF>> playersHandsPlayable;
    /**
     * The images of the cards in the hand of displayPlayer
     */
    private List<ImageView> handCards;
    /**
     * The buttons to flip the hand cards of thisPlayer
     */
    private List<Button> flipButtons;
    /**
     * A map associating to the nickname of each player the VBox in which his information is being displayed
     */
    private Map<PlayerLobby, Node> playerNodes;
    /**
     * A map associating each resource to its Label
     */
    private Map<Resource, Label> counterLabels;

    /**
     * Overlay rectangle for the init view
     */
    private Rectangle initOverlay;
    /**
     * Listener for the main window's width used for the overlay in init and winner view
     */
    private ChangeListener<Number> overlayWidthListener;
    /**
     * Listener for the main window's height used for the overlay in init and winner view
     */
    private ChangeListener<Number> overlayHeightListener;


    /**
     * Map associating each player to its token image in the score tracker
     */
    private final Map<PlayerLobby, ImageView> tokenImgs = new HashMap<>();
    /**
     * Map associating each player to the current saved points, before a new eventual modification
     */
    private final Map<PlayerLobby, Integer> savedPoints = new HashMap<>();
    /**
     * Map associating each player to the current graphical x-offset for their token on the scroeboard,
     * with respect to the main coordinates of their points.
     * In fact, to show overlapped token, the image of a token could be placed with a slight offset
     */
    private final Map<PlayerLobby, Double> tokenOffsetCoordinates = new HashMap<>();
    /**
     * Flag indicating if the first scroll adjustment to center the starter card has already happened
     * Hence it is set to true each time the displayPlayer changes
     */
    private boolean firstFieldScrollAdjustment = true;

    /**
     * Last playing animations for the players' token. If the token of a player is not moving, the animation is null
     */
    private final Map<PlayerLobby, PathTransition> lastTokenAnimations = new HashMap<>();

    /**
     * Controller for the initialization view
     */
    private ViewGUIControllerInit controllerInit;
    /**
     * Controller for the winner view
     */
    private ViewGUIControllerWinner controllerWinner;
    /**
     * Main view handler of the client
     */
    private ViewGUI view;

    // ----------------------------------------------------------------
    // CONSTANTS
    // ----------------------------------------------------------------

    /**
     * Entire width of the image of a card
     */
    private static final int imageW = 150;
    /**
     * Entire height of the image of a card
     */
    private static final int imageH = 100;
    /**
     * Width of a visible corner of the image of a card
     */
    private static final int cornerX = 33;
    /**
     * Height of a visible corner of the image of a card
     */
    private static final int cornerY = 40;
    /**
     * Width/Height of the tokens visible on top of the starter card on the field
     */
    private static final int tokenDim = 30;

    /**
     * Relative coordinates for x-positions of tokens on the score tracker
     */
    private static final List<Double> xTranslToken = List.of(
            0.19375, 0.414583333333333, 0.6375, 0.75, 0.527083333333333, 0.304166666666667, 0.0833333333333333, 0.0833333333333333,
            0.304166666666667, 0.527083333333333, 0.75, 0.75, 0.527083333333333, 0.304166666666667, 0.0833333333333333, 0.0833333333333333,
            0.304166666666667, 0.527083333333333, 0.75, 0.747916666666667, 0.414583333333333, 0.0833333333333333, 0.0833333333333333,
            0.0833333333333333, 0.2125, 0.416666666666667, 0.620833333333333, 0.75, 0.75, 0.416666666666667
    );
    /**
     * Relative coordinates for y-positions of tokens on the score tracker
     */
    private static final List<Double> yTranslToken = List.of(
            -0.0386680988184748, -0.0386680988184748, -0.0386680988184748, -0.141783029001074, -0.141783029001074,
            -0.141783029001074, -0.141783029001074, -0.247046186895811, -0.247046186895811, -0.247046186895811, -0.247046186895811,
            -0.352309344790548, -0.352309344790548, -0.352309344790548, -0.352309344790548, -0.457572502685285, -0.457572502685285,
            -0.457572502685285, -0.457572502685285, -0.561761546723953, -0.613319011815252, -0.561761546723953, -0.66702470461869,
            -0.772287862513426, -0.857142857142857, -0.876476906552095, -0.857142857142857, -0.772287862513426, -0.66702470461869,
            -0.749731471535983
    );
    /**
     * Horizontal offset of overlapped tokens
     */
    private static final double TOKEN_X_OFFSET = 7;
    /**
     * Token dimension (square shape) relative to width (x) of the score tracker
     */
    private static final double tokenDimRel2x = 0.17083;

    /**
     * Only for debug: delay for doing actions in SKIP_TURNS mode
     */
    private static final long THINKING_TIME = 100;

    /**
     * Player of the sounds effects that are played when a card is played
     */
    private MediaPlayer playCardSoundPlayer;


    // ----------------------------------------------------------------
    //      CONTROLLER METHODS
    // ----------------------------------------------------------------

    /**
     * Initialization method, it must be called as first method before starting using the object.
     * It sets all the visual elements and the internal state information
     * @param controllerInit Controller for the initialization view. It has not to be already set
     * @param controllerWinner Controller for the winner view. It has not to be already set
     * @param chat Chat instance
     */
    public void init(ViewGUI view, ViewGUIControllerInit controllerInit, ViewGUIControllerWinner controllerWinner, Chat chat) {
        // First internal state initialization
        this.controllerInit = controllerInit;
        this.controllerWinner = controllerWinner;
        this.chat = chat;
        this.view = view;
        controllerInit.setGameState(state);
        controllerInit.setThisPlayer(thisPlayer);
        controllerWinner.setGameState(state);
        controllerWinner.setThisPlayer(thisPlayer);

        // Init of lists of graphical elements
        flipButtons = List.of(flipButton0, flipButton1, flipButton2);
        handCards = List.of(handCard0, handCard1, handCard2);
        counterLabels = Map.of(
                Resource.PLANT, plantCounterLabel,
                Resource.ANIMAL, animalCounterLabel,
                Resource.FUNGUS, fungusCounterLabel,
                Resource.INSECT, insectCounterLabel,
                Resource.QUILL, quillCounterLabel,
                Resource.INKWELL, inkwellCounterLabel,
                Resource.MANUSCRIPT, manuscriptCounterLabel
        );

        //init the chat
        setChat(this.chat);
        turnsCounterLabel.setVisible(false);
        turnsCounterBackground.setVisible(false);

        // Displaying the pickable cards and the common objectives
        // At the beginning of the game, the pickable cards shouldn't be clickable
        displayPickablesAndCommonObjs();
        pickablesContainer.setMouseTransparent(true);

        // Init of score tracker
        initScoreTracker();

        // Set size of fieldScrollPane and adjust the field whenever the size of the scroll pane changes
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        fieldScrollPane.setPrefHeight(screenBounds.getHeight() * 0.9);
        fieldScrollPane.widthProperty().addListener(
                (obs,oldVal,newVal)-> adjustFieldContainerSize()
        );
        fieldScrollPane.heightProperty().addListener(
                (obs,oldVal,newVal)-> adjustFieldContainerSize()
        );

        // Set the initial side of the hand cards to front
        handCardSides = new ArrayList<>(List.of(Side.SIDEFRONT, Side.SIDEFRONT, Side.SIDEFRONT));

        //Initialize playable hands of each player
        playersHandsPlayable=new HashMap<>();
        for(PlayerLobby player : state.getPlayers())
            playersHandsPlayable.put(player,new ArrayList<>((state.getPlayerState(player).getHandPlayable())));

        // First display of player's field and hand
        displayPlayer=null;
        switchToPlayer(thisPlayer);

        // Set the initial condition to 'no card placed', for flow of the game
        flowCardPlaced = false;

        // Init of players container
        playerNodes = new HashMap<>();
        initPlayerContainer();

        guideArea.setText("""
                > Drag a card in your hand to one of the blue boxes to play it
                > Click a card to draw it
                > Click on the banner a player to view its field""");

//        StackPane root=(StackPane) scene.getRoot();
//        root.widthProperty().addListener((obs,oldVal,newVal)->{
//            System.out.println(root.getScaleX()+" "+newVal+" "+oldVal);
//            root.getChildren().getFirst().setScaleX(root.getScaleX()*newVal.doubleValue()/oldVal.doubleValue());});
//        root.heightProperty().addListener((obs,oldVal,newVal)->{root.getChildren().get(1).setScaleY(root.getScaleY()*newVal.doubleValue()/oldVal.doubleValue());});
    }

    /**
     * Method that sets the player
     * @param thisPlayer the player associated to this {@link ViewGUIController}
     */
    @Override
    public void setThisPlayer(PlayerLobby thisPlayer) {
        this.thisPlayer = thisPlayer;
    }

    /**
     * Method that sets the gameState
     * @param gameState the representation of the state of the game
     */
    @Override
    public void setGameState(GameState gameState) {
        this.state=gameState;
        log = new LogGUI(gameState);
    }

    /**
     *
     * @return the title of the scene
     */
    @Override
    public String getSceneTitle() {
        return "Codex Naturalis - Match";
    }

    /**
     * Shows a generic exception in the view (specific exception could be handled in different
     * ways, also depending on the phase or the state of game)
     * @param e Exception to be shown
     */
    @Override
    public void showException(Exception e) {
        if(!ParametersClient.SKIP_TURNS) {
//            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("An error occurred");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            });

            // Sometimes we get a "player doesn't have this card exception", even if should be a req not met exception
            if (state.getCurrentPlayer()!=null && state.getCurrentPlayer().equals(thisPlayer) && !flowCardPlaced && attemptedToPlayCardHand != null) {
                Platform.runLater(() -> {
                    attemptedToPlayCardHand.setVisible(true);
                    attemptedToPlayFlipButton.setVisible(true);
                    fieldContainer.getChildren().remove(attemptedToPlayCardField);
                    fieldContainer.getChildren().add(attemptedToPlayCardBox);
                    attemptedToPlayCardBox.toBack();
                    for (int i = 0; i < handCards.size(); i++) {
                        int finalI = i;
                        handCards.get(i).setOnDragDetected(event -> {
                            Dragboard db = handCards.get(finalI).startDragAndDrop(TransferMode.ANY);
                            ClipboardContent content = new ClipboardContent();
                            content.putString(handCards.get(finalI).getId());
                            db.setContent(content);
                            event.consume();
                        });
                    }
                });
            }
        } else {
            if(e instanceof RequirementsNotMetException) {
                // I'm here if I tried to play, in debug mode, a gold card, but resulted in an exception
                // So I surrender for this turn and play a resource card
                CardPlayableIF playedCard = state.getPlayerState(thisPlayer).getHandPlayable()
                        .stream().filter(c -> c instanceof CardResource).findFirst().orElse(null);
                Coordinates coord = state.getPlayerState(thisPlayer).getField().getAvailableCoords().getFirst();
                if(playedCard != null)
                    networkHandler.playCard(playedCard, coord, Side.SIDEFRONT);
                else
                    networkHandler.playCard(state.getPlayerState(thisPlayer).getHandPlayable().getFirst(), coord, Side.SIDEBACK);
            }
        }
    }

    /**
     * It shows that a player disconnected from an ongoing game by updating the player container
     * @param player Player who disconnected
     */
    @Override
    public void showPlayerDisconnected(PlayerLobby player) {
        playerContainerUpdateConnection(player);
        if(state.countConnected() == 1) {
            Platform.runLater(()-> {
                updateActionLabel();
                for (ImageView handCard : handCards) {
                    handCard.setOnDragDetected(null);
                }
                pickablesContainer.setMouseTransparent(true);
            });
        }
        log.logDisconnect(player);
        showLastLogs();
    }

    /**
     * It shows that a player reconnected to an ongoing game by updating the player container
     * @param player Player who reconnected
     */
    @Override
    public void showPlayerReconnected(PlayerLobby player) {
        playerContainerUpdateConnection(player);
        if (displayPlayer.equals(thisPlayer) && thisPlayer.equals(state.getCurrentPlayer())) {
            Platform.runLater(() -> {
                if(flowCardPlaced){
                    pickablesContainer.setMouseTransparent(false);
                }
                else {
                    for (int i = 0; i < handCards.size(); i++) {
                        ImageView handCard = handCards.get(i);
                        makeDraggable(i, handCard, flipButtons.get(i));
                    }
                }
                updateActionLabel();
            });
        }

        log.logReconnect(player);
        showLastLogs();
    }

    /**
     * Force closing the app. It should be used to end the app for anomalous reasons
     */
    @Override
    public void forceCloseApp() {
        networkHandler.stopPing();
        Platform.runLater(() -> actionLabel.setText(">>ERROR<<"));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Platform.runLater(() -> stage.close());
    }

    /**
     * Show the start of the game by creating a semi transparent layer and displaying the init scene on top of it
     */
    public void showStartGame(){
        if(state.getGameStatus() == GameStatus.INIT) {
            StackPane topPane = (StackPane) this.getScene().getRoot();

            // Create the semi-transparent layer
            initOverlay = new Rectangle();
            initOverlay.setMouseTransparent(true);
            initOverlay.setOpacity(0.5);
            overlayWidthListener = (observable, oldValue, newValue) -> initOverlay.setWidth(newValue.doubleValue());
            overlayHeightListener = (observable, oldValue, newValue) -> initOverlay.setHeight(newValue.doubleValue());
            topPane.widthProperty().addListener(overlayWidthListener);
            topPane.heightProperty().addListener(overlayHeightListener);
            topPane.getChildren().addAll(initOverlay, controllerInit.getScene().getRoot());
            controllerInit.showStartGame();

            log.logStartGame();
            showLastLogs();
        }
    }

    /**
     * Show that player his starting card by calling the corresponding method on {@link ViewGUIControllerInit}, displaying in the field
     * , adding the corresponding log and displaying the logs
     * @param player who played his starter card
     */
    public void showPlayedStarter(PlayerLobby player){
        controllerInit.showPlayedStarter(player);
        if(displayPlayer.equals(player))
            displayField();
        log.logPlayedStarter(player);
        showLastLogs();
    }

    /**
     * Show that a player showed his personal objectiveby calling the corresponding method on {@link ViewGUIControllerInit}, adding the corresponding log
     * and displaying the logs
     * @param player who chose his personal objective
     */
    public void showChosenPersonalObjective(PlayerLobby player) {
        controllerInit.showChosenPersonalObjective(player);
        if(displayPlayer.equals(player))
            displayHandObjective();
        log.logChosenPersonalObjective(player);
        showLastLogs();
    }

    /**
     * Show that we reached the in game phase, by removed the init scene and the transparent overlay,
     * adding the log message, updating the hand playable, field and players container
     */
    public void showInGame() {
        StackPane stackPane = (StackPane) this.getScene().getRoot();
        stackPane.getChildren().remove(controllerInit.getScene().getRoot());
        stackPane.getChildren().remove(initOverlay);
        if(overlayWidthListener != null)
            stackPane.widthProperty().removeListener(overlayWidthListener);
        if(overlayHeightListener != null)
            stackPane.heightProperty().removeListener(overlayHeightListener);

        // First in game log (if phase is actually in game)
        if(state.getGameStatus() == GameStatus.IN_GAME) {
            log.logNextTurn();
            showLastLogs();
        }

        //display hand playable
        displayPlayer=null;
        switchToPlayer(thisPlayer);
        updateActionLabel();

        // init points to > 0 if in_game is called after a reconnection
        state.getPlayers().forEach(this::updateTokenPosition);

        playAudio("startGame.mp3");

        playersContainerUpdateTurns();

        if(ParametersClient.SKIP_TURNS && state.getCurrentPlayer().equals(thisPlayer)) {
            new Thread(() -> {
                try {
                    Thread.sleep(THINKING_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                networkHandler.playCard(state.getPlayerState(thisPlayer).getHandPlayable().getFirst(),
                        state.getPlayerState(thisPlayer).getField().getAvailableCoords().getFirst(),
                        Side.SIDEBACK);
            }).start();
        }
    }

    /**
     * If the player is {@link #thisPlayer}, set to true {@link #flowCardPlaced}, make the pickable cards
     * clickable and update {@link #actionLabel}.
     * Update {@link #playersHandsPlayable}.
     * If the player is {@link #displayPlayer}, display the field and the hand playable (to update them)
     * @param player who played the card
     * @param coord of the player where the card was played
     */
    public void showPlayedCard(PlayerLobby player, Coordinates coord) {
        int pointsBefore = savedPoints.get(player);
        if(thisPlayer.equals(player)) {
            flowCardPlaced = true;
            // After a card has been played, the pickable cards should be clickable
            pickablesContainer.setMouseTransparent(false);
            Platform.runLater(() -> actionLabel.setText("Play a card"));
        }
        for (int i = 0; i < playersHandsPlayable.get(player).size(); i++) {
            if(!state.getPlayerState(player).getHandPlayable().contains(playersHandsPlayable.get(player).get(i))) {
                playersHandsPlayable.get(player).set(i, null);
            }
        }
        if(player.equals(displayPlayer)){
            displayHandPlayable();
            displayField();
            if(ParametersClient.SOUND_ENABLE)
                Platform.runLater(()-> {
                    if(playCardSoundPlayer !=null) { //to avoid some exceptions on linux
                        playCardSoundPlayer.setStartTime(Duration.millis((1000 + playCardSoundPlayer.getStartTime().toMillis()) % 480000));
                        playCardSoundPlayer.setStopTime(Duration.millis(1000 + playCardSoundPlayer.getStartTime().toMillis()));
                        playCardSoundPlayer.play();
                    }
                });
        }

        updateTokenPosition(player);

        log.logPlayedCard(player, coord, pointsBefore < state.getPlayerState(player).getPoints());
        showLastLogs();
        updateActionLabel();

        if(ParametersClient.SKIP_TURNS && state.getCurrentPlayer().equals(thisPlayer)) {
            new Thread(() -> {
                try {
                    Thread.sleep(THINKING_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                List<CardPlayableIF> pickablesForSkip = new ArrayList<>(state.getPickables().stream().filter(Objects::nonNull).toList());
                Collections.shuffle(pickablesForSkip);
                if(!pickablesForSkip.isEmpty())
                    networkHandler.pickCard(pickablesForSkip.getFirst());       // I assume there is at least 1 card
            }).start();
        }
    }

    /**
     * Show that a card was picked by making the pickable cards not clickable, updating {@link #playersHandsPlayable}
     * and displaying the hand playable
     * @param player who picked the card
     */
    public void showPickedCard(PlayerLobby player) {
        if (player.equals(thisPlayer)){
            // After a card has been picked, the pickable cards should not be clickable
            pickablesContainer.setMouseTransparent(true);
        }
        List<CardPlayableIF> updatedHandPlayable = state.getPlayerState(player).getHandPlayable();
        CardPlayableIF pickedCard = null;
        for (CardPlayableIF card : updatedHandPlayable)
            if (!playersHandsPlayable.get(player).contains(card))
                pickedCard = card;
        for (int i = 0; i < playersHandsPlayable.get(player).size(); i++) {
            if (playersHandsPlayable.get(player).get(i) == null)
                playersHandsPlayable.get(player).set(i, pickedCard);
        }
        if(player.equals(displayPlayer))
            displayHandPlayable();
        displayPickablesAndCommonObjs();

        log.logPickedCard(player);
        updateActionLabel();
    }

    /**
     * Make all the updates necessary to make it the turn of the next player
     */
    public void showNextTurn() {
        if (displayPlayer.equals(thisPlayer) && state.getCurrentPlayer().equals(thisPlayer) && state.countConnected() > 1) {
            for (int i = 0; i < handCards.size(); i++) {
                    ImageView handCard = handCards.get(i);
                    makeDraggable(i, handCard, flipButtons.get(i));
            }
        }
        if(state.getCurrentPlayer().equals(thisPlayer)) {
            flowCardPlaced = false;
            playAudio("yourTurn.wav",0.1);
        }
        playersContainerUpdateTurns();

        log.logNextTurn();
        showLastLogs();
        updateActionLabel();
        if(state.getGameStatus() == GameStatus.FINAL_PHASE)
            Platform.runLater(() -> turnsCounterLabel.setText(String.format("-%d to end game", state.getTurnsToEnd())));

        if(ParametersClient.SKIP_TURNS && state.getCurrentPlayer().equals(thisPlayer)) {
            new Thread(() -> {
                try {
                    Thread.sleep(THINKING_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // I try to play a golden card, if present.
                // If ok, the game moves on. Otherwise, in showException i play a resourceCard
                CardPlayableIF playedCard = state.getPlayerState(thisPlayer).getHandPlayable().stream()
                        .filter(p -> p instanceof CardGold).findFirst().orElse(state.getPlayerState(thisPlayer).getHandPlayable().getFirst());
                networkHandler.playCard(playedCard,
                        state.getPlayerState(thisPlayer).getField().getAvailableCoords().getFirst(),
                        Side.SIDEFRONT);
            }).start();
        }
    }

    /**
     * Sho the logs, update the {@link #turnsCounterLabel}
     */
    public synchronized void showFinalPhase() {
        log.logFinalPhase();
        showLastLogs();
        Platform.runLater(() -> {
            // I repeat this line here, not only in showNextTurn, so that i'm sure this is done
            turnsCounterLabel.setText(String.format("-%d to end game", state.getTurnsToEnd()));
            turnsCounterBackground.setVisible(true);
            turnsCounterLabel.setVisible(true);

            playTransitionAppearLeftRight(turnsCounterBackground, turnsCounterBackground.getFitWidth());
            playTransitionAppearLeftRight(turnsCounterLabel, turnsCounterLabel.getWidth());
        });
    }

    // ----------------------------------------------------------------
    //      OVERLAYER FOR WINNER
    // ----------------------------------------------------------------

    /**
     * Modifies all the elements on the view in order to update them with the final value and not to allow any other modifications.
     */
    public synchronized void showUpdatePoints() {
        log.logUpdatePoints(savedPoints);
        showLastLogs();
        updateActionLabel();
        playersContainerUpdateTurns();
        handCards.forEach(c -> c.setOnDragDetected(null));

        turnsCounterLabel.setVisible(false);

        state.getPlayers().forEach(this::updateTokenPosition);
    }

    /**
     * Creates a new layer over all the view, which closes after the first click of the mouse
     */
    public synchronized void showWinner() {
        log.logWinner();
        showLastLogs();

        Platform.runLater(() -> {
            controllerWinner.showWinner();

            StackPane mainRoot = (StackPane) this.getScene().getRoot();

            // Create the semi-transparent layer
            Rectangle overlay = new Rectangle(mainRoot.getWidth(), mainRoot.getHeight(), Color.rgb(0, 0, 0, 0.5));
            overlay.setMouseTransparent(true);  // Allow clicks to pass through to the underlying elements

            // Create a StackPane to hold both the overlay and the table
            StackPane overlayPane = new StackPane(overlay, controllerWinner.getScene().getRoot());
            overlayPane.setAlignment(Pos.CENTER);
            overlayPane.setBackground(Background.EMPTY);
            overlayPane.setPrefSize(mainRoot.getWidth(), mainRoot.getHeight());

            overlayWidthListener = (observable, oldValue, newValue) -> {
                overlayPane.setPrefWidth(newValue.doubleValue());
                overlay.setWidth(newValue.doubleValue());
            };
            overlayHeightListener = (observable, oldValue, newValue) -> {
                overlayPane.setPrefHeight(newValue.doubleValue());
                overlay.setHeight(newValue.doubleValue());
            };
            mainRoot.widthProperty().addListener(overlayWidthListener);
            mainRoot.heightProperty().addListener(overlayHeightListener);

            // Add a click handler to remove the overlay when clicked
            overlayPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                mainRoot.getChildren().remove(overlayPane);
                mainRoot.widthProperty().removeListener(overlayWidthListener);
                mainRoot.heightProperty().removeListener(overlayHeightListener);
            });

            // Add the overlay to the main AnchorPane
            mainRoot.getChildren().add(overlayPane);
        });

        if(state.getWinner().contains(thisPlayer))
            playAudio("endWinner-daCambiare.mp3");
        else
            playAudio("endLoser-daCambiare.mp3");
    }

    public synchronized void showEndGame() {
    }

    // ----------------------------------------------------------------
    //      UTILS: PLAYER CONTAINER
    // ----------------------------------------------------------------

    /**
     * It flips the hand card by switching the corresponding value of handCardSides and
     * then displaying the new side.
     * @param i index of the handCard that needs to be flipped
     * @param handCard the handCard that needs to be flipped
     */
    private void flipCard(int i, ImageView handCard){
        //if (i != playedCardIndex)  this shouldn't be necessary anymore
        if (handCardSides.get(i) == Side.SIDEFRONT) {
            handCardSides.set(i, Side.SIDEBACK);

        } else {
            handCardSides.set(i, Side.SIDEFRONT);
        }
        displayCard(playersHandsPlayable.get(thisPlayer).get(i).getId(), handCardSides.get(i), handCard);
    }

    /**
     * this method displays the hand of playable cards of displayPlayer
     * If displayPlayer is thisPlayer, it prints the card on the front, adds the buttons to flip them and
     * make them draggable if it is the turn of thisPlayer.
     * Otherwise, it shows the cards on the back.
     * Note that handPlayable is managed in such a way that the order of the cards in the hand does not change,
     * but this is only the case as long as you do not change display player.
     */
    private void displayHandPlayable(){
        Platform.runLater(() -> {
            handPlayable = playersHandsPlayable.get(displayPlayer);
            for (int i = 0; i < handCards.size(); i++) {
                if(i<handPlayable.size() && handPlayable.get(i)!=null) {
                    ImageView handCard = handCards.get(i);
                    Button flipHandCard = flipButtons.get(i);
                    handCard.setVisible(true);
                    if (thisPlayer.equals(displayPlayer)) {
                        handCardSides.set(i,Side.SIDEFRONT);
                        int finalI = i;
                        flipHandCard.setVisible(true);
                        flipHandCard.setOnMouseClicked(mouseEvent -> flipCard(finalI, handCard));
                        if (!flowCardPlaced && thisPlayer.equals(state.getCurrentPlayer()) && state.countConnected()>1) {
                            makeDraggable(i, handCard,flipHandCard);
                        }
                    }
                    else {
                        handCardSides.set(i,Side.SIDEBACK);
                        flipHandCard.setOnMouseClicked(null);
                        flipHandCard.setVisible(false);
                        handCard.setOnDragDetected(null);
                    }
                    displayCard(handPlayable.get(i).getId(), handCardSides.get(i), handCard);
                }
                else {
                    flipButtons.get(i).setVisible(false);
                    handCards.get(i).setVisible(false);
                }
            }
        });
    }

    /**
     * Display the personal objective card
     */
    public void displayHandObjective(){
        if(thisPlayer.equals(displayPlayer))
            displayCard(state.getPlayerState(displayPlayer).getHandObjective().getId(), Side.SIDEFRONT, handObjective);
        else
            displayCard(state.getPlayerState(displayPlayer).getHandObjective().getId(), Side.SIDEBACK, handObjective);
    }

    /**
     * Initialize the player container
     */
    private void initPlayerContainer() {
        int playerCount = this.state.getPlayers().size();
        for(Node node: playersContainer.getChildren()) {
            int currPlayerIdx = GridPane.getRowIndex(node);
            StackPane stackPane = (StackPane) node;
            if(currPlayerIdx < playerCount) {
                PlayerLobby currPlayer = this.state.getPlayers().get(currPlayerIdx);
                this.playerNodes.put(currPlayer, node); // save node association with nickname
                stackPane.setOnMouseClicked((MouseEvent event) -> switchToPlayer(currPlayer));

                ColorToken color = currPlayer.getToken().getColor();
                String colorClass = "player-" + color.name().toLowerCase();
                stackPane.getStyleClass().add(colorClass);

                for(Node playerNode: stackPane.getChildren()) {
                    if(playerNode instanceof Label label) {
                        if (label.getId().equals("player")) {
                            String isYouPostfix = this.thisPlayer.equals(currPlayer) ? " (you)" : "";
                            label.setText(currPlayer.getNickname() + isYouPostfix);
                        } else {
                            throw new RuntimeException("Error while labeling in playerContainer");
                        }
                    } else if(playerNode instanceof ImageView imgView) {
                        if (imgView.getId().equals("online")) {
                            imgView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am13/client/view/gui/style/img/player-online.png"))));
                        }
                    }
                }
            } else {
                for(Node playerNode: stackPane.getChildren()) {
                    try {
                        Label labelNode = (Label) playerNode;
                        labelNode.setText("");
                    } catch (Exception ignore) {}
                }
            }
        }
    }

    /**
     * This method updates displayPlayer and displays his field and playable hand
     * @param displayPlayer the player that needs to be displayed
     */
    private void switchToPlayer(PlayerLobby displayPlayer) {
        if(!displayPlayer.equals(this.displayPlayer)) {
            this.displayPlayer = displayPlayer;

            firstFieldScrollAdjustment = true;
            fieldContainer.setPrefSize(fieldScrollPane.getWidth(), fieldScrollPane.getHeight());

            displayField();
            displayHandPlayable();
            if(state.getPlayerState(displayPlayer).getHandObjective()!=null)
                displayHandObjective();
            displayPlayerLabel.setText(displayPlayer.getNickname());
            pickablesContainer.setMouseTransparent(!displayPlayer.equals(thisPlayer));

            if(ParametersClient.SOUND_ENABLE)
                Platform.runLater(()-> {
                    if(System.getProperty("os.name").toLowerCase().contains("win")) {
                        //init fugue player
                        Media fugueMedia = new Media(Objects.requireNonNull(getClass().getResource("/sounds/" + "12ToccataAndFugueInDMinor.mp3")).toString());
                        playCardSoundPlayer = new MediaPlayer(fugueMedia);
                        playCardSoundPlayer.setVolume(0.001);
                        playCardSoundPlayer.setStartTime(Duration.millis(0));
                        playCardSoundPlayer.setStopTime(Duration.millis(1000));
                    }
                });
        }
    }

    /**
     * Updates player container labels representing the turn of the players. Every turn label is set to "waiting"
     * except for the player currently playing, which will be set to "TURN".
     */
    private void playersContainerUpdateTurns() {
        for(PlayerLobby p: this.state.getPlayers()) {
            playerNodes.get(p).setEffect(null);
        }

        //this method is also called in the CALC POINTS phase
        if(state.getCurrentPlayer() != null) {
            DropShadow shadow = new DropShadow();
            shadow.setRadius(25);
            shadow.setOffsetX(0);
            shadow.setOffsetY(0);
            shadow.setSpread(0.5);
            shadow.setColor(new Color(1, 1, 1, 0.75));
            playerNodes.get(state.getCurrentPlayer()).setEffect(shadow);
        }
    }

    /**
     * Updates player container connection status of the player which has disconnected or reconnected.
     * @param player Player which needs its connection status label to be updated
     */
    private void playerContainerUpdateConnection(PlayerLobby player) {
        StackPane sPane = (StackPane) playerNodes.get(player);
        for(Node node: sPane.getChildren()) {
            if (Objects.requireNonNull(node) instanceof ImageView) {
                ImageView imgView = (ImageView) node;
                if (imgView.getId().equals("online")) {
                    if (state.getPlayerState(player).isConnected())
                        Platform.runLater(() -> imgView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am13/client/view/gui/style/img/player-online.png")))));
                    else
                        Platform.runLater(() -> imgView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am13/client/view/gui/style/img/player-offline.png")))));
                }
            }
        }
    }

    // ----------------------------------------------------------------
    //      UTILS: PICKABLES CONTAINER
    // ----------------------------------------------------------------

    /**
     * Display the pickable cards and the common objectives
     */
    private void displayPickablesAndCommonObjs(){
        Platform.runLater(() -> {
            clearPickables();

            List<CardPlayableIF> pickables = state.getPickables();
            List<ImageView> pickablesViews = List.of(resDeck,resPick1,resPick2,gldDeck,gldPick1,gldPick2);
            for (int i = 0; i < pickables.size(); i++) {
                if(pickables.get(i)!=null) {
                    displayCard(pickables.get(i).getId(), pickables.get(i).getVisibleSide(), pickablesViews.get(i));
                    int finalI = i;
                    pickablesViews.get(i).setOnMouseClicked(mouseEvent -> {
                        if (flowCardPlaced)
                            networkHandler.pickCard(pickables.get(finalI));
                    });
                }
                else
                    pickablesViews.get(i).setVisible(false);
            }

            List<CardObjectiveIF> commonObjectives=state.getCommonObjectives();
            displayCard(commonObjectives.get(0).getId(),Side.SIDEBACK,objDeck);
            displayCard(commonObjectives.get(0).getId(),Side.SIDEFRONT,commonObj1);
            displayCard(commonObjectives.get(1).getId(),Side.SIDEFRONT,commonObj2);
        });
    }

    /**
     * Clear the pickable cards
     */
    private void clearPickables(){
        resDeck.setImage(null);
        resPick1.setImage(null);
        resPick2.setImage(null);
        gldDeck.setImage(null);
        gldPick1.setImage(null);
        gldPick2.setImage(null);
        objDeck.setImage(null);
        commonObj1.setImage(null);
        commonObj2.setImage(null);
    }

    /**
     * It makes the card corresponding to the parameters draggable.
     * When the drag is completed, it makes the card not visible and removes to possibility to drag all the cards
     * in the hand.
     * @param handPlayableIndex the index of the card that needs to be made draggable
     * @param imageView corresponding to the card that needs to be made draggable
     */
    private void makeDraggable(int handPlayableIndex, ImageView imageView, Button flipButton){
        imageView.setId(String.valueOf(handPlayableIndex));
        imageView.setOnDragDetected(event -> {
            Dragboard db = imageView.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(imageView.getId());
            db.setContent(content);
            ImageView smallImg=new ImageView(imageView.getImage());
            smallImg.setFitHeight(imageView.getFitHeight()*0.5);
            smallImg.setFitWidth(imageView.getFitWidth()*0.5);
            imageView.setVisible(false);
            db.setDragView(smallImg.snapshot(null,null));
            event.consume();
        });
        imageView.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                imageView.setVisible(false);
                flipButton.setVisible(false);
                attemptedToPlayCardHand = imageView;
                attemptedToPlayFlipButton=flipButton;
                for (ImageView handCard : handCards) {
                    handCard.setOnDragDetected(null);
                }
            }
            else
                imageView.setVisible(true);
            event.consume();
        });
    }

    // ----------------------------------------------------------------
    //      UTILS: FIELD CONTAINER
    // ----------------------------------------------------------------

    /**
     * This method displays the field of displayPlayer, by displaying all the placed cards and adding boxes
     * at each available coordinate.
     */
    private void displayField() {
        Platform.runLater(() -> {
            handPlayable = playersHandsPlayable.get(displayPlayer);
            fieldContainer.getChildren().clear();
            for (Coordinates coordinates : state.getPlayerState(displayPlayer).getField().getPlacedCoords()) {
                CardSidePlayableIF curPlayedCard = state.getPlayerState(displayPlayer).getField().getCardSideAtCoord(coordinates);
                if(curPlayedCard!=null) {
                    ImageView fieldCardImg = new ImageView();
                    displayCard(curPlayedCard.getId(), curPlayedCard.getSide(), fieldCardImg);
                    int posX = (imageW - cornerX) * coordinates.getPosX();
                    int posY = (-imageH + cornerY) * coordinates.getPosY();
                    fieldCardImg.setTranslateX(posX);
                    fieldCardImg.setTranslateY(posY);
                    fieldContainer.getChildren().add(fieldCardImg);
                }
            }

            // print also the token for the color of the player, and the black token if this is the first player
            ImageView token = createTokenImage(displayPlayer);
            token.setFitWidth(tokenDim);
            token.setTranslateX(-0.1867 * imageW);
            token.toFront();
            fieldContainer.getChildren().add(token);
            if(displayPlayer.equals(state.getFirstPlayer())) {
                token = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/tokens/blackToken.png"))));
                token.setPreserveRatio(true);
                token.setFitWidth(tokenDim);
                token.setTranslateX(0.1867 * imageW);
                token.toFront();
                fieldContainer.getChildren().add(token);
            }

            for (Coordinates coordinates : state.getPlayerState(displayPlayer).getField().getAvailableCoords()) {
                addCardBox(coordinates, handPlayable);
            }
            adjustFieldContainerSize();

            // set counter values
            Map<Resource, Integer> resources = state.getPlayerState(displayPlayer).getField().getResourcesInField();
            for(Resource r : counterLabels.keySet()) {
                counterLabels.get(r).setText(resources.get(r).toString());
            }
        });
    }

    /**
     * This method adds a box at the passed coordinates.
     * If thisPlayer is displayPlayer, I make it possible to drag objects (playable cards) into the box.
     * If it is not the turn of thisPlayer, the cards will not be draggable, so it is not necessary to change
     * the action executed when objects are dragged into the box depending on the current player.
     * @param coordinates of the field (as it is stored in the state) at which the box should be added
     * @param finalHandPlayable the playable cards of displayPlayer
     */
    private void addCardBox(Coordinates coordinates, List<CardPlayableIF> finalHandPlayable) {
        Rectangle box = new Rectangle(imageW, imageH, Color.BLUE);
        box.setOpacity(0.5);
        box.setArcHeight(15);
        box.setArcWidth(15);
        int posX = (imageW - cornerX) * coordinates.getPosX();
        int posY = (-imageH + cornerY) * coordinates.getPosY();
        box.setTranslateX(posX);
        box.setTranslateY(posY);
        if(thisPlayer.equals(displayPlayer)) { //I make it possible to drag things into them even if it's not the right run, in that case the hand cards won't be draggable
            box.setOnDragOver(event -> {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            });

            box.setOnDragExited(event -> {
                box.setFill(Color.BLUE);
                event.consume();
            });

            box.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                int handIndex = Integer.parseInt(db.getString());
                networkHandler.playCard(finalHandPlayable.get(handIndex), coordinates, handCardSides.get(handIndex));
                String cardId = finalHandPlayable.get(handIndex).getId();
                ImageView newCardImg = new ImageView();
                displayCard(cardId,handCardSides.get(handIndex),newCardImg);
                newCardImg.setTranslateX(posX);
                newCardImg.setTranslateY(posY);
                attemptedToPlayCardField = newCardImg;
                attemptedToPlayCardBox = box;
                fieldContainer.getChildren().remove(box);
                fieldContainer.getChildren().add(newCardImg);
                event.setDropCompleted(true);
                event.consume();
            });
        }
        fieldContainer.getChildren().add(box);
        box.toBack();

        fieldContainer.layout();  // Force layout pass to ensure bounds are updated
    }

    /**
     * Adjust field container size to contain all the cards displayed in it.
     * It checks the maximum extension of the elements in it, and if it does not exceed the fieldScrollPane current
     * size, it sets the size of the fieldContainer to the double of the fieldScrollPane.
     * If instead it exceeds, the size if that maximum extension doubled.
     * Also, the scroll of the fieldScrollPane is adjusted, considering if it is the first time the adjustment happens
     * for the displayed field (set to center of field) or if it must take into account the old value.
     */
    private void adjustFieldContainerSize() {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (Node node : fieldContainer.getChildren()) {
            Bounds bounds = node.getBoundsInParent();
            minX = Math.min(minX, bounds.getMinX());
            minY = Math.min(minY, bounds.getMinY());
            maxX = Math.max(maxX, bounds.getMaxX());
            maxY = Math.max(maxY, bounds.getMaxY());
        }


        double prefWidth = 2 * Math.max(maxX - minX, fieldScrollPane.getWidth());
        double prefHeight = 2 * Math.max(maxY - minY, fieldScrollPane.getHeight());
        double scrollX, scrollY;
        if(firstFieldScrollAdjustment) {
            scrollX = 0.5;
            scrollY = 0.5;
            firstFieldScrollAdjustment = false;
        } else {
            scrollX = prefWidth == 2* fieldScrollPane.getWidth() ? fieldScrollPane.getHvalue() :
                    fieldScrollPane.getHvalue() * prefWidth / fieldContainer.getPrefWidth()
                    + 0.5 * (prefWidth-fieldContainer.getPrefWidth()) / fieldContainer.getPrefWidth();
            scrollY = prefHeight == 2 * fieldScrollPane.getHeight() ? fieldScrollPane.getVvalue() :
                    fieldScrollPane.getVvalue() * prefHeight / fieldContainer.getPrefHeight()
                    + 0.5 * (prefHeight-fieldContainer.getPrefHeight()) / fieldContainer.getPrefHeight();
        }

        // Update the preferred size of the StackPane
        fieldContainer.setPrefSize(prefWidth, prefHeight);
        fieldContainer.widthProperty().addListener((oldVal,newVal,obs) -> fieldScrollPane.setHvalue(scrollX));
        fieldContainer.heightProperty().addListener((oldVal,newVal,obs) -> fieldScrollPane.setVvalue(scrollY));
    }

    /**
     * Update the actionLabel's text, according to the current phase of the game flow for thisPlayer
     */
    private void updateActionLabel() {
        Platform.runLater(() -> {
            if(state.getGameStatus() == GameStatus.CALC_POINTS || state.getGameStatus() == GameStatus.ENDED)
                actionLabel.setText("Game ended");
            else if(thisPlayer.equals(state.getCurrentPlayer())) {
                if(state.countConnected()==1)
                    actionLabel.setText("You're alone, wait");
                else {
                    if (!flowCardPlaced)
                        actionLabel.setText("Play a card");
                    else
                        actionLabel.setText("Pick a card");
                }
            } else
                actionLabel.setText("Wait for your turn");
        });
    }

    /**
     * Method associated to the button for zooming in the field
     */
    @FXML
    public void onFieldZoomButtonAction() {
        if(fieldContainer.getScaleX()<3) {
            fieldContainer.setScaleX(fieldContainer.getScaleX() * 1.1);
            fieldContainer.setScaleY(fieldContainer.getScaleY() * 1.1);
        }
    }

    /**
     * Method associated to the button for zooming out the field
     */
    @FXML
    public void onFieldDezoomButtonAction() {
        if(fieldContainer.getScaleX()>0.1) {
            fieldContainer.setScaleX(fieldContainer.getScaleY() / 1.1);
            fieldContainer.setScaleY(fieldContainer.getScaleY() / 1.1);
        }
    }

    /**
     * Method associated to the scroll (CTRL + scroll of mouse wheel) to zoom/dezoom the field
     * @param scrollEvent Event of the scroll
     */
    @FXML
    public void onMouseWheelScroll(ScrollEvent scrollEvent) {
        if (scrollEvent.isControlDown()) {
            double delta = scrollEvent.getDeltaY();
            if (delta > 0) {
                onFieldZoomButtonAction();
            } else {
                onFieldDezoomButtonAction();
            }
        }
    }

    /**
     * Method associated to the button for resetting the scroll/zoom of the field
     */
    public void onClickResetFieldScroll() {
        fieldScrollPane.setHvalue(0.5);
        fieldScrollPane.setVvalue(0.5);
        fieldContainer.setScaleX(1);
        fieldContainer.setScaleY(1);
    }

    // ----------------------------------------------------------------
    //      UTILS: SCORE TRACKER AND COUNTER
    // -------------------------------------------------------------

    /**
     * Initializes the score tracker view and creates all the tokens as image views.
     * Places all the tokens at the position 0
     */
    private void initScoreTracker() {
        Platform.runLater(() -> {
            int count = 0;
            for(PlayerLobby p : state.getPlayers()) {
                savedPoints.put(p, 0);
                tokenOffsetCoordinates.put(p, count*TOKEN_X_OFFSET);
                ImageView tokenImg = createAndPositionTokenImage(p);
                tokenImgs.put(p, tokenImg);
                count++;
            }
//            tokenImgs.get(thisPlayer).toFront();
        });
        for(PlayerLobby p : state.getPlayers()) {
            lastTokenAnimations.put(p, null);
        }
    }

    /**
     * Creates the image for the color token of the given player
     * @param player Player whose the color token images is to be generated
     * @return Container of the image of the player's color token
     */
    private ImageView createTokenImage(PlayerLobby player) {
        Image tokenTexture = null;
        switch (player.getToken().getColor()) {
            case ColorToken.RED -> tokenTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/tokens/redToken.png")));
            case ColorToken.BLUE -> tokenTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/tokens/blueToken.png")));
            case ColorToken.GREEN -> tokenTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/tokens/greenToken.png")));
            case ColorToken.YELLOW -> tokenTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/tokens/yellowToken.png")));
        }
        ImageView token = new ImageView(tokenTexture);
        token.setPreserveRatio(true);
        return token;
    }

    /**
     * Creates the image for the color token of the given player, and places it on the position 0 of the score tracker
     * @param player Player whose the color token images is to be generated and placed
     * @return Container of the image of the player's color token
     */
    private ImageView createAndPositionTokenImage(PlayerLobby player) {
        double xDim = scoreTrackerContainer.getWidth() == 0 ? scoreTrackerView.getFitWidth() : scoreTrackerContainer.getWidth();
        double yDim = scoreTrackerContainer.getHeight() == 0 ? scoreTrackerView.getFitHeight() : scoreTrackerContainer.getHeight();
        ImageView newToken = createTokenImage(player);

        newToken.setFitHeight(tokenDimRel2x * xDim);
        newToken.setFitWidth(tokenDimRel2x * xDim);

        StackPane.setAlignment(newToken, Pos.BOTTOM_LEFT);
        newToken.setTranslateX(xTranslToken.getFirst() * xDim + tokenOffsetCoordinates.get(player));
        newToken.setTranslateY(yTranslToken.getFirst() * yDim);
        scoreTrackerContainer.getChildren().add(newToken);
        newToken.toFront();

        return newToken;
    }

    /**
     * Recursive creation and concatenation of the animation for the new tokens created to pass from
     * the current saved point to the final updated points
     * @param player Player whose token is to be animated
     */
    private void updateTokenPositionRic(PlayerLobby player) {
        int points = state.getPlayerState(player).getPoints();
        int currentPoints = savedPoints.get(player);

        if(points == currentPoints)
            return;

        if (currentPoints % 29 == 0 && currentPoints > 0) {
            // The maximum of the score tracker was surpassed, i must start from 0
            ImageView token = createAndPositionTokenImage(player);
            tokenImgs.replace(player, token);
        }

        if (currentPoints / 29 < points / 29) {
            // I must go to 29 and concatenate another animation after that
            int finalAnimationPoints = (currentPoints / 29 + 1) * 29;
            PathTransition animation = createAnimationTokenMove(player, currentPoints % 29, 29);
            tokenOffsetCoordinates.replace(player, 0.0);
            animation.setOnFinished(event -> {
                synchronized (player) {
                    savedPoints.replace(player, finalAnimationPoints);
                    tokenImgs.get(player).toFront();
                    updateTokenPositionRic(player);
                    lastTokenAnimations.replace(player, null);
                }
            });

            synchronized (player) {
                PathTransition lastTokenAnimation = lastTokenAnimations.get(player);
                if(lastTokenAnimation != null) {     // An animation is still playing
                    lastTokenAnimation.setOnFinished(actionEvent -> {
                        savedPoints.replace(player, points);
                        tokenImgs.get(player).toFront();
                        animation.play();
                        lastTokenAnimations.replace(player, animation);
                    });
                } else {     // no animation is currently playing
                    savedPoints.replace(player, points);
                    tokenImgs.get(player).toFront();
                    animation.play();
                }
            }

        } else {
            PathTransition animation = createAnimationTokenMove(player, currentPoints % 29, points % 29);
            animation.setOnFinished(actionEvent -> {
                    synchronized (player) {
                        lastTokenAnimations.replace(player, null);
                        tokenImgs.get(player).toFront();
                    }
                }
            );
            synchronized (player) {
                PathTransition lastTokenAnimation = lastTokenAnimations.get(player);
                if(lastTokenAnimation != null) {     // An animation is still playing
                    lastTokenAnimation.setOnFinished(actionEvent -> {
                        savedPoints.replace(player, points);
                        tokenImgs.get(player).toFront();
                        animation.play();
                        lastTokenAnimations.replace(player, animation);
                    });
                } else {     // no animation is currently playing
                    savedPoints.replace(player, points);
                    animation.play();
                }
            }
        }
    }

    /**
     * Updates the position of the token image on the score tracker, according to the actual points of the player.
     * It animates the movement making the token pass through all the intermediate steps.
     * If the points exceeds the maximum of the scoretracker, a new token is created starting from 0 again, so that
     * the total points are obtained as the sum of the points where the old token and the new token are.
     * @param player Player whose token is to be moved
     */
    private void updateTokenPosition(PlayerLobby player) {
        Platform.runLater(() -> updateTokenPositionRic(player));
    }

    /**
     * Creates and plays an animation of the given token moving on the scoretrakcer.
     * The specified points must be the visible number on the scoretracker, and the token can move only forward
     * @param player Player whose token is to be moved
     * @param from Points where to start from. Must be a number >=0 and < 29
     * @param to Points where to arrive. Must be a number <code>>from</code>, so >0 and <=29
     * @return The transition animation (could be useful to add listeners...)
     */
    private PathTransition createAnimationTokenMove(PlayerLobby player, int from, int to) {
        ImageView token = tokenImgs.get(player);
        double xDim = scoreTrackerContainer.getWidth();
        double yDim = scoreTrackerContainer.getHeight();

        Path path = new Path();
        path.setVisible(false);
        path.setLayoutX(tokenDimRel2x*xDim/2);
        path.setLayoutY(tokenDimRel2x*xDim/2);
        scoreTrackerContainer.getChildren().add(path);
        path.getElements().add(new MoveTo(
                xTranslToken.get(from) * xDim + tokenOffsetCoordinates.get(player),
                yTranslToken.get(from) * yDim ));

        // If from where i am now there are other tokens with offset > than mine, i must move them
        List<PlayerLobby> inSameSpot = new ArrayList<>();
        for(int i=0 ; i<5 ; i++) {
            int finalI = i;
            List<PlayerLobby> temp = savedPoints.entrySet().stream().filter(entry -> entry.getValue()==from+29*finalI).
                    map(Map.Entry::getKey).toList();
            if(!temp.isEmpty())
                inSameSpot.addAll(temp);
        }
        for(PlayerLobby p : inSameSpot)
            if(tokenOffsetCoordinates.get(p) > tokenOffsetCoordinates.get(player))
                playAnimationTokenOffsetLess(p);

        for(int point=from+1 ; point<=to ; point++) {
            int count = 0;
            for(PlayerLobby p : state.getPlayers())
                if(!p.equals(player) && savedPoints.get(p)%29==point)
                    count++;

            double offset = count * TOKEN_X_OFFSET;
            tokenOffsetCoordinates.replace(player, offset);
            path.getElements().add(new LineTo(
                    xTranslToken.get(point) * xDim + offset,
                    yTranslToken.get(point) * yDim));
        }

        if(to == 29 && player.equals(thisPlayer))
            tokenImgs.get(player).toFront();

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(0.25*(to-from))); // Set animation duration
        pathTransition.setPath(path);
        pathTransition.setNode(token);
        pathTransition.setCycleCount(1); // Play once
        return pathTransition;
    }

    /**
     * Creates and plays an animation moving the token of the specified player of an 'offset less', if its
     * token has an offset > 0.
     * Updates the saved offset, too.
     * @param player Player whose token is to be moved
     */
    private void playAnimationTokenOffsetLess(PlayerLobby player) {
        if(tokenOffsetCoordinates.get(player) == 0)
            return;

        double xDim = scoreTrackerContainer.getWidth();
        double yDim = scoreTrackerContainer.getHeight();
        Path path = new Path();
        path.setVisible(false);
        path.setLayoutX(tokenDimRel2x * xDim / 2);
        path.setLayoutY(tokenDimRel2x * xDim / 2);
        scoreTrackerContainer.getChildren().add(path);
        path.getElements().add(new MoveTo(
                xTranslToken.get(savedPoints.get(player) % 29) * xDim + tokenOffsetCoordinates.get(player),
                yTranslToken.get(savedPoints.get(player) % 29) * yDim));

        tokenOffsetCoordinates.replace(player, tokenOffsetCoordinates.get(player) - TOKEN_X_OFFSET);
        path.getElements().add(new LineTo(
                xTranslToken.get(savedPoints.get(player) % 29) * xDim + tokenOffsetCoordinates.get(player),
                yTranslToken.get(savedPoints.get(player) % 29) * yDim));

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(0.25)); // Set animation duration
        pathTransition.setPath(path);
        pathTransition.setNode(tokenImgs.get(player));
        pathTransition.setCycleCount(1); // Play once
        pathTransition.setOnFinished(actionEvent -> {
            synchronized (player) {
                lastTokenAnimations.replace(player, null);
            }
        });

        synchronized (player) {
            if(lastTokenAnimations.get(player) != null) {
                lastTokenAnimations.get(player).setOnFinished(actionEvent -> {
                    lastTokenAnimations.replace(player, pathTransition);
                    pathTransition.play();
                    tokenImgs.get(player).toFront();
                });
            } else {
                lastTokenAnimations.replace(player, pathTransition);
                pathTransition.play();
                tokenImgs.get(player).toFront();
            }
        }
    }

    //if you need methods related to other player to use as a reference, see 28/05, before ~7pm

    // ----------------------------------------------------------------
    //      COMMON
    // ----------------------------------------------------------------

    /**
     * It opens the image corresponding to the passed id and side, adds it to the passed ImageView and then
     * sets the height and width (they will always be the same since the only images that need to be displayed are cards).
     * If necessary, it adapts the id (since many backs are identical, and all the backs of objective cards are identical,
     * there is not a different image for each one of them)
     *  @param id of the card to be displayed
     * @param side of the card to be displayed
     * @param imageView in which the card should be displayed
     */
    private void displayCard(String id, Side side, ImageView imageView){
        Image cardImage;
        if(side==Side.SIDEBACK && id.charAt(0)!='s')
            if (id.charAt(0)=='o')
                id="o000";
            else
                id=id.substring(0,3)+'0';
        if(side==Side.SIDEFRONT)
            cardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/cards/fronts/" + id + ".png")));
        else
            cardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/cards/backs/" + id + ".png")));
        imageView.setImage(cardImage);
        imageView.setFitHeight(imageH);
        imageView.setFitWidth(imageW);
    }

    /**
     * Updates the logArea to show the last non-shown log messages
     */
    private void showLastLogs() {
        Platform.runLater(() -> {
            while(log.hasOtherLogs())
                logArea.appendText(log.popNextLog() + "\n");
        });
    }

    /**
     * Plays the audio file specified by the name.
     * The file must be in relative path /sounds with respect to the fxml file
     * @param fileName Name of the file, with the extension too
     */
    private void playAudio(String fileName, double volume) {
        if(ParametersClient.SOUND_ENABLE)
            if(System.getProperty("os.name").toLowerCase().contains("win")) {
                Platform.runLater(() -> {
                    AudioClip audioClip = new AudioClip(Objects.requireNonNull(getClass().getResource("/sounds/" + fileName)).toString());
                    audioClip.setVolume(volume);
                    audioClip.play();
                });
            }
    }

    private void playAudio(String fileName) {
        playAudio(fileName,0.3);
    }

    /**
     * Creates and plays a transition making the given node appearing with the slide of a clip from left to right
     * @param node Node to make appear
     * @param totWidth Width of the node (total movement the clip must do)
     */
    private void playTransitionAppearLeftRight(Node node, double totWidth) {
        Rectangle clip = new Rectangle(0, totWidth);
        // Apply a GaussianBlur to the clip
        GaussianBlur blur = new GaussianBlur(20); // Adjust the radius as needed
        blur.setRadius(75);
        clip.setEffect(blur);

        node.setClip(clip);

        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(clip.widthProperty(), totWidth);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1.25), keyValue); // Duration of 2 seconds
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    /**
     * Action performed to return to homepage, actually disconnecting voluntarily from the game
     */
    @FXML
    private void onReturnRoomClick() {
        networkHandler.stopPing();
        view.showStartupScreen(ParametersClient.IS_SOCKET, ParametersClient.SERVER_IP, ParametersClient.SERVER_PORT);
        networkHandler.getRooms();
    }

    // ----------------------------------------------------------------
    //    CHAT METHODS
    // ----------------------------------------------------------------

    /**
     * Sets the chat, and initializes chatChoices with the valid choices.
     * @param chat of thisPlayer in this match
     */
    public void setChat(Chat chat){

        List<PlayerLobby> otherPlayers=new ArrayList<>();
        for(PlayerLobby player : state.getPlayers())
            if(!player.equals(thisPlayer)) {
                List<PlayerLobby> onePlayerList = new ArrayList<>(List.of(player));
                chatChoice.getItems().add(onePlayerList);
                otherPlayers.add(player);
            }
        if(otherPlayers.size()>1)
            chatChoice.getItems().add(otherPlayers);
        chatChoice.setOnAction(actionEvent -> showChatWith(chatChoice.getValue()));
        chatChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(List<PlayerLobby> playerLobbies) {
                if (playerLobbies == null)
                    return "";
                if (playerLobbies.size() == 1)
                    return playerLobbies.getFirst().getNickname();
                return "All";
            }

            @Override
            public List<PlayerLobby> fromString(String s) {
                if (s.equals("All"))
                    return otherPlayers;
                else
                    for (PlayerLobby player : state.getPlayers())
                        if (player.getNickname().equals(s))
                            return new ArrayList<>(List.of(player));
                return null;
            }
        });
        this.chat = chat;
    }

    /**
     * When the button is clicked, if there is a message and a receiver has been selected, a chat message is sent.
     */
    @FXML
    public synchronized void onClickSendMessage(){
        if(!chatField.getText().isEmpty() && chatChoice.getValue()!=null) {
            networkHandler.sendChatMessage(chatChoice.getValue(), chatField.getText());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * When enter is pressed on the keyboard, the message is sent
     * @param event a key is pressed on the keyboard
     */
    @FXML
    public void onChatTextFieldKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onClickSendMessage();
        }
    }

    @FXML
    public void onClickShowRulebook(){
        InputStream manualAsStream= Objects.requireNonNull(getClass().getResourceAsStream("/docs/CODEX_Rulebook_EN.pdf"));
        java.nio.file.Path tempOutput;
        try {
            tempOutput = Files.createTempFile("TempManual", ".pdf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tempOutput.toFile().deleteOnExit();
        try {
            Files.copy(manualAsStream, tempOutput, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File userManual = new File (tempOutput.toFile().getPath());
        new Thread(() -> {
            try {
                Desktop.getDesktop().open(userManual);
            } catch (IOException ignored) {

            }
        }).start();
    }

    /**
     * If the message belongs to the chat that is currently being selected, it shows it.
     *
     * @param sender    of the message
     * @param receivers of the message
     */
    public void showChatMessage(PlayerLobby sender, List<PlayerLobby> receivers) {
        if(chatChoice.getValue()!=null) {
            if (sender.equals(thisPlayer)) {
                chatField.clear();
                if (receivers.equals(chatChoice.getValue()))
                    showChatWith(chatChoice.getValue());
            } else if (receivers.contains(thisPlayer)) {
                if (receivers.size() == 1 && chatChoice.getValue().size() == 1 && chatChoice.getValue().getFirst().equals(sender))
                    showChatWith(chatChoice.getValue());
                else if (receivers.size() > 1 && chatChoice.getValue().size() > 1)
                    showChatWith(chatChoice.getValue());
            }
        }

        if (receivers.contains(thisPlayer)) {
            log.logMessageReceived(sender, receivers.size() > 1);
            showLastLogs();
            playAudio("messageNotification.mp3");

        }
    }

    /**
     * Clears the chat area and replaces it with all the messages between thisPlayer and the passed parameter
     * @param receivers list of players that thisPlayer is chatting with
     */
    private void showChatWith(List<PlayerLobby> receivers) {
        Platform.runLater(() -> {
                    chatArea.clear();
                    for (ChatMessage chatMessage : chat.getChatWith(receivers))
                        chatArea.appendText(chatMessage.getSender().getNickname() + ": " + chatMessage.getText() + "\n");
                }
        );
    }
}
