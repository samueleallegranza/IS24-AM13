package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.view.tui.Log;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.*;
import java.util.stream.Stream;


//TODO: x Matteo, sistema il scrollPane, le barre di scorrimente non funzinano

public class ViewGUIControllerMatch extends ViewGUIController {

    @FXML
    private Button zoom;
    @FXML
    private Button deZoom;
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
    private StackPane fieldContainer;

    @FXML
    private GridPane playersContainer;
    private Map<String, Node> playerNodes;

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

    private GameState state;
    private PlayerLobby thisPlayer;
    private PlayerLobby displayPlayer;
    private List<Side> handCardSides;
    private boolean flowCardPlaced;
    private ImageView attemptedToPlayCardHand;
    private ImageView attemptedToPlayCardField;
    private Rectangle attemptedToPlayCardBox;
    private List<CardPlayableIF> handPlayable;
    private List<ImageView> handCards;
    private List<Button> flipButtons;


    /**
     * Area of non-editable text for showing logs
     */
    @FXML
    private TextArea logArea;

    /**
     * Handler of the logs
     */
    private Log log;


    private static final Integer imageW=150,imageH=100,cornerX=33,cornerY=40;

    // ----------------------------------------------------------------
    //      CONTROLLER METHODS
    // ----------------------------------------------------------------


    private void showLastLog() {
        Platform.runLater(() -> logArea.appendText(log.getLogMessages().getFirst() + "\n"));
    }

    private void showLastLog(int nLogs) {
        Platform.runLater(() -> {
            for(int i=nLogs-1 ; i>=0 ; i--)
                logArea.appendText(log.getLogMessages().get(i) + "\n");
        });
    }

    @Override
    public void setThisPlayer(PlayerLobby thisPlayer) {
        this.thisPlayer = thisPlayer;
    }

    @Override
    public void setGameState(GameState gameState) {
        this.state=gameState;
        log = new Log(gameState);

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


        //todo sometimes we get a player doesn't have this card exception, even if should be a req not met exception
//        if (e instanceof RequirementsNotMetException){
            if(state.getCurrentPlayer().equals(thisPlayer) && !flowCardPlaced) {
                Platform.runLater(() -> {
//                    handCardsContainer.getChildren().add(attemptedToPlayCardHand);
                    attemptedToPlayCardHand.setVisible(true);
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
    //            List<CardPlayableIF> handPlayable=null;
    //            for(PlayerLobby playerLobby : state.getPlayers())
    //                if(playerLobby.equals(player)) {
    //                    handPlayable=state.getPlayerState(playerLobby).getHandPlayable();
    //                }
    //            for (int i = 0; i < handPlayable.size(); i++) {
    //                int posX=(imageW+10)*i,posY=50;
    //                ImageView handCard=handCards.;
    //
    //                makeDraggable(i, handCard);
    //            }

                });
            }
//        }
    }



    @Override
    public void showInGame() {
        // Displaying the pickable cards and the common objectives
        displayPickablesAndCommonObjs();
        // At the beginning of the game, the pickable cards shouldn't be clickable
        pickablesContainer.setMouseTransparent(true);

        handCards=(Stream.of(handCard0,handCard1,handCard2).toList());
        flipButtons=Stream.of(flipButton0,flipButton1,flipButton2).toList();

        handCardSides=new ArrayList<>();

        //display hand playable, set the initial side of the hand cards to front
        for (int i = 0; i < 3; i++) {
            handCardSides.add(Side.SIDEFRONT);
        }

        displayPlayer=thisPlayer;
        handPlayable=new ArrayList<>(state.getPlayerState(displayPlayer).getHandPlayable());
        displayField();
        displayHandPlayable();
        flowCardPlaced=false;

        createZoomDeZoomButtons();

        // Players container
        this.playerNodes = new HashMap<>();
        this.displayPlayer = this.thisPlayer;
        initPlayerContainer();

        log.logNextTurn();
        showLastLog();
    }

    @Override
    public void showPlayedCard(PlayerLobby player, Coordinates coord) {

        if(this.thisPlayer.equals(player)) {
            flowCardPlaced = true;
            // After a card has been played, the pickable cards should be clickable
            pickablesContainer.setMouseTransparent(false);
        }
        if(player.equals(displayPlayer)){
            for (int i = 0; i < handPlayable.size(); i++) {
                if(!state.getPlayerState(player).getHandPlayable().contains(handPlayable.get(i)))
                    handPlayable.set(i,null);
            }
            displayHandPlayable();
            displayField();
        }

         playersContainerUpdatePoints(player);

        log.logPlayedCard(player, coord);
        showLastLog();

    }

