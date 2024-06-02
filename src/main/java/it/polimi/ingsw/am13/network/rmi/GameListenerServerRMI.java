package it.polimi.ingsw.am13.network.rmi;
import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.network.rmi.GameListenerClientRMI;
import it.polimi.ingsw.am13.client.network.rmi.GameListenerClientRMIIF;
import it.polimi.ingsw.am13.controller.GameController;
import it.polimi.ingsw.am13.controller.GameListener;
import it.polimi.ingsw.am13.controller.LobbyException;
import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.ConnectionException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a game listener for RMI connection.
 * For each type of update, it tries asynchronously to call the corresponding method of {@link GameListenerClientRMI},
 * which is in the client (RMI remote call), for a fixed maximum number of times
 */
public class GameListenerServerRMI implements GameListener {

    //TODO: per ora problema di latenza è gestito chiamando i metodi RMI in modo asincrono con thread,
    // ma è da rivedere se veramente questo aiuta...
    // RISOLVI IL PRIMA POSSIBILE

    // TODO: sistema sistema di log di server, come x Socket (guarda ClientRequestHandler e GameListenerServerSocket)

    // TODO (possibile problema): ad ora se il server crasha, i client non se ne accorgono
    //  Forse basta gestire meglio le RemoteException di NetworkHandlerRMI...

    /**
     * Long representing the last time the ping was updated
     */
    private long ping;

    /**
     * Client-side listener, on which to call the RMI methods (to send the updates).
     * It the class that will receive the updates.
     */
    private transient final GameListenerClientRMIIF clientLis;

    /**
     * Players associated to this game listener
     */
    private final PlayerLobby player;

    /**
     * Controller of the game, null until the startGaame / reconnectGame update is received
     */
    private transient GameController controller;

    /**
     * Creates a new server-side game listener wrapping the client-side listener
     * @param clientLis Client-side listener, which will receive the updates.
     */
    public GameListenerServerRMI(GameListenerClientRMIIF clientLis, PlayerLobby player) {
        this.clientLis = clientLis;
        ping = System.currentTimeMillis();
        this.player = player;
        this.controller = null;
    }

    /**
     * @return Player corresponding to this listener
     */
    @Override
    public PlayerLobby getPlayer() {
        return player;
    }

    /**
     * @return Last updated ping
     */
    @Override
    public Long getPing() {
        return ping;
    }

    /**
     * Updates last ping received, and sends the update to the client
     */
    @Override
    public void updatePing() {
        ping = System.currentTimeMillis();
    }


    /**
     * Functional interface representing a generic call to a RMI method, hence it throws {@link RemoteException}
     */
    private interface RunnableRMI {
        void run() throws RemoteException;
    }



    //TODO: gestione di RemoteException da rivedere. Tipo, può succedere che  anche se mi arrivano i ping
    // (non disconnetto il player), non riesco a inviare l'update?


    /**
     * Tries to execute a generic RMI call for a fixed number of attempts, that it stops and do nothing.
     * This is done asynchronously via a new thread.
     * This method should be called for each RMI call in this class
     * @param fun RMI function to be called
     */
    private void tryRMICall(RunnableRMI fun, String res) {
        new Thread(() -> {
            try {
                fun.run();
                logResponse(res);
            } catch (RemoteException e) {
//                throw new RuntimeException(e);
                System.out.printf("[RMI][%s] Unable to contact the client\n", player.getNickname());
                if(controller!=null) {
                    try {
                        controller.disconnectPlayer(player);
                    } catch (InvalidPlayerException | LobbyException ex) {
                        throw new RuntimeException(ex);
                    } catch (ConnectionException ignore) {
                    }
                }
            }
            //TODO: disconnetti il player se la chiamata non va a buon fine...
        }).start();
    }

    /**
     * Logging function which notifies the response which has been sent on the server console .
     * @param res Response name
     */
    private void logResponse(String res) {
        System.out.printf("[RMI][%s] Sent Response %s\n", player.getNickname(), res);
    }

    /**
     * Updates the client that a player has joined a lobby (waiting for a game)
     * @param player player who joined
     */
    @Override
    public void updatePlayerJoinedRoom(PlayerLobby player) {
        tryRMICall(() -> clientLis.updatePlayerJoinedRoom(player), "joinedRoom");
    }

    /**
     * Updates the client that a player has left a lobby (waiting for a game)
     * @param player player who left
     */
    @Override
    public void updatePlayerLeftRoom(PlayerLobby player) {
        tryRMICall(() -> clientLis.updatePlayerLeftRoom(player),
                "leftRoom");
    }

    /**
     * Updates the client that the game has started: starter cards and initial cards have been given to the players.
     * The client is notified passing the {@link GameModelIF} containing a GameModel with GameStatus set to INIT.
     * @param model The game model containing the game status set to INIT.
     */
    @Override
    public void updateStartGame(GameModelIF model, GameController controller) {
        this.controller = controller;
        tryRMICall(() -> {
            try {
                clientLis.updateStartGame(
                        new GameState(model),
                        new GameControllerRMI(controller, player));
            } catch (InvalidPlayerException e) {
                throw new RuntimeException(e);
                //TODO pensaci meglio: può succedere?
            }
        }, "startedGame");
    }

