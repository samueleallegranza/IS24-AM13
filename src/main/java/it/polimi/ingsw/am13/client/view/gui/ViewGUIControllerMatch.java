package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.view.Log;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.RequirementsNotMetException;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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

public class ViewGUIControllerMatch extends ViewGUIController {

    public Button zoom;
    public Button deZoom;
    public VBox sidePlayers;
    public StackPane sideField0;
    public Pane sideHand0;
    public StackPane sideField1;
    public Pane sideHand1;
    public StackPane sideField2;
    public Pane sideHand2;
    public ImageView handCard0;
    public Button flipButton0;
    public ImageView handCard1;
    public Button flipButton1;
    public ImageView handCard2;
    public Button flipButton2;
    public ImageView side0handCard0;
    public ImageView side0handCard1;
    public ImageView side0handCard2;
    public ImageView side1handCard0;
    public ImageView side1handCard1;
    public ImageView side1handCard2;
    public ImageView side2handCard0;
    public ImageView side2handCard1;
    public ImageView side2handCard2;
    @FXML
    private StackPane fieldContainer;

    @FXML
    private HBox handCardsContainer;

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
    private List<List<CardPlayableIF>> handsPlayable;

    int thisPlayerIndex;
    private int playedCardIndex;

    private List<List<ImageView>> sideHandCards;
    private List<ImageView> handCards;
    private List<Button> flipButtons;
    private List<StackPane> sideFields;
    private List<Pane> sideHands;


    /**
     * Area of non-editable text for showing logs
     */
    @FXML
    private TextArea logArea;

    /**
     * Handler of the logs
     */
    private Log log;


    //this is a list that saves for each player index in state.getPlayers() its position in the sideFields/sideHands (-1 if it's currently being displayed)
    private List<Integer> playerIndexToSidePos;
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
                    playedCardIndex=-1;
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

        flowCardPlaced=false;
        CardPlayableIF starterCard=null;
        playedCardIndex=-1;
        boolean foundThisPlayer=false;
        sideHands=Stream.of(sideHand0,sideHand1,sideHand2).toList();
        sideFields=Stream.of(sideField0,sideField1,sideField2).toList();
        sideHandCards=new ArrayList<>();
        handCards=(Stream.of(handCard0,handCard1,handCard2).toList());
        sideHandCards.add(0,Stream.of(side0handCard0,side0handCard1,side0handCard2).toList());
        sideHandCards.add(1,Stream.of(side1handCard0,side1handCard1,side1handCard2).toList());
        sideHandCards.add(2,Stream.of(side2handCard0,side2handCard1,side2handCard2).toList());
//        sidePlayers.setVisible(false);
        flipButtons=Stream.of(flipButton0,flipButton1,flipButton2).toList();
        playerIndexToSidePos=new ArrayList<>();
        handsPlayable=new ArrayList<>();
        thisPlayerIndex=0;
        for (int i = 0; i < state.getPlayers().size(); i++) {
            PlayerLobby playerLobby=state.getPlayers().get(i);
            handsPlayable.add(new ArrayList<>(state.getPlayerState(playerLobby).getHandPlayable()));
            if(playerLobby.equals(thisPlayer)) {
                thisPlayerIndex=i;
                starterCard=state.getPlayerState(playerLobby).getStarterCard();
                foundThisPlayer=true;
                playerIndexToSidePos.add(-1);
            }
            else{
                if(foundThisPlayer)
                    playerIndexToSidePos.add(i-1);
                else
                    playerIndexToSidePos.add(i);
                showOtherPlayer(i);
            }

        }

        //display the starter card
        ImageView imageStarterSideView=new ImageView();
        if(starterCard.getPlayedCardSide().equals(starterCard.getSide(Side.SIDEFRONT)))
            displayCard(starterCard.getId(),Side.SIDEFRONT,imageStarterSideView);
        else
            displayCard(starterCard.getId(),Side.SIDEFRONT,imageStarterSideView);
        fieldContainer.setAlignment(Pos.CENTER);
        fieldContainer.getChildren().add(imageStarterSideView);
        handCardSides=new ArrayList<>();

        //display hand playable, set the initial side of the hand cards to front
        for (int i = 0; i < 3; i++) {
            handCardSides.add(Side.SIDEFRONT);
        }
        displayHandPlayable();

