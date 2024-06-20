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

/**
 * This class is the socket implementation of {@link GameListener}.
 * It handles the communication between the server and the clients.
 * It contains methods to update the game state of the clients.
 */
public class GameListenerServerSocket implements GameListener {

    /**
     * The ping time of the client.
     */
    private Long ping;

    /**
     * The handler of the client requests
     */
    private transient final ClientRequestsHandler clientRequestsHandler;

    /**
     * The output stream in which the messages to the server are written
     */
    private transient final ObjectOutputStream out;

    /**
     * The player associated with this listener
     */
    private PlayerLobby player;

    /**
     * Initialize the attributes
     * @param clientRequestsHandler the handler of the client requests
     * @param out the output stream in which the messages to the server are written
     * @param player the player associated with this listener
     */
    public GameListenerServerSocket(ClientRequestsHandler clientRequestsHandler,ObjectOutputStream out, PlayerLobby player) {
        this.clientRequestsHandler = clientRequestsHandler;
        this.out = out;
        this.player = player;
        this.ping = System.currentTimeMillis();
    }

    /**
     * @return Player corresponding to this listener
     */
    @Override
    public PlayerLobby getPlayer() {
        return player;
    }

    /**
     * @return Last stored ping
     */
    @Override
    public Long getPing() {
        return ping;
    }

    /**
     * Updates the client that a player has joined a lobby (waiting for a game)
     * @param player player who joined
     */
    @Override
    public void updatePlayerJoinedRoom(PlayerLobby player) {
        sendMessage(new MsgResponsePlayerJoinedRooom(player));
    }

    /**
     * Updates the client that a player has left a lobby (waiting for a game)
     * @param player player who left
     */
    @Override
    public void updatePlayerLeftRoom(PlayerLobby player) {
        sendMessage(new MsgResponsePlayerLeftRoom(player));
    }

    /**
     * Updates the client that the game has started: starter cards and initial cards have been given to the players.
     * The client is notified passing the {@link GameModelIF} containing a GameModel with GameStatus set to INIT.
     * @param model The game model containing current game's state for the newly started game
     * @param controller The controller of the newly started game
     */
    @Override
    public void updateStartGame(GameModelIF model, GameController controller) {
        clientRequestsHandler.handleStartGame(controller);
        sendMessage(new MsgResponseStartGame(model));
    }

    /**
     * Updates the client that a player has played their starter card.
     * @param player The player that played the starter card.
     * @param cardStarter The starter card played.
     * @param availableCoords The list of coordinates that are available to play a card
     */
    @Override
    public void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter, List<Coordinates> availableCoords) {
        sendMessage(new MsgResponsePlayedStarter(player, cardStarter, availableCoords));
    }

    /**
     * Updates the client that a player has chosen their personal objective card.
     * @param player The player that chose the personal objective card.
     * @param chosenObj Objective card chosen by the player
     */
    @Override
    public void updateChosenPersonalObjective(PlayerLobby player, CardObjectiveIF chosenObj) {
        sendMessage(new MsgResponseChosenPersonalObjective(player, chosenObj));
    }

    /**
     * Updates the client that the turn has passed to another player.
     * @param player The player that is going to play the next turn.
     */
    @Override
    public void updateNextTurn(PlayerLobby player) {
        sendMessage(new MsgResponseNextTurn(player));
    }

    /**
     * Updates the client when a player plays a card.
     * @param player The player that played the card.
     * @param cardPlayable The card played.
     * @param coord The coordinates where the card has been placed, relative to the player's field.
     * @param points The points given by the card.
     * @param availableCoords The list of coordinates that are available to play a card
     */
    @Override
    public void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayable,
                                 Coordinates coord, int points, List<Coordinates> availableCoords) {
        sendMessage(new MsgResponsePlayedCard(player, cardPlayable, coord, points, availableCoords));
    }

    /**
     * Updates the client when a player picks a card from the common visible cards.
     * @param player The player that picked the card.
     * @param updatedVisibleCards The updated list of visible cards in the common field.
     * @param pickedCard The card picked by the player
     */
    @Override
    public void updatePickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards, CardPlayableIF pickedCard) {
        sendMessage(new MsgResponsePickedCard(player, updatedVisibleCards, pickedCard));
    }

    /**
     * Updates the client when the points given by Objective cards (common and personal) have been calculated.
     * @param pointsMap A map containing the points of each player.
     */
    @Override
    public void updatePoints(Map<PlayerLobby, Integer> pointsMap) {
        sendMessage(new MsgResponsePointsCalculated(pointsMap));
    }

    /**
     * Updates the client with the winner
     * @param winners The player(s) that has won the game.
     */
    @Override
    public void updateWinner(List<PlayerLobby> winners) {
        sendMessage(new MsgResponseWinner(winners));
    }

    /**
     * Updates the client about ending of game.
     * After this update, the server should not respond to any other request from the clients
     */
    @Override
    public void updateEndGame() {
        clientRequestsHandler.handleEndGame();
        sendMessage(new MsgResponseEndGame());
    }

    /**
     * Updates the client with the player that has disconnected from the game.
     * @param player The player that has disconnected.
     */
    @Override
    public void updatePlayerDisconnected(PlayerLobby player) {
        sendMessage(new MsgResponsePlayerDisconnected(player));
    }

    /**
     * Updates the client that a player has disconnected and the corresponding socket must be closed.
     */
    @Override
    public void updateCloseSocket(){
        clientRequestsHandler.handleDisconnection();
    }

    /**
     * Updates the client with the player that has reconnected to the game.
     * @param player The player that has reconnected.
     */
    @Override
    public void updatePlayerReconnected(PlayerLobby player) {
        sendMessage(new MsgResponsePlayerReconnected(player));
    }

    /**
     * Updates the client that the game is in the final phase.
     * @param turnsToEnd Number of turns to reach the end of the turn-based phase
     */
    @Override
    public void updateFinalPhase(int turnsToEnd) {
        sendMessage(new MsgResponseFinalPhase(turnsToEnd));
    }

    /**
     * Updates the client that the game has begun the turn-based phase.
     */
    @Override
    public void updateInGame() {
        sendMessage(new MsgResponseInGame());
    }

    /**
     * @param model      The updated game model
     * @param controller The controller of the game
     * @param player     The updated player who is reconnecting.
     */
    @Override
    public void updateGameModel(GameModelIF model, GameController controller, PlayerLobby player) {
        sendMessage(new MsgResponseUpdateGameState(model, player));
    }

    /**
     * Updates last ping received, and sends the update to the client
     */
    @Override
    public void updatePing() {
        ping = System.currentTimeMillis();
        sendMessage(new MsgResponsePing());
    }

    /**
     * Updates the client with a chat message
     *
     * @param sender    of the message
     * @param receivers of the message
     * @param text      content of the message
     */
    @Override
    public void updateChatMessage(PlayerLobby sender, List<PlayerLobby> receivers, String text) {
        sendMessage(new MsgResponseChat(sender,receivers,text));
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
    private synchronized void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
            out.reset();

            // for debugging purposes only
            MsgResponse msg = (MsgResponse) message;
            if (msg.getClass() != MsgResponsePing.class)
                clientRequestsHandler.logResponse(msg.getType());
        } catch (IOException e) {
            // The client could have crashed, i do nothing
            //TODO: potrebbe essere gestita diversamente?
        }
    }


}
