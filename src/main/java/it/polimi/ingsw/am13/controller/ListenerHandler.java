package it.polimi.ingsw.am13.controller;
import it.polimi.ingsw.am13.model.GameModel;
import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.CardStarterIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

/**
 * This class is responsible for handling a List of {@link GameListener} <br>
 * and notifying the view when a change occurs in the {@link GameModel} after a game event happens.
 * Depending on the event, the view is notified passing the appropriate parameters.
 */
public class ListenerHandler {
    private final List<GameListener> listeners;

    // TODO: da capire dove mettere questa mappa
    // private Map<GameListener, PlayerLobby> clients;

    public ListenerHandler() {
        listeners = new ArrayList<>();
    }

    /**
     * Adds a {@link GameListener} to the list of GameListener.
     * @param listener The listener to be added.
     */
    public synchronized void addListener(GameListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a {@link GameListener} from the list of GameListener.
     * @param listener The listener to be removed.
     */
    public synchronized void removeListener(GameListener listener) {
        listeners.remove(listener);
    }

    /**
     * Returns the list of {@link GameListener} handled by this class.
     * @return the list of GameListener.
     */
    public synchronized List<GameListener> getListeners() {
        return listeners;
    }

    /**
     * Notifies the view that the game has started: starter cards and initial cards have been given to the players. <br>
     * It also notifies to the view that the possible personal objectives have been set and
     * are retrievable using {@link it.polimi.ingsw.am13.model.GameModelIF#fetchPersonalObjectives(PlayerLobby)}.
     * The view is notified passing the {@link GameModelIF} containing a GameModel with GameStatus set to INIT.
     * @param model The {@link GameModelIF} containing the immutable version of a GameModel.
     */
    public synchronized void notifyStartGame(GameModelIF model){
        for (GameListener listener : listeners) {
            listener.updateStartGame(model);
        }
    }

    /**
     * Notifies the view that a starter card has been played by a player. <br>
     * @param player The player that played the starter card.
     * @param cardStarter The starter card played by the player.
     */
    public synchronized void notifyPlayedStarter(PlayerLobby player, CardStarterIF cardStarter){
        for (GameListener listener : listeners) {
            listener.updatePlayedStarter(player, cardStarter);
        }
    }

    /**
     * Notifies the view that a player has chosen a personal objective card. <br>
     * @param player The player that has chosen a personal objective card.
     */
    public synchronized void notifyChosenPersonalObjective(PlayerLobby player){
        for (GameListener listener : listeners) {
            listener.updateChosenPersonalObjective(player);
        }
    }

    /**
     * Notifies the view that the turn is passed to another player. <br>
     * @param player The player which is going to play the next turn.
     */
    public void notifyNextTurn(PlayerLobby player) {
        for (GameListener listener : listeners) {
            listener.updateNextTurn(player);
        }
    }

    /**
     * Notifies the view that a card has been played by a player. <br>
     * @param player The player that played the card.
     * @param cardPlayable The card played by the player.
     * @param coord The coordinates where the card has been placed, relative to the player's field.
     */
    public void notifyPlayedCard(PlayerLobby player, CardPlayableIF cardPlayable, Coordinates coord) {
        for (GameListener listener : listeners) {
            listener.updatePlayedCard(player, cardPlayable, coord);
        }
    }

    /**
     * Notifies the view that a card has been picked by a player. <br>
     * @param player The player that picked the card.
     * @param updatedVisibleCards The updated list of visible cards in the common field.
     */
    public void notifyPickedCard(PlayerLobby player, List<CardPlayableIF> updatedVisibleCards){
        for (GameListener listener : listeners) {
            listener.updatePickedCard(player, updatedVisibleCards);
        }
    }

    /**
     * Notifies the view that the points have been calculated. <br>
     * @param pointsMap A map containing the points of each player.
     */
    public void notifyPointsCalculated(HashMap<PlayerLobby, Integer> pointsMap){
        for (GameListener listener : listeners){
            listener.updatePoints(pointsMap);
        }
    }

    /**
     * Notifies the view that a player has won the game. <br>
     * @param player The player that has won the game.
     */
    public void notifyWinner(PlayerLobby player){
        for (GameListener listener : listeners){
            listener.updateWinner(player);
        }
    }

    /**
     * Notifies the view that a player has disconnected from the game. <br>
     * @param player The player that has disconnected from the game.
     */
    public void notifyPlayerDisconnected(PlayerLobby player){
        for (GameListener listener : listeners){
            listener.updatePlayerDisconnected(player);
        }
    }

    /**
     * Notifies the view that a player has reconnected to the game. <br>
     * @param player The player that has reconnected to the game.
     */
    public void notifyPlayerReconnected(PlayerLobby player){
        for (GameListener listener : listeners){
            listener.updatePlayerReconnected(player);
        }
    }
}