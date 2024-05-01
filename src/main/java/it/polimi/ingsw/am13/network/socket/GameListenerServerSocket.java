package it.polimi.ingsw.am13.network.socket;

import it.polimi.ingsw.am13.controller.GameController;
import it.polimi.ingsw.am13.controller.GameListener;
import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.CardStarterIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.network.socket.message.Message;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class GameListenerServerSocket implements GameListener {
    private final PrintWriter out;
    private PlayerLobby player;

    //FIXME: forse non serve
    private final ClientRequestsHandler clientHandler;

    public GameListenerServerSocket(PrintWriter out, PlayerLobby player, ClientRequestsHandler clientHandler) {
        this.out = out;
        this.player = player;
        this.clientHandler = clientHandler;
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
        // sendMessage(new MessageResultJoinRoom("joinRoom", "resJoinRoom", player));
    }

    @Override
    public void updatePlayerLeftRoom(PlayerLobby player) {

    }

    @Override
    public void updateGameBegins(GameController controller) {

    }

    @Override
    public void updateStartGame(GameModelIF model) {

    }

    @Override
    public void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter) {

    }

    @Override
    public void updateChosenPersonalObjective(PlayerLobby player) {

    }

    @Override
    public void updateNextTurn(PlayerLobby player) {

    }

    @Override
    public void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayable, Side side, Coordinates coord, int points, List<Coordinates> availableCoords) {

    }

    @Override
    public void updatePickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards) {

    }

    @Override
    public void updatePoints(Map<PlayerLobby, Integer> pointsMap) {

    }

    @Override
    public void updateWinner(PlayerLobby winner) {

    }

    @Override
    public void updateEndGame() {

    }

    @Override
    public void updatePlayerDisconnected(PlayerLobby player) {

    }

    @Override
    public void updatePlayerReconnected(PlayerLobby player) {

    }

    @Override
    public void updateFinalPhase() {

    }

    @Override
    public void updateInGame() {

    }

    @Override
    public void updateGameModel(GameModelIF model, GameController controller) {

    }

    @Override
    public void updatePing() {

    }

    public void setPlayer(PlayerLobby player) {
        this.player = player;
    }

    // FIXME: Reimplement
    public void sendMessage(Message message) {
        // out.println(message.toJson());
    }
}
