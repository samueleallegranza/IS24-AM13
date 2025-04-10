package it.polimi.ingsw.am13.controller;

import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Interface to be implemented by the classes that handle the communication between the server and the clients.
 * It contains methods to update the game state of the clients.
 */
public interface GameListener extends Serializable {

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
     * Updates the client that a player has joined a lobby (waiting for a game)
     * @param player player who joined
     */
    void updatePlayerJoinedRoom(PlayerLobby player);

    /**
     * Updates the client that a player has left a lobby (waiting for a game)
     * @param player player who left
     */
    void updatePlayerLeftRoom(PlayerLobby player);


    // METHODS CALLED BY ListenerHandler in GameModel TO MANAGE THE GAME FLOW


    /**
     * Updates the client that the game has started: starter cards and initial cards have been given to the players.
     * The client is notified passing the {@link GameModelIF} containing a GameModel with GameStatus set to INIT.
     * @param model The game model containing current game's state for the newly started game
     * @param controller The controller of the newly started game
     */
    void updateStartGame(GameModelIF model, GameController controller);

    /**
     * This method should be called ONLY when a player reconnects to the game.
     * Updates the client of the reconnected player with the updated game model and corresponding player.
     * @param model The updated game model
     * @param controller The controller of the game
     * @param player The updated player who is reconnecting.
     */
    void updateGameModel(GameModelIF model, GameController controller, PlayerLobby player);

    /**
     * Updates the client that a player has played their starter card.
     * @param player The player that played the starter card.
     * @param cardStarter The starter card played.
     * @param availableCoords The list of coordinates that are available to play a card
     */
    void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter, List<Coordinates> availableCoords);

    /**
     * Updates the client that a player has chosen their personal objective card.
     * @param player The player that chose the personal objective card.
     * @param chosenObj Objective card chosen by the player
     */
    void updateChosenPersonalObjective(PlayerLobby player, CardObjectiveIF chosenObj);

    /**
     * Updates the client that the turn has passed to another player.
     * @param player The player that is going to play the next turn.
     */
    void updateNextTurn(PlayerLobby player);

    /**
     * Updates the client when a player plays a card.
     * @param player The player that played the card.
     * @param cardPlayed The card played.
     * @param coord The coordinates where the card has been placed, relative to the player's field.
     * @param points The points given by the card.
     * @param availableCoords The list of coordinates that are available to play a card
     */
    void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayed, Coordinates coord, int points, List<Coordinates> availableCoords);

    /**
     * Updates the client when a player picks a card from the common visible cards.
     * @param player The player that picked the card.
     * @param updatedVisibleCards The updated list of visible cards in the common field.
     * @param pickedCard The card picked by the player
     */
    void updatePickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards, CardPlayableIF pickedCard);

    /**
     * Updates the client when the points given by Objective cards (common and personal) have been calculated.
     * @param pointsMap A map containing the points of each player.
     */
    void updatePoints(Map<PlayerLobby, Integer> pointsMap);

    /**
     * Updates the client with the winner
     * @param winners The player(s) that has won the game.
     */
    void updateWinner(List<PlayerLobby> winners);

    /**
     * Updates the client about ending of game.
     * After this update, the server should not respond to any other request from the clients
     */
    void updateEndGame();

    /**
     * Updates the client with the player that has disconnected from the game.
     * @param player The player that has disconnected.
     */
    void updatePlayerDisconnected(PlayerLobby player);

    /**
     * Closes the connection for the player associated to this listener
     */
    void closeConnection();

    /**
     * Updates the client with the player that has reconnected to the game.
     * @param player The player that has reconnected.
     */
    void updatePlayerReconnected(PlayerLobby player);

    /**
     * Updates the client that the game is in the final phase.
     * @param turnsToEnd Number of turns to reach the end of the turn-based phase
     */
    void updateFinalPhase(int turnsToEnd);

    /**
     * Updates the client that the game has begun the turn-based phase.
     */
    void updateInGame();

    /**
     * Updates last ping received, and sends the update to the client
     */
    void updatePing();

    /**
     * Updates the client with a chat message
     * @param sender of the message
     * @param receivers of the message
     * @param text content of the message
     */
    void updateChatMessage(PlayerLobby sender, List<PlayerLobby> receivers, String text);
}
