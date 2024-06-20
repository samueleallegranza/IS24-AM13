package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.ParametersClient;
import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.model.card.CardIF;
import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Objects;

/**
 * Controller of 'Init' scene, where the player must complete the initialization part of the game
 */
public class ViewGUIControllerInit extends ViewGUIController {

    @FXML
    public Label descriptionLabel;
    @FXML
    public ImageView firstChoiceImage;
    @FXML
    public ImageView secondChoiceImage;

    /**
     * The player associated to this {@link ViewGUIControllerInit}
     */
    private PlayerLobby thisPlayer;
    /**
     * The representation of the state of the game
     */
    private GameState state;
    /**
     * Handler of the logs
     */
    private LogGUI log;

    /**
     *
     * @return the title of the scene
     */
    @Override
    public String getSceneTitle() {
        return "Initial Phase";
    }

    /**
     * This method cannot be called
     * @param e Exception to be shown
     */
    @Override
    public void showException(Exception e) {
    }

    /**
     * This method cannot be called
     * @param player Player who disconnected
     */
    @Override
    public void showPlayerDisconnected(PlayerLobby player) {
    }

    /**
     * This method cannot be called
     * @param player Player who reconnected
     */
    @Override
    public void showPlayerReconnected(PlayerLobby player) {
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
     * @param state the representation of the state of the game
     */
    @Override
    public void setGameState(GameState state) {
        this.state = state;
    }

    /**
     * Shows the start of the game, by showing the two sides of the starter card the player can choose from
     */
    public void showStartGame() {
        firstChoiceImage.setMouseTransparent(false);
        secondChoiceImage.setMouseTransparent(false);
        log = new LogGUI(state);
        descriptionLabel.setText("How do you want to play the starter card(front/back)?");
        CardIF starterCard=null;
        for(PlayerLobby playerLobby : state.getPlayers())
            if(playerLobby.equals(thisPlayer))
                starterCard =state.getPlayerState(playerLobby).getStarterCard();
        Image imageFront=new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/cards/fronts/" + starterCard.getId() + ".png")));
        firstChoiceImage.setOnMouseClicked(mouseEvent -> networkHandler.playStarter(Side.SIDEFRONT));
        firstChoiceImage.setImage(imageFront);
        Image imageBack=new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/cards/backs/" + starterCard.getId() + ".png")));
        secondChoiceImage.setOnMouseClicked(mouseEvent -> networkHandler.playStarter(Side.SIDEBACK));
        secondChoiceImage.setImage(imageBack);

        if(ParametersClient.SKIP_INIT) {
            firstChoiceImage.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,
                    0, 0, 0, 0,
                    MouseButton.PRIMARY,
                    1, true, true, true, true,
                    true, true, true, true, true, true,
                    null));
        }
    }


    /**
     * If the player who played the starter card is thisPlayer, it shows the two personal objective cards
     * the player can choose from
     * @param player the player who played his starter card
     */
    public void showPlayedStarter(PlayerLobby player) {
        if(this.thisPlayer.equals(player)) {
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

        if(ParametersClient.SKIP_INIT && thisPlayer.equals(player)) {
            firstChoiceImage.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,
                    0, 0, 0, 0,
                    MouseButton.PRIMARY,
                    1, true, true, true, true,
                    true, true, true, true, true, true,
                    null));
        }
    }

    /**
     * If the player who has chosen his personal objective is thisPlayer, it shows that the player should wait
     * for the other players to finish their initial phase
     * @param player the player who has chosen his personal objective
     */
    public void showChosenPersonalObjective(PlayerLobby player) {
        if(this.thisPlayer.equals(player)) {
            descriptionLabel.setText("You have chosen your personal objective.\n Now wait for the others to finish their initial phase");
            firstChoiceImage.setImage(null);
            secondChoiceImage.setImage(null);
        }
        log.logChosenPersonalObjective(player);
//        showLastLogs();
    }

}
