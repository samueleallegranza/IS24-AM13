package it.polimi.ingsw.am13.network.socket;

import it.polimi.ingsw.am13.controller.GameController;
import it.polimi.ingsw.am13.controller.GameListener;
import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.network.socket.message.Message;
import it.polimi.ingsw.am13.network.socket.message.response.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

public class GameListenerServerSocket implements GameListener {
    private final ClientRequestsHandler clientRequestsHandler;
    private final ObjectOutputStream out;
    private PlayerLobby player;

    public GameListenerServerSocket(ClientRequestsHandler clientRequestsHandler,ObjectOutputStream out, PlayerLobby player) {
        this.clientRequestsHandler = clientRequestsHandler;
        this.out = out;
        this.player = player;
    }

    @Override
    public PlayerLobby getPlayer() {
        return player;
    }

    @Override
    public Long getPing() {
        return null;
    }

    // FIXME: Reimplement
    @Override
    public void updatePlayerJoinedRoom(PlayerLobby player) {
        sendMessage(new MsgResponsePlayerJoinedRooom(player));
    }

    @Override
    public void updatePlayerLeftRoom(PlayerLobby player) {
        sendMessage(new MsgResponsePlayerLeftRoom(player));
    }

    @Override
    public void updateStartGame(GameModelIF model, GameController controller) {
        clientRequestsHandler.handleStartGame(controller);
        sendMessage(new MsgResponseStartGame(model));
    }

    @Override
    public void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter, List<Coordinates> availableCoords) {
        sendMessage(new MsgResponsePlayedStarter(player, cardStarter));
        //TODO: modifica messaggio (includi available coords)
    }

    @Override
    public void updateChosenPersonalObjective(PlayerLobby player, CardObjectiveIF chosenObj) {
        sendMessage(new MsgResponseChosenPersonalObjective(player));
        //TODO: modifica messaggio (includi chosenObj)
    }

    @Override
    public void updateNextTurn(PlayerLobby player) {
        sendMessage(new MsgResponseNextTurn(player));
    }

    @Override
    public void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayable,
                                 Coordinates coord, int points, List<Coordinates> availableCoords) {
        sendMessage(new MsgResponsePlayedCard(player, cardPlayable, null, coord, points, availableCoords));
        //TODO: modifica messaggio (elimina side, ora l'ho messo a null)
    }

    @Override
    public void updatePickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards, CardPlayableIF pickedCard) {
        sendMessage(new MsgResponsePickedCard(player, updatedVisibleCards));
        //TODO: modifica messaggio (includi picked card)
    }

    @Override
    public void updatePoints(Map<PlayerLobby, Integer> pointsMap) {
        sendMessage(new MsgResponsePointsCalculated(pointsMap));
    }

    @Override
    public void updateWinner(PlayerLobby winner) {
        sendMessage(new MsgResponseWinner(winner));
    }

    @Override
    public void updateEndGame() {
        clientRequestsHandler.handleEndGame();
        sendMessage(new MsgResponseEndGame());
    }

    @Override
    public void updatePlayerDisconnected(PlayerLobby player) {
        sendMessage(new MsgResponsePlayerDisconnected(player));
    }

    @Override
    public void updatePlayerReconnected(PlayerLobby player) {
        sendMessage(new MsgResponsePlayerReconnected(player));
    }

    @Override
    public void updateFinalPhase() {
        sendMessage(new MsgResponseFinalPhase());
    }

    @Override
    public void updateInGame() {
        sendMessage(new MsgResponseInGame());
    }

    @Override
    public void updateGameModel(GameModelIF model) {
        sendMessage(new MsgResponseUpdateGameState(model));
    }

    @Override
    public void updatePing() {
        //FIXME: Implement
    }

    public void sendError(Exception exception) {
        sendMessage(new MsgResponseError(exception));
    }

    public void setPlayer(PlayerLobby player) {
        this.player = player;
    }

    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
