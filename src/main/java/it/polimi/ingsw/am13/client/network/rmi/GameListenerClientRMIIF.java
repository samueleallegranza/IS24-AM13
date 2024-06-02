package it.polimi.ingsw.am13.client.network.rmi;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.CardStarterIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.network.rmi.GameControllerRMIIF;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Interface exposed remotely by the client representing the client-side listener for RMI connection.
 * It is the actual interface the client should pass to the server when it connects to the room/game as a
 * reference to itself, which the server can then use to send updates to the client.
 * It handles the updates the server sends to the clients, by modifying the view and updating the internal game's state
 */
public interface GameListenerClientRMIIF extends Remote {

    /**
     * @return Player associated to this listener
     */
    PlayerLobby getPlayer() throws RemoteException;

    /**
     * A player has joined the room
     * @param player player who joined
     */
    void updatePlayerJoinedRoom(PlayerLobby player) throws RemoteException;

    /**
     * A player left the room
     * @param player Player who left
     */
    void updatePlayerLeftRoom(PlayerLobby player) throws RemoteException;

    /**
     * The game has started: starter cards and initial cards have been given to the players.
     * The specified model contains the initial set up for the started game.
     * This triggers also the beginning of the thread for sending pings
     * @param state The current game state when the method is called
     */
    void updateStartGame(GameState state, GameControllerRMIIF controller)
            throws RemoteException, InvalidPlayerException;

    /**
     * This method should be called once the player has reconnected an already started game.
     * It updates the entire game's state, i.e. it creates a new game's state starting from the specified model
     * (reconstructing the current situation in game).
     * @param state The current game state when the method is called
     */
    void updateGameModel(GameState state, GameControllerRMIIF controller, PlayerLobby player) throws RemoteException;

    /**
     * A player has played their starter card.
     * @param player The player that played the starter card.
     * @param cardStarter The starter card played.
     * @param availableCoords The list of coordinates that are available to play a card
     */
    void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter, List<Coordinates> availableCoords) throws RemoteException;

    /**
     * A player has chosen their personal objective card.
     * @param player The player that chose the personal objective card.
     * @param chosenObj Objective card chosen by the player
     */
    void updateChosenPersonalObjective(PlayerLobby player, CardObjectiveIF chosenObj) throws RemoteException;

    /**
     * The game has begun the turn-based phase.
     */
    void updateInGame() throws RemoteException;

    /**
     * The turn has passed to another player.
     * @param player The player that is going to play the next turn.
     */
    void updateNextTurn(PlayerLobby player) throws RemoteException;

    /**
     * A player plays a card.
     * @param player The player that played the card.
     * @param cardPlayed The card played.
     * @param coord The coordinates where the card has been placed, relative to the player's field.
     * @param points The points given by the card.
     * @param availableCoords The list of coordinates that are available to play a card
     */
    void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayed, Coordinates coord,
                                 int points, List<Coordinates> availableCoords) throws RemoteException;

    /**
     * A player picks a card from the common visible cards.
     * @param player The player that picked the card.
     * @param updatedVisibleCards The updated list of visible cards in the common field.
     * @param pickedCard The card picked by the player
     */
    void updatePickedCard(PlayerLobby player, List<CardPlayableIF> updatedVisibleCards, CardPlayableIF pickedCard) throws RemoteException;

    /**
     * The game is in the final phase.
     */
    void updateFinalPhase() throws RemoteException;

    /**
     * The points given by Objective cards (common and personal) have been calculated.
     * Following the game's rules, the specified points are the final ones
     * @param pointsMap A map containing the points of each player.
     */
    void updatePoints(Map<PlayerLobby, Integer> pointsMap) throws RemoteException;

    /**
     * The winner has been calculated
     * @param winner The player that has won the game.
     */
    void updateWinner(PlayerLobby winner) throws RemoteException;

    /**
     * The game has ended.
     * After this update, the server should not respond to any other request
     * This triggers also the interruption for the thread sending pings
     */
    void updateEndGame() throws RemoteException;

    /**
     * A player has disconnected from the game.
     * @param player The player that has disconnected.
     */
    void updatePlayerDisconnected(PlayerLobby player) throws RemoteException;

    /**
     * A player has reconnected to the game.
     * @param player The player that has reconnected.
     */
    void updatePlayerReconnected(PlayerLobby player) throws RemoteException;

    /**
     * Updates the client with a chat message
     *
     * @param sender    of the message
     * @param receivers of the message
     * @param text      content of the message
     */
     void updateChatMessage(PlayerLobby sender, List<PlayerLobby> receivers, String text) throws RemoteException;

}
