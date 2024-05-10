package it.polimi.ingsw.am13;

import it.polimi.ingsw.am13.controller.GameController;
import it.polimi.ingsw.am13.controller.GameListener;
import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.CardStarterIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link GameListener} only used for tests.
 * The only thing that it does is to store the player with which it is created
 */
public class LisForTest implements GameListener {

    private final PlayerLobby player;

    public ControlAction lastAction;

    public LisForTest(PlayerLobby player) {
        this.player = player;
    }

    public LisForTest(String nick, ColorToken color) {
        this.player = new PlayerLobby(nick, color);
    }

    @Override
    public PlayerLobby getPlayer() {
        return player;
    }

    @Override
    public Long getPing() {
        return null;
    }

    @Override
    public void updatePlayerJoinedRoom(PlayerLobby player) {
        lastAction = ControlAction.JOIN_ROOM;
    }

    @Override
    public void updatePlayerLeftRoom(PlayerLobby player) {
        lastAction = ControlAction.LEAVE_ROOM;
    }

    @Override
    public void updateStartGame(GameModelIF model, GameController controller) {
        lastAction = ControlAction.START_GAME;
    }

    @Override
    public void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter, List<Coordinates> availableCoords) {
        lastAction = ControlAction.PLAY_STARTER;
    }

    @Override
    public void updateChosenPersonalObjective(PlayerLobby player, CardObjectiveIF chosenObj) {
        lastAction = ControlAction.CHOOSE_OBJ;
    }

    @Override
    public void updateNextTurn(PlayerLobby player) {
        lastAction = ControlAction.NEXT_TURN;
    }

    @Override
    public void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayed, Coordinates coord, int points, List<Coordinates> availableCoords) {
        lastAction = ControlAction.PLAY;
    }

    @Override
    public void updatePickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards, CardPlayableIF pickedCard) {
        lastAction = ControlAction.PICK;
    }

    @Override
    public void updatePoints(Map<PlayerLobby, Integer> pointsMap) {
        lastAction = ControlAction.EXTRAN_POINTS;
    }

    @Override
    public void updateWinner(PlayerLobby winner) {
        lastAction =  ControlAction.WINNER;
    }

    @Override
    public void updateEndGame() {
        lastAction = ControlAction.END_GAME;
    }

    @Override
    public void updatePlayerDisconnected(PlayerLobby player) {
        lastAction = ControlAction.DISCONNECTED;
    }

    @Override
    public void updatePlayerReconnected(PlayerLobby player) {
        lastAction = ControlAction.RECONNECTED;
    }

    @Override
    public void updateFinalPhase() {
        lastAction = ControlAction.FINAL_PHASE;
    }

    @Override
    public void updateInGame() {
        lastAction = ControlAction.IN_GAME;
    }

    @Override
    public void updateGameModel(GameModelIF model) {
        lastAction = ControlAction.UPDATE_GAMEMODEL;
    }

    @Override
    public void updatePing() {
        lastAction = ControlAction.UPDATE_PING;
    }
}