    /**
     * This method should be called ONLY when a player reconnects to the game.
     * Updates the client of the reconnected player with the updated game model and corresponding player.
     * @param model The updated game model.
     */
    @Override
    public void updateGameModel(GameModelIF model, GameController controller, PlayerLobby player) {
        this.controller = controller;
        tryRMICall(() -> {
//            try {
            try {
                clientLis.updateGameModel(
                        new GameState(model),
                        new GameControllerRMI(controller, player),
                        player);
            } catch (InvalidPlayerException e) {
//                //TODO: pensaci meglio: può succedere?
                throw new RuntimeException(e);
            }
        }, "updatedGameState");
    }

    /**
     * Updates the client that a player has played their starter card.
     * @param player The player that played the starter card.
     * @param cardStarter The starter card played.
     * @param availableCoords The list of coordinates that are available to play a card
     */
    @Override
    public void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter, List<Coordinates> availableCoords) {
        tryRMICall(() -> clientLis.updatePlayedStarter(player, cardStarter, availableCoords),
                "playedStarter");
    }

    /**
     * Updates the client that a player has chosen their personal objective card.
     * @param player The player that chose the personal objective card.
     * @param chosenObj Objective card chosen by the player
     */
    @Override
    public void updateChosenPersonalObjective(PlayerLobby player, CardObjectiveIF chosenObj) {
        tryRMICall(() -> clientLis.updateChosenPersonalObjective(player, chosenObj),
                "chosenObjective");
    }

    /**
     * Updates the client that the turn has passed to another player.
     * @param player The player that is going to play the next turn.
     */
    @Override
    public void updateNextTurn(PlayerLobby player) {
        tryRMICall(() -> clientLis.updateNextTurn(player),
                "nextTurn");
    }

    /**
     * Updates the client when a player plays a card.
     * @param player The player that played the card.
     * @param cardPlayed The card played.
     * @param coord The coordinates where the card has been placed, relative to the player's field.
     * @param points The points given by the card.
     * @param availableCoords The list of coordinates that are available to play a card
     */
    @Override
    public void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayed, Coordinates coord, int points, List<Coordinates> availableCoords) {
        tryRMICall(() -> clientLis.updatePlayedCard(player, cardPlayed, coord, points, availableCoords),
                "playedCard");
    }

    /**
     * Updates the client when a player picks a card from the common visible cards.
     * @param player The player that picked the card.
     * @param updatedVisibleCards The updated list of visible cards in the common field.
     * @param pickedCard The card picked by the player
     */
    @Override
    public void updatePickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards, CardPlayableIF pickedCard) {
        tryRMICall(() -> clientLis.updatePickedCard(player, new ArrayList<>(updatedVisibleCards), pickedCard),
                "pickedCard");
    }

    /**
     * Updates the client when the points given by Objective cards (common and personal) have been calculated.
     * @param pointsMap A map containing the points of each player.
     */
    @Override
    public void updatePoints(Map<PlayerLobby, Integer> pointsMap) {
        tryRMICall(() -> clientLis.updatePoints(pointsMap),
                "updatedPoints");
    }

    /**
     * Updates the client with the winner
     * @param winner The player that has won the game.
     */
    @Override
    public void updateWinner(PlayerLobby winner) {
        tryRMICall(() -> clientLis.updateWinner(winner),
                "winner");
    }

    /**
     * Updates the client about ending of game.
     * After this update, the server should not respond to any other request from the clients
     */
    @Override
    public void updateEndGame() {
        tryRMICall(clientLis::updateEndGame, "endGame");
    }

    /**
     * Updates the client with the player that has disconnected from the game.
     * @param player The player that has disconnected.
     */
    @Override
    public void updatePlayerDisconnected(PlayerLobby player) {
        tryRMICall(() -> clientLis.updatePlayerDisconnected(player),
                "playerDisconnected");
    }

    @Override
    public void updateCloseSocket() {
        //TODO: lato socket lo uso per chiudere il socket del client, qua valuta se serve fare qualcosa.
    }

    /**
     * Updates the client with the player that has reconnected to the game.
     * @param player The player that has reconnected.
     */
    @Override
    public void updatePlayerReconnected(PlayerLobby player) {
        tryRMICall(() -> clientLis.updatePlayerReconnected(player),
                "playerReconnected");
    }

    /**
     * Updates the client that the game is in the final phase.
     */
    @Override
    public void updateFinalPhase() {
        tryRMICall(clientLis::updateFinalPhase, "finalPhase");
    }

    /**
     * Updates the client that the game has begun the turn-based phase.
     */
    @Override
    public void updateInGame() {
        tryRMICall(clientLis::updateInGame, "inGame");
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
        tryRMICall(()-> clientLis.updateChatMessage(sender,receivers,text),"chatMessage");
    }

}
