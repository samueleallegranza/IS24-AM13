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

    /**
     * The ping time of the client.
     */
    private Long ping;

    /**
     * The handler of the client requests.
     */
    private transient final ClientRequestsHandler clientRequestsHandler;
    private transient final ObjectOutputStream out;

    /**
     * The player associated with this listener
     */
    private PlayerLobby player;

    public GameListenerServerSocket(ClientRequestsHandler clientRequestsHandler,ObjectOutputStream out, PlayerLobby player) {
        this.clientRequestsHandler = clientRequestsHandler;
        this.out = out;
        this.player = player;
        this.ping = System.currentTimeMillis();
    }

    @Override
    public PlayerLobby getPlayer() {
        return player;
    }

    @Override
    public Long getPing() {
        return ping;
    }

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
        sendMessage(new MsgResponsePlayedStarter(player, cardStarter, availableCoords));
    }

    @Override
    public void updateChosenPersonalObjective(PlayerLobby player, CardObjectiveIF chosenObj) {
        sendMessage(new MsgResponseChosenPersonalObjective(player, chosenObj));
    }

    @Override
    public void updateNextTurn(PlayerLobby player) {
        sendMessage(new MsgResponseNextTurn(player));
    }

    @Override
    public void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayable,
                                 Coordinates coord, int points, List<Coordinates> availableCoords) {
        sendMessage(new MsgResponsePlayedCard(player, cardPlayable, coord, points, availableCoords));
    }

    @Override
    public void updatePickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards, CardPlayableIF pickedCard) {
        sendMessage(new MsgResponsePickedCard(player, updatedVisibleCards, pickedCard));
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
    public void updateCloseSocket(){
        clientRequestsHandler.handleDisconnection();
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
    public void updateGameModel(GameModelIF model, GameController controller, PlayerLobby player) {
        sendMessage(new MsgResponseUpdateGameState(model, player));
    }

    @Override
    public void updatePing() {
        ping = System.currentTimeMillis();
        sendMessage(new MsgResponsePing());
    }

    /**
     * Sends an error message to the client
     * @param exception The exception that caused the error
     */
    public void sendError(Exception exception) {
        sendMessage(new MsgResponseError(exception));
    }

    /**
     * Sets the player associated with this listener
     * @param player The player to set
     */
    public void setPlayer(PlayerLobby player) {
        this.player = player;
    }

    /**
     * Sends a message to the client
     * @param message The message to send
     */
    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
            out.reset();

            // for debugging purposes only
            MsgResponse msg = (MsgResponse) message;
            if (msg.getClass()!= MsgResponsePing.class)
                clientRequestsHandler.logResponse(msg.getType());
        } catch (IOException e) {
            //TODO: handle better
            throw new RuntimeException(e);
        }
    }


}
