package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.CardIF;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class ViewGUIControllerInit extends ViewGUIController{
    public Label chooseStarterLabel;
    public ImageView frontStarterImage;
    public ImageView backStarterImage;
    private PlayerLobby player;

    @Override
    public void setPlayer(PlayerLobby player) {
        this.player=player;
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
        chooseStarterLabel.setText("How do you want to play the starter card(front/back)?");
        CardIF starterCard=null;
        for(PlayerLobby playerLobby : state.getPlayers())
            if(playerLobby.equals(player))
                starterCard=state.getPlayerState(playerLobby).getStarterCard();
        Image imageFront=new Image(getClass().getResourceAsStream("/img/cards/fronts/"+starterCard.getId()+".png"));
        frontStarterImage.setOnMouseClicked(mouseEvent -> networkHandler.playStarter(Side.SIDEFRONT));
        frontStarterImage.setImage(imageFront);
        Image imageBack=new Image(getClass().getResourceAsStream("/img/cards/backs/"+starterCard.getId()+".png"));
        backStarterImage.setOnMouseClicked(mouseEvent -> networkHandler.playStarter(Side.SIDEBACK));
        backStarterImage.setImage(imageBack);
    }

    @Override
    public void showException(Exception e) {

    }

    @Override
    public void setGameId(int gameId) {

    }
}
