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
import it.polimi.ingsw.am13.network.socket.message.response.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link GameListener} only used for tests.
 * The only thing that it does is to store the player with which it is created
 */
public class LisForTest implements GameListener {

    private final PlayerLobby player;

    public boolean stopPing;
    private long ping;

    public GameController controller;

    public GameModelIF model;

    public final List<ControlAction> actions = new ArrayList<>();
    public final List<MsgResponse> updates = new ArrayList<>();

    public LisForTest(PlayerLobby player) {
        this.player = player;
        this.controller = null;
        this.stopPing = false;
    }

    public LisForTest(String nick, ColorToken color) {
        this(new PlayerLobby(nick, color));
    }

    @Override
    public PlayerLobby getPlayer() {
        return player;
    }

    @Override
    public Long getPing() {
        if(!stopPing)
            ping = System.currentTimeMillis();
        return ping;
    }

    @Override
    public void updatePlayerJoinedRoom(PlayerLobby player) {
        actions.add(ControlAction.JOIN_ROOM);
        updates.add(new MsgResponsePlayerJoinedRooom(player));
    }

    @Override
    public void updatePlayerLeftRoom(PlayerLobby player) {
        actions.add(ControlAction.LEAVE_ROOM);
        updates.add(new MsgResponsePlayerLeftRoom(player));
    }

    @Override
    public void updateStartGame(GameModelIF model, GameController controller) {
        this.controller = controller;
        this.model = model;
        actions.add(ControlAction.START_GAME);
        updates.add(new MsgResponseStartGame(model));
    }

    @Override
    public void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter, List<Coordinates> availableCoords) {
        actions.add(ControlAction.PLAY_STARTER);
        updates.add(new MsgResponsePlayedStarter(player, cardStarter, availableCoords));
    }

    @Override
    public void updateChosenPersonalObjective(PlayerLobby player, CardObjectiveIF chosenObj) {
        actions.add(ControlAction.CHOOSE_OBJ);
        updates.add(new MsgResponseChosenPersonalObjective(player, chosenObj));
    }

    @Override
    public void updateNextTurn(PlayerLobby player) {
        actions.add(ControlAction.NEXT_TURN);
        updates.add(new MsgResponseNextTurn(player));
    }

    @Override
    public void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayed, Coordinates coord, int points, List<Coordinates> availableCoords) {
        actions.add(ControlAction.PLAY);
        updates.add(new MsgResponsePlayedCard(player, cardPlayed, coord, points, availableCoords));
    }

    @Override
    public void updatePickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards, CardPlayableIF pickedCard) {
        actions.add(ControlAction.PICK);
        updates.add(new MsgResponsePickedCard(player, updatedVisibleCards, pickedCard));
    }

    @Override
    public void updatePoints(Map<PlayerLobby, Integer> pointsMap) {
        actions.add(ControlAction.EXTRAN_POINTS);
        updates.add(new MsgResponsePointsCalculated(pointsMap));
    }

    @Override
    public void updateWinner(PlayerLobby winner) {
        actions.add( ControlAction.WINNER);
        updates.add(new MsgResponseWinner(winner));
    }

    @Override
    public void updateEndGame() {
        actions.add(ControlAction.END_GAME);
        updates.add(new MsgResponseEndGame());
    }

    @Override
    public void updatePlayerDisconnected(PlayerLobby player) {
        actions.add(ControlAction.DISCONNECTED);
        updates.add(new MsgResponsePlayerDisconnected(player));
    }

    /**
     * Updates the client that a player has disconnected and the corresponding socket must be closed.
     */
    @Override
    public void updateCloseSocket() {
        //TODO: gestisci

    }

    @Override
    public void updatePlayerReconnected(PlayerLobby player) {
        actions.add(ControlAction.RECONNECTED);
        updates.add(new MsgResponsePlayerReconnected(player));
    }

    @Override
    public void updateFinalPhase() {
        actions.add(ControlAction.FINAL_PHASE);
        updates.add(new MsgResponseFinalPhase());
    }

    @Override
    public void updateInGame() {
        actions.add(ControlAction.IN_GAME);
        updates.add(new MsgResponseInGame());
    }

    // NOTA: uso messaggio per joined room
    @Override
    public void updateGameModel(GameModelIF model, GameController controller, PlayerLobby player) {
        actions.add(ControlAction.UPDATE_GAMEMODEL);
        updates.add(new MsgResponsePlayerJoinedRooom(player));
    }

    @Override
    public void updatePing() {
        actions.add(ControlAction.UPDATE_PING);
        updates.add(new MsgResponsePing());
    }
}