    @Override
    public void showPickedCard(PlayerLobby player) {
        if (this.thisPlayer.equals(player)){
            //System.out.println("Client player: " + thisPlayer.getNickname() + ". Player who picked a card: " + player.getNickname());
//            Platform.runLater(this::updateHandPlayable);
            // After a card has been picked, the pickable cards should not be clickable
            pickablesContainer.setMouseTransparent(true);
        }
        if(player.equals(displayPlayer)){
            List<CardPlayableIF> updatedHandPlayable=state.getPlayerState(player).getHandPlayable();
            CardPlayableIF pickedCard=null;
            for(CardPlayableIF card : updatedHandPlayable)
                if(!handPlayable.contains(card))
                    pickedCard=card;
            for (int i = 0; i < handPlayable.size(); i++) {
                if(handPlayable.get(i)==null)
                    handPlayable.set(i,pickedCard);
            }
            displayField();
            displayHandPlayable();
        }
        displayPickablesAndCommonObjs();

        log.logPickedCard(player);
    }

    @Override
    public void showNextTurn() {
        if (displayPlayer.equals(thisPlayer) && state.getCurrentPlayer().equals(thisPlayer)) {
            for (int i = 0; i < handPlayable.size(); i++) {
//                if(handPlayable.get(i)!=null) { //this should be useless
                    ImageView handCard = handCards.get(i);
                    makeDraggable(i, handCard);
//                }
            }
        }
        if(state.getCurrentPlayer().equals(thisPlayer))
            flowCardPlaced=false;
        playersContainerUpdateTurns();

        log.logNextTurn();
        showLastLog(2);
    }

    @Override
    public void showPlayerDisconnected(PlayerLobby player) {
        playerContainerUpdateConnection(player);
        //if I am currently watching a player who disconnected, I automatically switch the view back to mine
//        if(player.equals(displayPlayer)){
//            displayPlayer=thisPlayer;
//            handPlayable=new ArrayList<>(state.getPlayerState(displayPlayer).getHandPlayable());
//            displayField();
//            displayHandPlayable();
//        }

        log.logDisconnect(player);
        showLastLog();
    }

    @Override
    public void showPlayerReconnected(PlayerLobby player) {
        playerContainerUpdateConnection(player);

        log.logReconnect(player);
        showLastLog();
    }

    // >>> Following methods are ghosts <<<
    //TODO pensa meglio a questa cosa
    @Override
    public void showStartupScreen(boolean isSocket, String ip, int port) {}
    @Override
    public void setRoom(RoomIF room) {}
    @Override
    public void showPlayedStarter(PlayerLobby player) {}
    @Override
    public void showChosenPersonalObjective(PlayerLobby player) {}
    @Override
    public void showRooms(List<RoomIF> rooms) {}
    @Override
    public void showPlayerJoinedRoom(PlayerLobby player) {}
    @Override
    public void showStartGame(GameState state) {}

    @Override
    public synchronized void showFinalPhase() {
        log.logFinalPhase();
        showLastLog();
    }

    @Override
    public synchronized void showUpdatePoints() {
//        for(PlayerLobby playerLobby : state.getPlayers())
//            playersContainerUpdatePoints(playerLobby);
    }

    @Override
    public synchronized void showWinner() {
//        System.out.println("not good");
    }

    @Override
    public synchronized void showEndGame() {

    }

    // ----------------------------------------------------------------
    //      UTILS: PLAYER CONTAINER
    // ----------------------------------------------------------------
    private void flipCard(int i, ImageView handCard){
        //if (finalI != playedCardIndex)  this shouldn't be necessary anymore
        if (handCardSides.get(i) == Side.SIDEFRONT) {
            handCardSides.set(i, Side.SIDEBACK);

        } else {
            handCardSides.set(i, Side.SIDEFRONT);
        }
        displayCard(handPlayable.get(i).getId(), handCardSides.get(i), handCard);
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
                            makeDraggable(i, handCard);
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
                else
                    handCards.get(i).setVisible(false);
            }
            displayCard(state.getPlayerState(displayPlayer).getHandObjective().getId(), Side.SIDEFRONT, handObjective);
        });
    }

    /**
     * this method is deprecated, I leave it here just because we might want to make partial updates only
     * of display player in the future. Remember that if you do use this, the code in it must be adapted.
     */