        //add the boxes in which cards can be played
        for(Coordinates coordinates : state.getPlayerState(thisPlayer).getField().getAvailableCoords()) {
            addCardBox(coordinates,handsPlayable.get(thisPlayerIndex));
        }
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
            List<CardPlayableIF> finalHandPlayable=handsPlayable.get(thisPlayerIndex);
            Platform.runLater(() -> {
                for (Coordinates coordinates : state.getPlayerState(player).getField().getAvailableCoords()) {
                    int posX = (imageW - cornerX) * coordinates.getPosX();
                    int posY = (-imageH + cornerY) * coordinates.getPosY();
                    boolean alreadyPresent=false;
                    for (int i = 0; i < fieldContainer.getChildren().size() && !alreadyPresent; i++) {
                        if(fieldContainer.getChildren().get(i).getTranslateX()==posX && fieldContainer.getChildren().get(i).getTranslateY()==posY)
                            alreadyPresent=true;
                    }
                    if (!alreadyPresent)
                        addCardBox(coordinates, finalHandPlayable);
                }
            });

            // After a card has been played, the pickable cards should be clickable
            pickablesContainer.setMouseTransparent(false);
        }
        else {
            int index=-1;
            for (int i = 0; i < state.getPlayers().size() && index==-1; i++) {
                if(state.getPlayers().get(i).equals(player))
                    index=i;
            }
            updateOtherPlayerPlay(index,coord);
        }

         playersContainerUpdatePoints(player, this.state.getPlayerState(player).getPoints());

        log.logPlayedCard(player, coord);
        showLastLog();
    }

    @Override
    public void showPickedCard(PlayerLobby player) {
        if (this.thisPlayer.equals(player)){
            //System.out.println("Client player: " + thisPlayer.getNickname() + ". Player who picked a card: " + player.getNickname());
            Platform.runLater(this::updateHandPlayable);
            // After a card has been picked, the pickable cards should not be clickable
            pickablesContainer.setMouseTransparent(true);
        }
        else {
            int index=-1;
            for (int i = 0; i < state.getPlayers().size() && index==-1; i++) {
                if(state.getPlayers().get(i).equals(player))
                    index=i;
            }
            updateOtherPlayerPick(index);
        }
        displayPickablesAndCommonObjs();

        log.logPickedCard(player);
    }

    @Override
    public void showNextTurn() {
        if (state.getCurrentPlayer().equals(thisPlayer)) {
            for (int i = 0; i < handsPlayable.get(thisPlayerIndex).size(); i++) {
                ImageView handCard=handCards.get(i);
                if(state.getCurrentPlayer().equals(thisPlayer)) {
                    makeDraggable(i, handCard);
                }
            }
        }

        playersContainerUpdateTurns();

        log.logNextTurn();
        showLastLog(2);
    }

    @Override
    public void showPlayerDisconnected(PlayerLobby player) {
        playerContainerUpdateConnection(player);

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



    // ----------------------------------------------------------------
    //      UTILS: PLAYER CONTAINER
    // ----------------------------------------------------------------
    private void flipCard(int i, ImageView handCard){
        //if (finalI != playedCardIndex)  this shouldn't be necessary anymore
        if (handCardSides.get(i) == Side.SIDEFRONT) {
            handCardSides.set(i, Side.SIDEBACK);
            displayCard(handsPlayable.get(thisPlayerIndex).get(i).getId(), Side.SIDEBACK, handCard);
        } else {
            displayCard(handsPlayable.get(thisPlayerIndex).get(i).getId(), Side.SIDEFRONT, handCard);
            handCardSides.set(i, Side.SIDEFRONT);
        }
    }
    private void displayHandPlayable(){
//        handPlayable=new ArrayList<>(state.getPlayerState(thisPlayer).getHandPlayable()); we have the update method, and copying from state should only be done in showInGame
        for (int i = 0; i < handsPlayable.get(thisPlayerIndex).size(); i++) {
            ImageView handCard=handCards.get(i);
            handCard.setVisible(true);
//            if(handCard)
            displayCard(handsPlayable.get(thisPlayerIndex).get(i).getId(),Side.SIDEFRONT,handCard);
            Button flipHandCard=flipButtons.get(i);
            int finalI = i;
            flipHandCard.setOnMouseClicked(mouseEvent -> flipCard(finalI,handCard));
            if(state.getCurrentPlayer().equals(thisPlayer)) {
                makeDraggable(i, handCard);
            }
        }
    }

    private void updateHandPlayable(){
        CardPlayableIF lastPlayedCard=handsPlayable.get(thisPlayerIndex).get(playedCardIndex);
        List<CardPlayableIF> remainingHandCards=new ArrayList<>();
        for (int i = 0; i < handsPlayable.get(thisPlayerIndex).size(); i++){
            if(i!=playedCardIndex)
                remainingHandCards.add(handsPlayable.get(thisPlayerIndex).get(i));
        }
        List<CardPlayableIF> updatedHandPlayable=new ArrayList<>(state.getPlayerState(thisPlayer).getHandPlayable());
        ImageView handCard=handCards.get(playedCardIndex);
//        for (int i = 0; i < handsPlayable.get(thisPlayerIndex).size(); i++){
//            if(!handCards.get(i).isVisible()){
//                handCard=handCards.get(i);
//            }
//        }
        handCard.setVisible(true);
        for (int i = 0; i < handsPlayable.get(thisPlayerIndex).size(); i++)
            if(!updatedHandPlayable.get(i).getId().equals(remainingHandCards.get(0).getId()) && !updatedHandPlayable.get(i).getId().equals(remainingHandCards.get(1).getId()))
                handsPlayable.get(thisPlayerIndex).set(playedCardIndex,updatedHandPlayable.get(i));


        displayCard(handsPlayable.get(thisPlayerIndex).get(playedCardIndex).getId(), Side.SIDEFRONT, handCard);
        Button flipHandCard = flipButtons.get(playedCardIndex);
        ImageView finalHandCard = handCard;

        flipHandCard.setOnMouseClicked(mouseEvent -> flipCard(playedCardIndex,finalHandCard));

    }

    void initPlayerContainer() {
        int playerCount = this.state.getPlayers().size();
        for(Node node: playersContainer.getChildren()) {
            int currPlayerIdx = GridPane.getRowIndex(node);
            VBox vBox = (VBox) node;
            if(currPlayerIdx < playerCount) {
                PlayerLobby currPlayer = this.state.getPlayers().get(currPlayerIdx);
                this.playerNodes.put(currPlayer.getNickname(), node); // save node association with nickname
                vBox.setOnMouseClicked((MouseEvent event) -> switchToPlayer(currPlayerIdx));
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

    void playersContainerUpdatePoints(PlayerLobby player, int points) {
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

    void switchToPlayer(int playerIdx) {
        Platform.runLater(() -> {
//            System.out.println(this.state.getPlayers().get(playerIdx).getNickname());
            if(!thisPlayer.equals(state.getCurrentPlayer()) && !state.getPlayers().get(playerIdx).equals(displayPlayer)) {
                int sideIndex=playerIndexToSidePos.get(playerIdx);
                for (int i = 0; i < playerIndexToSidePos.size(); i++) {
                    if(playerIndexToSidePos.get(i)==-1)
                        playerIndexToSidePos.set(i,sideIndex);
                }
                playerIndexToSidePos.set(playerIdx,-1);
                List<Node> tmp = List.copyOf(sideFields.get(sideIndex).getChildren());
                sideFields.get(sideIndex).getChildren().removeAll(sideFields.get(sideIndex).getChildren());
                sideFields.get(sideIndex).getChildren().addAll(fieldContainer.getChildren());
                fieldContainer.getChildren().removeAll(fieldContainer.getChildren());
                fieldContainer.getChildren().addAll(tmp);

                tmp = List.copyOf(sideHands.get(sideIndex).getChildren());
                sideHands.get(sideIndex).getChildren().removeAll(sideHands.get(sideIndex).getChildren());
                sideHands.get(sideIndex).getChildren().addAll(handCardsContainer.getChildren());
                handCardsContainer.getChildren().removeAll(handCardsContainer.getChildren());
                handCardsContainer.getChildren().addAll(tmp);
                displayPlayer=state.getPlayers().get(playerIdx);
            }
        });
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
                    Platform.runLater(() -> {
                        label.setText(this.state.getCurrentPlayer().equals(p) ? "TURN" : "waiting");
                    });
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
                Platform.runLater(() -> {
                    label.setText(state.getPlayerState(player).isConnected() ? "online" : "disconnected");
                });
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
    private void addCardBox(Coordinates coordinates, List<CardPlayableIF> finalHandPlayable){
        Rectangle box1 = new Rectangle(imageW, imageH, Color.BLUE);
        int posX=(imageW-cornerX)*coordinates.getPosX();
        int posY=(-imageH+cornerY)*coordinates.getPosY();
        box1.setTranslateX(posX);
        box1.setTranslateY(posY);

        box1.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });

        box1.setOnDragExited(event -> {
            box1.setFill(Color.BLUE);
            event.consume();
        });

        box1.setOnDragDropped(new EventHandler<>() {
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                int handIndex = Integer.parseInt(db.getString());
                networkHandler.playCard(finalHandPlayable.get(handIndex), coordinates, handCardSides.get(handIndex));
                Image imageHandCard;
                String cardId = finalHandPlayable.get(handIndex).getId();
                if (handCardSides.get(handIndex) == Side.SIDEBACK && cardId.charAt(0) != 's')
                    cardId = cardId.substring(0, 3) + '0';
                playedCardIndex = handIndex;
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
                attemptedToPlayCardBox = box1;
                fieldContainer.getChildren().remove(box1);
                fieldContainer.getChildren().add(newCardImg);
                event.setDropCompleted(true);
                event.consume();
            }
        });
        fieldContainer.getChildren().add(box1);
    }

    private void updateOtherPlayerPlay(int index, Coordinates coordinates){
        Platform.runLater(() -> {
            PlayerLobby otherPlayer = state.getPlayers().get(index);
            int sideIndex = playerIndexToSidePos.get(index);
            int posX = (imageW - cornerX) * coordinates.getPosX();
            int posY = (-imageH + cornerY) * coordinates.getPosY();

            List<CardPlayableIF> updatedHandPlayable = new ArrayList<>(state.getPlayerState(otherPlayer).getHandPlayable());
            int otherPlayedCardIndex = 0;
            for (int i = 0; i < handsPlayable.get(sideIndex).size(); i++)
                if (!updatedHandPlayable.contains(handsPlayable.get(sideIndex).get(i)))
                    otherPlayedCardIndex = i;
            sideHandCards.get(sideIndex).get(otherPlayedCardIndex).setVisible(false);
            Node boxToRemove;
            Side playedCardSide = Side.SIDEFRONT;
            for (Coordinates coord : state.getPlayerState(otherPlayer).getField().getPlacedCoords()) {
                CardSidePlayableIF cardSide = state.getPlayerState(otherPlayer).getField().getCardSideAtCoord(coord);
                System.out.println(cardSide.getColor()+" "+cardSide.getPoints());
                if (handsPlayable.get(sideIndex).get(otherPlayedCardIndex).getSide(Side.SIDEFRONT).equals(cardSide))
                    playedCardSide = Side.SIDEFRONT;
                else if (handsPlayable.get(sideIndex).get(otherPlayedCardIndex).getSide(Side.SIDEBACK).equals(cardSide))
                    playedCardSide = Side.SIDEBACK;
            }
            for (int i = 0; i < sideFields.get(sideIndex).getChildren().size(); i++) {
                if (sideFields.get(sideIndex).getChildren().get(i).getTranslateX() == posX && sideFields.get(sideIndex).getChildren().get(i).getTranslateY() == posY) {
                    boxToRemove = sideFields.get(sideIndex).getChildren().get(i);
                    sideFields.get(sideIndex).getChildren().remove(boxToRemove);
                    ImageView playedCardImage = new ImageView();
                    displayCard(handsPlayable.get(sideIndex).get(otherPlayedCardIndex).getId(), playedCardSide, playedCardImage);
                    playedCardImage.setTranslateX(posX);
                    playedCardImage.setTranslateY(posY);
                    sideFields.get(sideIndex).getChildren().add(playedCardImage);
                }
            }
            for (Coordinates curCoord : state.getPlayerState(otherPlayer).getField().getAvailableCoords()) {
                int curPosX = (imageW - cornerX) * curCoord.getPosX();
                int curPosY = (-imageH + cornerY) * curCoord.getPosY();
                boolean alreadyPresent=false;
                for (int i = 0; i < sideFields.get(sideIndex).getChildren().size() && !alreadyPresent; i++) {
                    if(sideFields.get(sideIndex).getChildren().get(i).getTranslateX()==curPosX && sideFields.get(sideIndex).getChildren().get(i).getTranslateY()==curPosY)
                        alreadyPresent=true;
                }
                if (!alreadyPresent){
                    Rectangle box = new Rectangle(imageW, imageH, Color.BLUE);
                    box.setTranslateX(curPosX);
                    box.setTranslateY(curPosY);
                    sideFields.get(sideIndex).getChildren().add(box);
                }
            }
        });
    }
    private void updateOtherPlayerPick(int index){
        Platform.runLater(() -> {
            PlayerLobby otherPlayer = state.getPlayers().get(index);
            int sideIndex = playerIndexToSidePos.get(index);

            List<CardPlayableIF> updatedHandPlayable = new ArrayList<>(state.getPlayerState(otherPlayer).getHandPlayable());
            int otherPlayedCardIndex = 0;
            for (int i = 0; i < handsPlayable.get(sideIndex).size(); i++)
                if (!updatedHandPlayable.contains(handsPlayable.get(sideIndex).get(i)))
                    otherPlayedCardIndex = i;
            for (int i = 0; i < handsPlayable.get(sideIndex).size(); i++)
                if (!handsPlayable.get(sideIndex).contains(updatedHandPlayable.get(i)))
                    handsPlayable.get(sideIndex).set(otherPlayedCardIndex, updatedHandPlayable.get(i));

            sideHandCards.get(sideIndex).get(otherPlayedCardIndex).setVisible(true);
            displayCard(handsPlayable.get(sideIndex).get(otherPlayedCardIndex).getId(), Side.SIDEBACK, sideHandCards.get(sideIndex).get(otherPlayedCardIndex));
//        for (int i = 0; i <  ; i++) {
//
//        }
//        CardSidePlayableIF playedCard=state.getPlayerState(state.getPlayers().get(index)).getField().getCardSideAtCoord(coordinates);
        });
    }
    private void showOtherPlayer(int index){
        Platform.runLater(() -> {
            int sideIndex = playerIndexToSidePos.get(index);
            PlayerLobby otherPlayer = state.getPlayers().get(index);
            CardPlayableIF starterCard = state.getPlayerState(otherPlayer).getStarterCard();
            List<CardPlayableIF> finalHandPlayable = handsPlayable.get(sideIndex);
            ImageView imageStarterSideView = new ImageView();
            if (starterCard.getPlayedCardSide().equals(starterCard.getSide(Side.SIDEFRONT)))
                displayCard(starterCard.getId(), Side.SIDEFRONT, imageStarterSideView);
            else
                displayCard(starterCard.getId(), Side.SIDEBACK, imageStarterSideView);
//        sidePlayers.setAlignment(Pos.CENTER);
            sideFields.get(sideIndex).setScaleX(0.5);
            sideFields.get(sideIndex).setScaleY(0.5);
            sideHands.get(sideIndex).setScaleX(0.5);
            sideHands.get(sideIndex).setScaleY(0.5);
            sideFields.get(sideIndex).getChildren().add(imageStarterSideView);
            for (Coordinates coordinates : state.getPlayerState(otherPlayer).getField().getAvailableCoords()) {
                Rectangle box = new Rectangle(imageW, imageH, Color.BLUE);
                int posX = (imageW - cornerX) * coordinates.getPosX();
                int posY = (-imageH + cornerY) * coordinates.getPosY();
                box.setTranslateX(posX);
                box.setTranslateY(posY);
                sideFields.get(sideIndex).getChildren().add(box);
            }
            for (int i = 0; i < finalHandPlayable.size(); i++) {
                displayCard(finalHandPlayable.get(i).getId(), Side.SIDEBACK, sideHandCards.get(sideIndex).get(i));
                sideHandCards.get(sideIndex).get(i).setTranslateX((imageW + 10) * i);
                sideHandCards.get(sideIndex).get(i).setTranslateY(50);
            }
        });
    }




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
