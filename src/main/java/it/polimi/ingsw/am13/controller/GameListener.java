package it.polimi.ingsw.am13.controller;

import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.CardStarterIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;
import java.util.Map;

/**
 * Interface to be implemented by the classes that handle the communication between the server and the clients.
 * It contains methods to update the view of the clients with the game status.
 */
public interface GameListener {

    /**
     * @return Player corresponding to this listener
     */
    PlayerLobby getPlayer();

    /**
     * @return Last stored ping
     */
    Long getPing();


    // METHODS TO BE CALLED BY ListenerHandler in Lobby TO MANAGE ROOMS


    /**
     * Updates the view that a player has joined a lobby (waiting for a game)
     * @param player player who joined
     */
    void updatePlayerJoinedRoom(PlayerLobby player);

    /**
     * Updates the view that a player has left a lobby (waiting for a game)
     * @param player player who left
     */
    void updatePlayerLeftRoom(PlayerLobby player);

    /**
     * Updates the view that the game has begun (from Lobby to actual game)
     * @param controller {@link GameController} created for the started game
     */
    void updateGameBegins(GameController controller);


    // METHODS CALLED BY ListenerHandler in GameModel TO MANAGE THE GAME FLOW


    /**
     * Updates the view that the game has started: starter cards and initial cards have been given to the players.
     * The view is notified passing the {@link GameModelIF} containing a GameModel with GameStatus set to INIT.
     * @param model The game model containing the game status set to INIT.
     */
    void updateStartGame(GameModelIF model);

    /**
     * Updates the view that a player has played their starter card.
     * @param player The player that played the starter card.
     * @param cardStarter The starter card played.
     */
    void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter);

    /**
     * Updates the view that a player has chosen their personal objective card.
     * @param player The player that chose the personal objective card.
     */
    void updateChosenPersonalObjective(PlayerLobby player);

    /**
     * Updates the view that the turn has passed to another player.
     * @param player The player that is going to play the next turn.
     */
    void updateNextTurn(PlayerLobby player);

    /**
     * Updates the view when a player plays a card.
     * @param player The player that played the card.
     * @param cardPlayed The card played.
     * @param side The side of the card that has been placed.
     * @param coord The coordinates where the card has been placed, relative to the player's field.
     * @param points The points given by the card.
     * @param availableCoords The list of coordinates that are available to play a card.
     */
    void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayed, Side side, Coordinates coord, int points, List<Coordinates> availableCoords);

    /**
     * Updates the view when a player picks a card from the common visible cards.
     * @param player The player that picked the card.
     * @param updatedVisibleCards The updated list of visible cards in the common field.
     */
    void updatePickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards);

    /**
     * Updates the view when the points given by Objective cards (common and personal) have been calculated.
     * @param pointsMap A map containing the points of each player.
     */
    void updatePoints(Map<PlayerLobby, Integer> pointsMap);

    /**
     * Updates the view with the winner
     * @param winner The player that has won the game.
     */
    void updateWinner(PlayerLobby winner);

    /**
     * Updates the view about ending of game.
     * After this update, the server should not respond to any other request from the clients
     */
    void updateEndGame();

    /**
     * Updates the view with the player that has disconnected from the game.
     * @param player The player that has disconnected.
     */
    void updatePlayerDisconnected(PlayerLobby player);

    /**
     * Updates the view with the player that has reconnected to the game.
     * @param player The player that has reconnected.
     */
    void updatePlayerReconnected(PlayerLobby player);

    /**
     * Updates the view that the game is in the final phase.
     */
    void updateFinalPhase();

    /**
     * Updates the view that the game has begun its playing phase.
     */
    void updateInGame();

    /**
     * This method should be called ONLY when a player reconnects to the game.
     * Updates the view of the reconnected player with the updated game model.
     * @param model The updated game model.
     * @param controller Controller of the game
     */
    void updateGameModel(GameModelIF model, GameController controller);

    /**
     * Updates last ping received
     */
    void updatePing();
}