//    private void updateHandPlayable(){
//        CardPlayableIF lastPlayedCard=handPlayable.get(playedCardIndex);
//        List<CardPlayableIF> remainingHandCards=new ArrayList<>();
//        for (int i = 0; i < handPlayable.size(); i++){
//            if(i!=playedCardIndex)
//                remainingHandCards.add(handPlayable.get(i));
//        }
//        List<CardPlayableIF> updatedHandPlayable=new ArrayList<>(state.getPlayerState(thisPlayer).getHandPlayable());
//        ImageView handCard=handCards.get(playedCardIndex);
//        handCard.setVisible(true);
//        for (int i = 0; i < handPlayable.size(); i++)
//            if(!updatedHandPlayable.get(i).getId().equals(remainingHandCards.get(0).getId()) && !updatedHandPlayable.get(i).getId().equals(remainingHandCards.get(1).getId()))
//                handPlayable.set(playedCardIndex,updatedHandPlayable.get(i));
//
//
//        displayCard(handPlayable.get(playedCardIndex).getId(), Side.SIDEFRONT, handCard);
//        Button flipHandCard = flipButtons.get(playedCardIndex);
//
//        flipHandCard.setOnMouseClicked(mouseEvent -> flipCard(playedCardIndex,handCard));
//    }

    void initPlayerContainer() {
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

    void playersContainerUpdatePoints(PlayerLobby player) {
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

    void switchToPlayer(PlayerLobby displayPlayer) {
        this.displayPlayer=displayPlayer;
        handPlayable=new ArrayList<>(state.getPlayerState(displayPlayer).getHandPlayable());
        displayField();
        displayHandPlayable();
//        Platform.runLater(() -> {
////            System.out.println(this.state.getPlayers().get(playerIdx).getNickname());
//            if(!thisPlayer.equals(state.getCurrentPlayer()) && !state.getPlayers().get(playerIdx).equals(displayPlayer)) {
//                int sideIndex=playerIndexToSidePos.get(playerIdx);
//                for (int i = 0; i < playerIndexToSidePos.size(); i++) {
//                    if(playerIndexToSidePos.get(i)==-1)
//                        playerIndexToSidePos.set(i,sideIndex);
//                }
//                playerIndexToSidePos.set(playerIdx,-1);
//                List<Node> tmp = List.copyOf(sideFields.get(sideIndex).getChildren());
//                sideFields.get(sideIndex).getChildren().removeAll(sideFields.get(sideIndex).getChildren());
//                sideFields.get(sideIndex).getChildren().addAll(fieldContainer.getChildren());
//                fieldContainer.getChildren().removeAll(fieldContainer.getChildren());
//                fieldContainer.getChildren().addAll(tmp);
//
//                tmp = List.copyOf(sideHands.get(sideIndex).getChildren());
//                sideHands.get(sideIndex).getChildren().removeAll(sideHands.get(sideIndex).getChildren());
//                sideHands.get(sideIndex).getChildren().addAll(handCardsContainer.getChildren());
//                handCardsContainer.getChildren().removeAll(handCardsContainer.getChildren());
//                handCardsContainer.getChildren().addAll(tmp);
//                displayPlayer=state.getPlayers().get(playerIdx);
//            }
//        });
    }

    /**
     * Updates player container labels representing the turn of the players. Every turn label is set to "waiting"
     * except for the player currently playing, which will be set to "TURN".
     */
    private void playersContainerUpdateTurns() {
        for(PlayerLobby p: this.state.getPlayers()) {
            VBox pVbox = (VBox) playerNodes.get(p.getNickname());
            for(Node nodeLabel: pVbox.getChildren()) {
                Label label = (Label) nodeLabel;
                if(label.getId().equals("turn"))
                    Platform.runLater(() -> label.setText(this.state.getCurrentPlayer().equals(p) ? "TURN" : "waiting"));
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

    private void clearHandPlayable(){
        for (ImageView handCard : handCards) {
            handCard.setImage(null);
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

            List<CardPlayableIF> pickables=state.getPickables();
            List<ImageView> pickablesViews= Stream.of(resDeck,resPick1,resPick2,gldDeck,gldPick1,gldPick2).toList();
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

    private void makeDraggable(int handPlayableIndex, ImageView imageView){
//        Platform.runLater(() -> {
            imageView.setId(String.valueOf(handPlayableIndex));
            imageView.setOnDragDetected(event -> {
                Dragboard db = imageView.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(imageView.getId());
                db.setContent(content);
                event.consume();
            });
            imageView.setOnDragDone(event -> {
                if (event.getTransferMode() == TransferMode.MOVE) {
//                    imageView.setImage(null);
                    imageView.setVisible(false);
                    attemptedToPlayCardHand = imageView;
//                    handCardsContainer.getChildren().remove(imageView);
                    for (ImageView handCard : handCards) {
                        handCard.setOnDragDetected(null);
                    }
                }
                event.consume();
            });
//        });
    }



    // ----------------------------------------------------------------
    //      UTILS: FIELD CONTAINER
    // ----------------------------------------------------------------

    private void displayField(){
        Platform.runLater(() -> {
            fieldContainer.getChildren().clear();
            for (Coordinates coordinates : state.getPlayerState(displayPlayer).getField().getPlacedCoords()) {
                CardSidePlayableIF curPlayedCard = state.getPlayerState(displayPlayer).getField().getCardSideAtCoord(coordinates);
                if(curPlayedCard!=null) {
                    ImageView fieldCardImg = new ImageView();
                    displayCard(curPlayedCard.getId(), curPlayedCard.getSide(), fieldCardImg);
//                    System.out.println(coordinates.getPosX() + " " + coordinates.getPosY() + " " + curPlayedCard.getId() + " " + curPlayedCard.getSide());
                    int posX = (imageW - cornerX) * coordinates.getPosX();
                    int posY = (-imageH + cornerY) * coordinates.getPosY();
                    fieldCardImg.setTranslateX(posX);
                    fieldCardImg.setTranslateY(posY);
                    fieldContainer.getChildren().add(fieldCardImg);
                }
            }
            for (Coordinates coordinates : state.getPlayerState(displayPlayer).getField().getAvailableCoords()) {
//                System.out.println(coordinates.getPosX()+" "+coordinates.getPosY());
                addCardBox(coordinates, handPlayable);
            }
        });
    }
    private void createZoomDeZoomButtons(){
        zoom.setText("+");
        zoom.setOnMouseClicked(mouseEvent -> {
            if(fieldContainer.getScaleX()<3) {
                fieldContainer.setScaleX(fieldContainer.getScaleX() + 0.1);
                fieldContainer.setScaleY(fieldContainer.getScaleY() + 0.1);
            }
        });
        deZoom.setText("-");
        deZoom.setOnMouseClicked(mouseEvent -> {
            if(fieldContainer.getScaleX()>0.1) {
                fieldContainer.setScaleX(fieldContainer.getScaleY() - 0.1);
                fieldContainer.setScaleY(fieldContainer.getScaleY() - 0.1);
            }
        });
    }
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

            box.setOnDragDropped(new EventHandler<>() {
                public void handle(DragEvent event) {
                    Dragboard db = event.getDragboard();
                    int handIndex = Integer.parseInt(db.getString());
//                    System.out.println(finalHandPlayable.get(handIndex).getId()+" "+handCardSides.get(handIndex));
                    networkHandler.playCard(finalHandPlayable.get(handIndex), coordinates, handCardSides.get(handIndex));
                    Image imageHandCard;
                    String cardId = finalHandPlayable.get(handIndex).getId();
                    if (handCardSides.get(handIndex) == Side.SIDEBACK && cardId.charAt(0) != 's')
                        cardId = cardId.substring(0, 3) + '0';
                    if (handCardSides.get(handIndex) == Side.SIDEFRONT)
                        imageHandCard = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/cards/fronts/" + cardId + ".png")));
                    else
                        imageHandCard = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/cards/backs/" + cardId + ".png")));
                    ImageView newCardImg = new ImageView(imageHandCard);
                    newCardImg.setFitWidth(imageW);
                    newCardImg.setFitHeight(imageH);
                    newCardImg.setTranslateX(posX);
                    newCardImg.setTranslateY(posY);
                    attemptedToPlayCardField = newCardImg;
                    attemptedToPlayCardBox = box;
                    fieldContainer.getChildren().remove(box);
                    fieldContainer.getChildren().add(newCardImg);
                    event.setDropCompleted(true);
                    event.consume();
                }
            });
        }
        fieldContainer.getChildren().add(box);
        box.toBack();
    }

    //if you need methods related to other player to use as a reference, done on 28/05, before ~7pm


    // ----------------------------------------------------------------
    //      COMMON
    // ----------------------------------------------------------------

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


}
