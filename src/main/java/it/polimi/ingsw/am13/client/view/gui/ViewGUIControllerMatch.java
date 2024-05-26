package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class ViewGUIControllerMatch extends ViewGUIController {

    @FXML
    private StackPane fieldContainer;

    @FXML
    private Pane handCardsContainer;

    @FXML
    private Pane playersContainer;

    @FXML
    private Pane pickablesContainer;


    private GameState state;
    private PlayerLobby player;
    private List<Side> handCardSides;
    private boolean flowCardPlaced;
    private ImageView attemptedToPlayCardHand;
    private ImageView attemptedToPlayCardField;
    private Rectangle attemptedToPlayCardBox;
    private List<CardPlayableIF> handPlayable;
    private int playedCardIndex;
    private static final Integer imageW=150,imageH=100,cornerX=35,cornerY=40;

    @Override
    public void setPlayer(PlayerLobby player) {
        this.player=player;
    }

    @Override
    public void setGameState(GameState gameState) {
        this.state=gameState;
    }

    @Override
    public void showStartupScreen(boolean isSocket, String ip, int port) {

    }

    @Override
    public void showRooms(List<RoomIF> rooms) {

    }

    @Override
    public void showPlayerJoinedRoom(PlayerLobby player) {

    }

    @Override
    public void showStartGame(GameState state) {

    }

    @Override
    public void showException(Exception e) {
        //todo for now I assume that if it's the moment for the player to play, that's what caused the exception
        //use istance of? (requirementsNotMetException)
        if(state.getCurrentPlayer().equals(player) && !flowCardPlaced) {
            Platform.runLater(() -> {
                handCardsContainer.getChildren().add(attemptedToPlayCardHand);
                fieldContainer.getChildren().remove(attemptedToPlayCardField);
                fieldContainer.getChildren().add(attemptedToPlayCardBox);
                playedCardIndex=-1;
                for (int i = 0; i < handCardsContainer.getChildren().size(); i++) {
                    if (handCardsContainer.getChildren().get(i).getOnMouseClicked() == null) {
                        int finalI = i;
                        handCardsContainer.getChildren().get(i).setOnDragDetected(new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent event) {
                                Dragboard db = handCardsContainer.getChildren().get(finalI).startDragAndDrop(TransferMode.ANY);
                                ClipboardContent content = new ClipboardContent();
                                content.putString(handCardsContainer.getChildren().get(finalI).getId());
                                db.setContent(content);
                                event.consume();
                            }
                        });
                    }
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
    }

    @Override
    public void setGameId(int gameId) {

    }

    @Override
    public void showPlayedStarter(PlayerLobby player) {

    }

    @Override
    public void showChosenPersonalObjective(PlayerLobby player) {

    }

    private void displayCard(String id, Side side, ImageView imageView){
        Image cardImage;
        if(side==Side.SIDEBACK && id.charAt(0)!='s')
            id=id.substring(0,3)+'0';
        if(side==Side.SIDEFRONT)
            cardImage = new Image(getClass().getResourceAsStream("/img/cards/fronts/" + id + ".png"));
        else
            cardImage = new Image(getClass().getResourceAsStream("/img/cards/backs/" + id + ".png"));
        imageView.setImage(cardImage);
        imageView.setFitHeight(imageH);
        imageView.setFitWidth(imageW);
    }

    private void makeDraggable(int handPlayableIndex, ImageView imageView){
        imageView.setId(String.valueOf(handPlayableIndex));
        imageView.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                Dragboard db = imageView.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(imageView.getId());
                db.setContent(content);
                event.consume();
            }
        });
        imageView.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getTransferMode() == TransferMode.MOVE) {
                    //imageView.setImage(null);
                    attemptedToPlayCardHand=imageView;
                    handCardsContainer.getChildren().remove(imageView);
                    for (int i = 0; i < handCardsContainer.getChildren().size(); i++) {
                        if(handCardsContainer.getChildren().get(i).getOnDragDetected()!=null)
                            handCardsContainer.getChildren().get(i).setOnDragDetected(null);
                    }
                }
                event.consume();
            }
        });
    }

    private void addCardBox(Coordinates coordinates, List<CardPlayableIF> finalHandPlayable){
        Rectangle box1 = new Rectangle(imageW, imageH, Color.BLUE);
        int posX=(imageW-cornerX)*coordinates.getPosX();
        int posY=(-imageH+cornerY)*coordinates.getPosY();
        box1.setTranslateX(posX);
        box1.setTranslateY(posY);
        box1.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });


        box1.setOnDragExited(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                box1.setFill(Color.BLUE);
                event.consume();
            }
        });


        box1.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                int handIndex=Integer.parseInt(db.getString());
                networkHandler.playCard(finalHandPlayable.get(handIndex), coordinates,handCardSides.get(handIndex));
                Image imageHandCard;
                String cardId=finalHandPlayable.get(handIndex).getId();
                if(handCardSides.get(handIndex)==Side.SIDEBACK && cardId.charAt(0)!='s')
                    cardId=cardId.substring(0,3)+'0';
                playedCardIndex=handIndex;
                if(handCardSides.get(handIndex)==Side.SIDEFRONT)
                    imageHandCard= new Image(getClass().getResourceAsStream("/img/cards/fronts/" + cardId + ".png"));
                else
                    imageHandCard= new Image(getClass().getResourceAsStream("/img/cards/backs/" + cardId + ".png"));
                ImageView newCardImg = new ImageView(imageHandCard);
                newCardImg.setFitWidth(imageW);
                newCardImg.setFitHeight(imageH);
                newCardImg.setTranslateX(posX);
                newCardImg.setTranslateY(posY);
                attemptedToPlayCardField=newCardImg;
                attemptedToPlayCardBox=box1;
                fieldContainer.getChildren().remove(box1);
                fieldContainer.getChildren().add(newCardImg);
                event.setDropCompleted(true);
                event.consume();
            }
        });
        fieldContainer.getChildren().add(box1);
    }
    @Override
    public void showInGame() {
        flowCardPlaced=false;
        CardPlayableIF starterCard=null;
        handPlayable=null;
        playedCardIndex=-1;
        for(PlayerLobby playerLobby : state.getPlayers())
            if(playerLobby.equals(player)) {
                starterCard=state.getPlayerState(playerLobby).getStarterCard();
                handPlayable=List.copyOf(state.getPlayerState(playerLobby).getHandPlayable());
            }
        Image imageStarterSide;
        if(starterCard.getPlayedCardSide().equals(starterCard.getSide(Side.SIDEFRONT)))
            imageStarterSide=new Image(getClass().getResourceAsStream("/img/cards/fronts/"+starterCard.getId()+".png"));
        else
            imageStarterSide=new Image(getClass().getResourceAsStream("/img/cards/backs/"+starterCard.getId()+".png"));
        ImageView imageStarterSideView=new ImageView(imageStarterSide);
        imageStarterSideView.setFitWidth(imageW);
        imageStarterSideView.setFitHeight(imageH);
        imageStarterSideView.setX(0);
        imageStarterSideView.setY(0);
        fieldContainer.setAlignment(Pos.CENTER);
        fieldContainer.getChildren().add(imageStarterSideView);
        handCardSides=new ArrayList<>();
        List<CardPlayableIF> finalHandPlayable = handPlayable;
        for (int i = 0; i < 4; i++) {
            handCardSides.add(Side.SIDEFRONT);
        }
        for (int i = 0; i < handPlayable.size(); i++) {
            ImageView handCard=new ImageView();
            displayCard(handPlayable.get(i).getId(),Side.SIDEFRONT,handCard);
            handCard.setX((imageW+10)*i);
            handCard.setY(50);
            Button flipHandCard=new Button();
            flipHandCard.setTranslateX((imageW+10)*i);
            flipHandCard.setTranslateY(0);
            int finalI = i;
            flipHandCard.setOnMouseClicked(mouseEvent -> {
                if (finalI != playedCardIndex) {
                    if (handCardSides.get(finalI) == Side.SIDEFRONT) {
                        handCardSides.set(finalI, Side.SIDEBACK);
                        displayCard(finalHandPlayable.get(finalI).getId(), Side.SIDEBACK, handCard);
                    } else {
                        displayCard(finalHandPlayable.get(finalI).getId(), Side.SIDEFRONT, handCard);
                        handCardSides.set(finalI, Side.SIDEFRONT);
                    }
                }
            });
            if(state.getFirstPlayer().equals(player)) {
                makeDraggable(i, handCard);
            }
            handCardsContainer.getChildren().addAll(handCard,flipHandCard);
        }
        for(Coordinates coordinates : state.getPlayerState(player).getField().getAvailableCoords()) {
            addCardBox(coordinates,finalHandPlayable);
        }
//        imageContainer.setScaleX(0.5);
    }

    @Override
    public void showPlayedCard(PlayerLobby player, Coordinates coord) {
        if(this.player.equals(player)) {
            flowCardPlaced = true;
            List<CardPlayableIF> finalHandPlayable=handPlayable;
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
        }
    }

    @Override
    public void showPickedCard(PlayerLobby player) {

    }

    @Override
    public void showNextTurn() {

    }
}
