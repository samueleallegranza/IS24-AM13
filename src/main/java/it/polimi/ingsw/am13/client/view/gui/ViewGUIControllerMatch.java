package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.chat.Chat;
import it.polimi.ingsw.am13.client.chat.ChatMessage;
import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.model.GameStatus;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.util.*;


//TODO x Matteo aggiungi traslazione di token su tabellone quando si aggiornano i punti

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
     * Label displaying whose the current displayed field is
     */
    @FXML
    private Label displayPlayerLabel;
    /**
     * Label for counter visible during final phase, displaying the number of turns left to the end of the game
     */
    @FXML
    private Label turnsCounterLabel;

    @FXML
    private Label plantCounterLabel;
    @FXML
    private Label animalCounterLabel;
    @FXML
    private Label fungusCounterLabel;
    @FXML
    private Label manuscriptCounterLabel;
    @FXML
    private Label insectCounterLabel;
    @FXML
    private Label quillCounterLabel;
    @FXML
    private Label inkwellCounterLabel;
    @FXML
    private ImageView plantCounterImage;
    @FXML
    private ImageView animalCounterImage;
    @FXML
    private ImageView fungusCounterImage;
    @FXML
    private ImageView insectCounterImage;
    @FXML
    private ImageView inkwellCounterImage;
    @FXML
    private ImageView quillCounterImage;
    @FXML
    private ImageView manuscriptCounterImage;

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


    //CHAT

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


    //TODO: aggiungi documentazione per gli attributi qua sotto
    private List<Side> handCardSides;
    private boolean flowCardPlaced;
    private ImageView attemptedToPlayCardHand;
    private Button attemptedToPlayFlipButton;
    private ImageView attemptedToPlayCardField;
    private Rectangle attemptedToPlayCardBox;
    private List<CardPlayableIF> handPlayable;
    private Map<PlayerLobby,List<CardPlayableIF>> playersHandsPlayable;
    private List<ImageView> handCards;
    private List<Button> flipButtons;
    private Map<String, Node> playerNodes;
    private Map<Resource, Label> counterLabels;
    /**
     * Map associating each player to its token image in the score tracker
     */
    private final Map<PlayerLobby, ImageView> tokenImgs = new HashMap<>();
    /**
     * Map associating each player to the current saved points, before a new eventual modification
     */
    private final Map<PlayerLobby, Integer> savedPoints = new HashMap<>();
    /**
     * Flag indicating if the first scroll adjustment to center the starter card has already happened
     * Hence it is set to true each time the displayPlayer changes
     */
    private boolean firstFieldScrollAdjustment = true;

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
     * Token dimension (square shape) relative to width (x) of the score tracker
     */
    private static final double tokenDimRel2x = 0.17083;


    // ----------------------------------------------------------------
    //      CONTROLLER METHODS
    // ----------------------------------------------------------------

    @Override
    public void setThisPlayer(PlayerLobby thisPlayer) {
        this.thisPlayer = thisPlayer;
    }
    @Override
    public void setGameState(GameState gameState) {
        this.state=gameState;
        log = new LogGUI(gameState);
    }
    @Override
    public String getSceneTitle() {
        return "Turn-based phase";
    }

    @Override
    public void showException(Exception e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        });

        //TODO sometimes we get a "player doesn't have this card exception", even if should be a req not met exception
        if(state.getCurrentPlayer().equals(thisPlayer) && !flowCardPlaced) {
            Platform.runLater(() -> {
//                handCardsContainer.getChildren().add(attemptedToPlayCardHand);
                attemptedToPlayCardHand.setVisible(true);
                attemptedToPlayFlipButton.setVisible(true);
                fieldContainer.getChildren().remove(attemptedToPlayCardField);
                fieldContainer.getChildren().add(attemptedToPlayCardBox);
                attemptedToPlayCardBox.toBack();
                for (int i = 0; i < handCards.size(); i++) {
                    int finalI = i;
                    handCards.get(i).setOnDragDetected( event -> {
                        Dragboard db = handCards.get(finalI).startDragAndDrop(TransferMode.ANY);
                        ClipboardContent content = new ClipboardContent();
                        content.putString(handCards.get(finalI).getId());
                        db.setContent(content);
                        event.consume();
                    } );
                }
            });
        }
    }
    @Override
    public void showPlayerDisconnected(PlayerLobby player) {
        playerContainerUpdateConnection(player);
        //if I am currently watching a player who disconnected, I automatically switch the view back to mine
//        if(player.equals(displayPlayer)){
//            displayPlayer = thisPlayer;
//            handPlayable = new ArrayList<>(state.getPlayerState(displayPlayer).getHandPlayable());
//            displayField();
//            displayHandPlayable();
//        }

        log.logDisconnect(player);
        showLastLogs();
    }
    @Override
    public void showPlayerReconnected(PlayerLobby player) {
        playerContainerUpdateConnection(player);

        log.logReconnect(player);
        showLastLogs();
    }


    public void showInGame(Chat chat) {
        // Init of lists of graphical elements
        flipButtons = List.of(flipButton0, flipButton1, flipButton2);
        handCards = List.of(handCard0, handCard1, handCard2);
        setChat(chat);
        turnsCounterLabel.setVisible(false);

        // Displaying the pickable cards and the common objectives
        // At the beginning of the game, the pickable cards shouldn't be clickable
        displayPickablesAndCommonObjs();
        pickablesContainer.setMouseTransparent(true);

        // Set the initial side of the hand cards to front
        handCardSides = new ArrayList<>(List.of(Side.SIDEFRONT, Side.SIDEFRONT, Side.SIDEFRONT));

        // Set the initial condition to 'no card placed', for flow of the game
        flowCardPlaced = false;

        // Init of players container
        playerNodes = new HashMap<>();
        initPlayerContainer();

        // Init of counter and score tracker
        initCounter();
        initScoreTracker();

        // First log
        log.logNextTurn();
        showLastLogs();

        // Set size of fieldScrollPane
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        fieldScrollPane.setPrefHeight(screenBounds.getHeight() * 0.9);
        fieldScrollPane.setPrefWidth(screenBounds.getWidth() - 130);

        //adjust the field whenever the size of the scroll pane changes
        fieldScrollPane.widthProperty().addListener(
                (obs,oldVal,newVal)-> adjustFieldContainerSize()

        );
        fieldScrollPane.heightProperty().addListener(
                (obs,oldVal,newVal)-> adjustFieldContainerSize()
        );

        //Initialize hand playables of each player
        playersHandsPlayable=new HashMap<>();
        for(PlayerLobby player : state.getPlayers())
            playersHandsPlayable.put(player,new ArrayList<>((state.getPlayerState(player).getHandPlayable())));

        // Set actionLabel and the field
        displayPlayer = null;
        switchToPlayer(thisPlayer);
        updateActionLabel();
    }

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
        }

        playersContainerUpdatePoints(player);
        updateTokenPosition(player);

        log.logPlayedCard(player, coord, pointsBefore < state.getPlayerState(player).getPoints());
        showLastLogs();
        updateActionLabel();
    }

    public void showPickedCard(PlayerLobby player) {
        if (this.thisPlayer.equals(player)){
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

    public void showNextTurn() {
        if (displayPlayer.equals(thisPlayer) && state.getCurrentPlayer().equals(thisPlayer)) {
            for (int i = 0; i < handCards.size(); i++) {
                    ImageView handCard = handCards.get(i);
                    makeDraggable(i, handCard, flipButtons.get(i));
            }
        }
        if(state.getCurrentPlayer().equals(thisPlayer)) {
            flowCardPlaced = false;
        }
        playersContainerUpdateTurns();

        log.logNextTurn();
        showLastLogs();
        updateActionLabel();
        if(state.getGameStatus() == GameStatus.FINAL_PHASE)
            Platform.runLater(() -> turnsCounterLabel.setText(String.format("-%d to the end of game", state.getTurnsToEnd())));
    }

    public synchronized void showFinalPhase() {
        log.logFinalPhase();
        showLastLogs();
        Platform.runLater(() -> {
            // I repeat this line here, not only in showNextTurn, so that i'm sure this is done
            turnsCounterLabel.setText(String.format("-%d to the end of game", state.getTurnsToEnd()));
            turnsCounterLabel.setVisible(true);
        });
    }


    //TODO: gestire nel server o nel client il caso di parità!!
    //TODO: magari nei log scrivi anche che tipo di carta è stata giocata / pescata

    // ----------------------------------------------------------------
    //      OVERLAYER FOR WINNER
    // ----------------------------------------------------------------

    /**
     * Creates a new layer with the given table to show the final points for the players.
     * @param pointsTable Table of the final points of the players
     * @param winnerText Label showing the definitive winner
     */
    private void createWinnerLayer(TableView<PlayerLobby> pointsTable, Label winnerText) {
        // Get the root AnchorPane dynamically
        AnchorPane anchorPane = (AnchorPane) actionLabel.getScene().getRoot();

        // Create the semi-transparent layer
        Rectangle overlay = new Rectangle(anchorPane.getWidth(), anchorPane.getHeight(), Color.rgb(0, 0, 0, 0.5));
        overlay.setMouseTransparent(true);  // Allow clicks to pass through to the underlying elements

        // Create a VBox to hold the table and center it in the overlay
        VBox vbox = new VBox(winnerText, pointsTable);
        vbox.setSpacing(30);
        vbox.setAlignment(Pos.CENTER);
        vbox.setMinSize(300, 200);

        // Create a StackPane to hold both the overlay and the table
        StackPane overlayPane = new StackPane(overlay, vbox);
        overlayPane.setPrefSize(anchorPane.getWidth(), anchorPane.getHeight());

        // Anchor the overlayPane to all sides of the AnchorPane
        AnchorPane.setTopAnchor(overlayPane, 0.0);
        AnchorPane.setBottomAnchor(overlayPane, 0.0);
        AnchorPane.setLeftAnchor(overlayPane, 0.0);
        AnchorPane.setRightAnchor(overlayPane, 0.0);

        // Add a click handler to remove the overlay when clicked
        overlayPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> anchorPane.getChildren().remove(overlayPane));

        // Add the overlay to the main AnchorPane
        anchorPane.getChildren().add(overlayPane);
    }

    /**
     * Modifies all the elements on the view in order to update them with the final value and not to allow any other modifications.
     */
    public synchronized void showUpdatePoints() {
        log.logUpdatePoints(savedPoints);
        showLastLogs();
        updateActionLabel();
        playersContainerUpdateTurns();
        state.getPlayers().forEach(this::playersContainerUpdatePoints);
        turnsCounterLabel.setVisible(false);
        //TODO: facciamo muovere anche i token sullo scoreboard?
    }

    /**
     * Creates a new layer over all the view, which closes after the first click of the mouse
     */
    public synchronized void showWinner() {
        log.logWinner();
        showLastLogs();

        Platform.runLater(() -> {
            // Set background color with blur effect
            Label winnerText = new Label();
            winnerText.setFont(new Font(24));
            winnerText.setTextFill(Color.WHITE);
            winnerText.setPadding(new Insets(10));

            // Set background color with blur effect
            BackgroundFill backgroundFill = new BackgroundFill(Color.BLACK, new CornerRadii(5), Insets.EMPTY);
            winnerText.setBackground(new Background(backgroundFill));

            // Apply blur effect only to the background
            DropShadow dropShadow = new DropShadow();
            dropShadow.setColor(Color.BLACK);
            dropShadow.setBlurType(BlurType.GAUSSIAN);
            dropShadow.setRadius(10); // Adjust blur radius as needed
            winnerText.setEffect(dropShadow);

            TableView<PlayerLobby> pointsTable = new TableView<>();
            pointsTable.setMaxWidth(175);
            TableColumn<PlayerLobby,String> playerColumn = new TableColumn<>();
            playerColumn.setPrefWidth(75);
            pointsTable.getColumns().add(playerColumn);
            TableColumn<PlayerLobby,String> pointsColumn = new TableColumn<>();
            pointsColumn.setPrefWidth(100);
            pointsTable.getColumns().add(pointsColumn);

            for(PlayerLobby playerLobby : state.getPlayers()) {
                pointsTable.getItems().add(playerLobby);
            }
            playerColumn.setCellValueFactory(new PropertyValueFactory<>("Nickname"));
            pointsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(state.getPlayerState(cellData.getValue()).getPoints())));

            if(thisPlayer.equals(state.getWinner()))
                winnerText.setText("You have won the game");
            else
                winnerText.setText(state.getWinner().getNickname() + " won the game");

            createWinnerLayer(pointsTable, winnerText);
        });
    }

    public synchronized void showEndGame() {
        //TODO: da implementare
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
                        if (!flowCardPlaced && state.getCurrentPlayer().equals(thisPlayer)) {
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
            if(thisPlayer.equals(displayPlayer))
                displayCard(state.getPlayerState(displayPlayer).getHandObjective().getId(), Side.SIDEFRONT, handObjective);
            else
                displayCard(state.getPlayerState(displayPlayer).getHandObjective().getId(), Side.SIDEBACK, handObjective);
        });
    }

    private void initPlayerContainer() {
        int playerCount = this.state.getPlayers().size();
        for(Node node: playersContainer.getChildren()) {
            int currPlayerIdx = GridPane.getRowIndex(node);
            VBox vBox = (VBox) node;
            if(currPlayerIdx < playerCount) {
                PlayerLobby currPlayer = this.state.getPlayers().get(currPlayerIdx);
                this.playerNodes.put(currPlayer.getNickname(), node); // save node association with nickname
                vBox.setOnMouseClicked((MouseEvent event) -> switchToPlayer(currPlayer));
                for(Node labelNode: vBox.getChildren()) {
                    Label label = (Label) labelNode;
                    switch (label.getId()) {
                        case "player" -> {
                            String isYouPostfix = this.thisPlayer.equals(currPlayer) ? " (you)" : "";
                            label.setText(currPlayer.getNickname() + isYouPostfix);
                        }
                        case "online" -> label.setText(this.state.getPlayerState(currPlayer).isConnected() ? "online" : "disconnected");
                        case "turn" -> label.setText(this.state.getCurrentPlayer().equals(currPlayer) ? "TURN" : "waiting");
                        case "points" -> label.setText(this.state.getPlayerState(currPlayer).getPoints() + " pts");
                        default -> throw new RuntimeException("Error while labeling in playerContainer");
                    }
                }
            } else {
                for(Node labelNode: vBox.getChildren()) ((Label) labelNode).setText("");
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
            displayPlayerLabel.setText("You are watching player " + displayPlayer.getNickname());
            pickablesContainer.setMouseTransparent(!displayPlayer.equals(thisPlayer));
        }
    }

    private void playersContainerUpdatePoints(PlayerLobby player) {
        Platform.runLater(() -> {
            // get node corresponding to player
            VBox vBox = (VBox) this.playerNodes.get(player.getNickname());
            for(Node labelNode: vBox.getChildren()) {
                Label label = (Label) labelNode;
                if(label.getId().equals("points"))
                    label.setText(this.state.getPlayerState(player).getPoints() + " pts");
            }
        });
    }

    /**
     * Updates player container labels representing the turn of the players. Every turn label is set to "waiting"
     * except for the player currently playing, which will be set to "TURN".
     */
    private void playersContainerUpdateTurns() {
        for(PlayerLobby p: this.state.getPlayers()) {
            String text;
            if(state.getGameStatus() == GameStatus.CALC_POINTS || state.getGameStatus() == GameStatus.ENDED)
                text = "ended";
            else
                text = p.equals(state.getCurrentPlayer()) ? "TURN" : "waiting";

            VBox pVbox = (VBox) playerNodes.get(p.getNickname());
            for(Node nodeLabel: pVbox.getChildren()) {
                Label label = (Label) nodeLabel;
                if(label.getId().equals("turn"))
                    Platform.runLater(() -> label.setText(text));
            }
        }
    }

    /**
     * Updates player container connection status of the player which has disconnected or reconnected.
     * @param player Player which needs its connection status label to be updated
     */
    private void playerContainerUpdateConnection(PlayerLobby player) {
        VBox pVbox = (VBox) playerNodes.get(player.getNickname());
        for(Node labelNode: pVbox.getChildren()) {
            Label label = (Label) labelNode;
            if(label.getId().equals("online")) {
                Platform.runLater(() -> label.setText(state.getPlayerState(player).isConnected() ? "online" : "disconnected"));
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
                displayCard(pickables.get(i).getId(),pickables.get(i).getVisibleSide(),pickablesViews.get(i));
                int finalI = i;
                pickablesViews.get(i).setOnMouseClicked(mouseEvent -> networkHandler.pickCard(pickables.get(finalI)));
            }

            List<CardObjectiveIF> commonObjectives=state.getCommonObjectives();
            displayCard(commonObjectives.get(0).getId(),Side.SIDEBACK,objDeck);
            displayCard(commonObjectives.get(0).getId(),Side.SIDEFRONT,commonObj1);
            displayCard(commonObjectives.get(1).getId(),Side.SIDEFRONT,commonObj2);
        });
    }

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
                networkHandler.playCard(finalHandPlayable.get(handIndex), coordinates, handCardSides.get(handIndex)); //todo eccezione index out of bound (2 con handplayable grande 2, come è possibile che sia capitato? avevo fatto vari switchplayer)
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
        System.out.println(prefWidth+" "+fieldScrollPane.getWidth()+" "+ fieldContainer.getWidth()+" "+fieldContainer.getPrefWidth());
        if(firstFieldScrollAdjustment) {
            scrollX = 0.5;
            scrollY = 0.5;
            firstFieldScrollAdjustment = false;
        } else {
            scrollX = prefWidth == 2* fieldScrollPane.getWidth() ? fieldScrollPane.getHvalue() :
                    fieldScrollPane.getHvalue() * prefWidth / fieldContainer.getPrefWidth();
//                    + 0.5 * (prefWidth-fieldContainer.getPrefWidth()) / fieldContainer.getPrefWidth();
            scrollY = prefHeight == 2 * fieldScrollPane.getHeight() ? fieldScrollPane.getVvalue() :
                    fieldScrollPane.getVvalue() * prefHeight / fieldContainer.getPrefHeight();
//                    + 0.0 * (prefHeight-fieldContainer.getPrefHeight()) / fieldContainer.getPrefHeight();
        }

        // Update the preferred size of the StackPane
        fieldContainer.setPrefSize(prefWidth, prefHeight);
        System.out.println(scrollX+" "+scrollY+" "+fieldScrollPane.getHvalue()+" "+fieldScrollPane.getVvalue());

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
                if(!flowCardPlaced)
                    actionLabel.setText("Play a card");
                else
                    actionLabel.setText("Pick a card");
            } else
                actionLabel.setText("Wait for your turn");
        });
    }

    /**
     * Method associated to the button for zooming in the field
     */
    public void onFieldZoomButtonAction() {
        if(fieldContainer.getScaleX()<3) {
            fieldContainer.setScaleX(fieldContainer.getScaleX() * 1.1);
            fieldContainer.setScaleY(fieldContainer.getScaleY() * 1.1);
        }
    }

    /**
     * Method associated to the button for zooming out the field
     */
    public void onFieldDezoomButtonAction() {
        if(fieldContainer.getScaleX()>0.1) {
            fieldContainer.setScaleX(fieldContainer.getScaleY() / 1.1);
            fieldContainer.setScaleY(fieldContainer.getScaleY() / 1.1);
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
     * Initializes the variables associated to counter labels, and loads the images for the counter
     */
    private void initCounter() {
        counterLabels = Map.of(
                Resource.PLANT, plantCounterLabel,
                Resource.ANIMAL, animalCounterLabel,
                Resource.FUNGUS, fungusCounterLabel,
                Resource.INSECT, insectCounterLabel,
                Resource.QUILL, quillCounterLabel,
                Resource.INKWELL, inkwellCounterLabel,
                Resource.MANUSCRIPT, manuscriptCounterLabel
        );
        plantCounterImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/symbols/plant.png"))));
        animalCounterImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/symbols/animal.png"))));
        fungusCounterImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/symbols/fungus.png"))));
        insectCounterImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/symbols/insect.png"))));
        inkwellCounterImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/symbols/inkwell.png"))));
        quillCounterImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/symbols/quill.png"))));
        manuscriptCounterImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/symbols/manuscript.png"))));
    }

    /**
     * Initializes the score tracker view and creates all the tokens as image views.
     * Places all the tokens at the position 0
     */
    private void initScoreTracker() {
        Platform.runLater(() -> {
            scoreTrackerView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/scoreTracker.png"))));
            double xDim = scoreTrackerContainer.getWidth()==0 ? scoreTrackerView.getFitWidth() : scoreTrackerContainer.getWidth();
            double yDim = scoreTrackerContainer.getHeight()==0 ? scoreTrackerView.getFitHeight() : scoreTrackerContainer.getHeight();
//            System.out.println(xDim+" "+yDim);

            for(PlayerLobby p : state.getPlayers()) {
                savedPoints.put(p, 0);
                ImageView tokenImg = createTokenImage(p);
                tokenImg.setFitHeight(tokenDimRel2x * xDim);
                tokenImg.setFitWidth(tokenDimRel2x * xDim);
                tokenImgs.put(p, tokenImg);

                StackPane.setAlignment(tokenImg, javafx.geometry.Pos.BOTTOM_LEFT);
                tokenImg.setTranslateX(xTranslToken.getFirst() * xDim);
                tokenImg.setTranslateY(yTranslToken.getFirst() * yDim);
                scoreTrackerContainer.getChildren().add(tokenImg);
            }
            tokenImgs.get(thisPlayer).toFront();
        });
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
     * Updates the position of the token image on the score tracker, according to the actual points of the player.
     * It animates the movement making the token pass through all the intermediate steps
     * @param player Player whose token is to be moved
     */
    private void updateTokenPosition(PlayerLobby player) {
        Platform.runLater(() -> {
            int points = state.getPlayerState(player).getPoints();
            if(savedPoints.get(player) < 29) {
                if(points > 29)
                    points = 29;
                ImageView token = tokenImgs.get(player);

                double xDim = scoreTrackerContainer.getWidth();
                double yDim = scoreTrackerContainer.getHeight();
                int currentPoints = this.savedPoints.get(player);

                Path path = new Path();
                path.setVisible(false);
                path.setLayoutX(tokenDimRel2x*xDim/2);
                path.setLayoutY(tokenDimRel2x*xDim/2);
                scoreTrackerContainer.getChildren().add(path);
                path.getElements().add(new MoveTo(xTranslToken.get(currentPoints) * xDim,
                        yTranslToken.get(currentPoints) * yDim ));
                for(int point=currentPoints+1 ; point<=points ; point++) {
                    path.getElements().add(new LineTo(
                            xTranslToken.get(point) * xDim,
                            yTranslToken.get(point) * yDim));
                    System.out.println(point);
                }
                PathTransition pathTransition = new PathTransition();
                pathTransition.setDuration(Duration.seconds(0.5*(points-currentPoints))); // Set animation duration
                pathTransition.setPath(path);
                pathTransition.setNode(token);
                pathTransition.setCycleCount(1); // Play once
                pathTransition.play();
            }
            this.savedPoints.replace(player, state.getPlayerState(player).getPoints());
        });
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
