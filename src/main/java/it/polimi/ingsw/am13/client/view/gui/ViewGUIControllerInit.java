package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.view.Log;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.CardIF;
import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Objects;

public class ViewGUIControllerInit extends ViewGUIController {
    public Label descriptionLabel;
    public ImageView firstChoiceImage;
    public ImageView secondChoiceImage;
    private PlayerLobby player;
    private GameState state;

    /**
     * Area of non-editable text for showing logs
     */
    @FXML
    private TextArea logArea;

    /**
     * Handler of the logs
     */
    private Log log;

    private void showLastLog() {
        Platform.runLater(() -> logArea.appendText(log.getLogMessages().getLast() + "\n"));
    }

    @Override
    public void setThisPlayer(PlayerLobby thisPlayer) {
        this.player= thisPlayer;
    }

    @Override
    public void setGameState(GameState gameState) {
        log = new Log(gameState);
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
        descriptionLabel.setText("How do you want to play the starter card(front/back)?");
        CardIF starterCard=null;
        this.state=state;
        for(PlayerLobby playerLobby : state.getPlayers())
            if(playerLobby.equals(player))
                starterCard=state.getPlayerState(playerLobby).getStarterCard();
        Image imageFront=new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/cards/fronts/" + starterCard.getId() + ".png")));
        firstChoiceImage.setOnMouseClicked(mouseEvent -> networkHandler.playStarter(Side.SIDEFRONT));
        firstChoiceImage.setImage(imageFront);
        Image imageBack=new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/cards/backs/" + starterCard.getId() + ".png")));
        secondChoiceImage.setOnMouseClicked(mouseEvent -> networkHandler.playStarter(Side.SIDEBACK));
        secondChoiceImage.setImage(imageBack);

        if(ViewGUI.DEBUG_MODE) {
            firstChoiceImage.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,
                    0, 0, 0, 0,
                    MouseButton.PRIMARY,
                    1, true, true, true, true,
                    true, true, true, true, true, true,
                    null));
        }
    }

    @Override
    public void showException(Exception e) {

    }

    @Override
    public void setRoom(RoomIF room) {

    }

    @Override
    public void showPlayedStarter(PlayerLobby player) {
        if(this.player.equals(player)) {
            descriptionLabel.setText("Which personal objective do you want to choose?");
            List<CardObjectiveIF> possibleHandObjectives = null;
            for (PlayerLobby playerLobby : state.getPlayers())
                if (playerLobby.equals(player))
                    possibleHandObjectives = state.getPlayerState(playerLobby).getPossibleHandObjectives();
            Image imageFront = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/cards/fronts/" + possibleHandObjectives.get(0).getId() + ".png")));
            List<CardObjectiveIF> finalPossibleHandObjectives = possibleHandObjectives;
            firstChoiceImage.setOnMouseClicked(mouseEvent -> networkHandler.choosePersonalObjective(finalPossibleHandObjectives.get(0)));
            firstChoiceImage.setImage(imageFront);
            Image imageBack = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/cards/fronts/" + possibleHandObjectives.get(1).getId() + ".png")));
            secondChoiceImage.setOnMouseClicked(mouseEvent -> networkHandler.choosePersonalObjective(finalPossibleHandObjectives.get(1)));
            secondChoiceImage.setImage(imageBack);
        }
        log.logPlayedStarter(player);
        showLastLog();

        if(ViewGUI.DEBUG_MODE) {
            firstChoiceImage.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,
                    0, 0, 0, 0,
                    MouseButton.PRIMARY,
                    1, true, true, true, true,
                    true, true, true, true, true, true,
                    null));
        }
    }

    @Override
    public void showChosenPersonalObjective(PlayerLobby player) {
        if(this.player.equals(player)) {
            Platform.runLater(() -> {
                descriptionLabel.setText("You have chosen your personal objective.\n Now wait for the others to finish their initial phase");
                firstChoiceImage.setImage(null);
                secondChoiceImage.setImage(null);
            });
        }
        log.logChosenPersonalObjective(player);
        showLastLog();
    }

    @Override
    public void showInGame() {

    }

    @Override
    public void showPlayedCard(PlayerLobby player, Coordinates coord) {

    }

    @Override
    public void showPickedCard(PlayerLobby player) {

    }

    @Override
    public void showNextTurn() {

    }
}
