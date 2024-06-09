package it.polimi.ingsw.am13.controller;
import it.polimi.ingsw.am13.model.GameModel;
import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//OSS: currently, the only classes managing notifications via ListenerHandler are Room for per-GameBegins and
// GameModel for post-GameBegins

/**
 * This class is responsible for handling a List of {@link GameListener} <br>
 * and notifying the view when a change occurs in the {@link GameModel} after a game event happens.
 * Depending on the event, the view is notified passing the appropriate parameters.
 */
public class ListenerHandler implements Serializable {

    //TODO: Non dovrebbe, ma potrebbero servire delle synchronized...

    /**
     * Listeners handled by the class
     */
    private final List<GameListener> listeners;

    public ListenerHandler() {
        listeners = new ArrayList<>();
    }

    /**
     * Adds a {@link GameListener} to the list of GameListener.
     * @param listener The listener to be added.
     */
    protected void addListener(GameListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a {@link GameListener} from the list of GameListener.
     * @param player Player to disconnect
     * @throws InvalidPlayerException if the player was not part of the listeners list.
     * @return Listener removed
     */
    protected GameListener removeListener(PlayerLobby player) throws InvalidPlayerException {
        for(GameListener l : listeners)
            if(l.getPlayer().equals(player)) {
                listeners.remove(l);
                return l;
            }
        throw new InvalidPlayerException("Player " + player + " is not among current listeners of the game");
    }

    /**
     * Returns the list of {@link GameListener} handled by this class.
     * @return the list of GameListener.
     */
    public List<GameListener> getListeners() {
        return new ArrayList<>(listeners);
    }



    // NOTIFIES HANDLED BY GAME MODEL


    /**
     * Notifies the view that the game has started: starter cards and initial cards have been given to the players. <br>
     * The view is notified passing the {@link GameModelIF} containing a GameModel with GameStatus set to INIT.
     * @param model The interface {@link GameModelIF} representing a version of the GameModel.
     */
    public void notifyStartGame(GameModelIF model, GameController controller){
        for (GameListener listener : listeners) {
            listener.updateStartGame(model, controller);
        }
    }

    /**
     * Notifies the view that a starter card has been played by a player. <br>
     * @param player The player that played the starter card.
     * @param cardStarter The starter card played by the player.
     * @param availableCoords The list of coordinates that are available to play a card
     */
    public void notifyPlayedStarter(PlayerLobby player, CardStarterIF cardStarter, List<Coordinates> availableCoords){
        for (GameListener listener : listeners) {
            listener.updatePlayedStarter(player, cardStarter, availableCoords);
        }
    }

    /**
     * Notifies the view that a player has chosen a personal objective card. <br>
     * @param player The player that has chosen a personal objective card.
     * @param chosenObj Objective card chosen by the player
     */
    public void notifyChosenPersonalObjective(PlayerLobby player, CardObjectiveIF chosenObj){
        for (GameListener listener : listeners) {
            listener.updateChosenPersonalObjective(player, chosenObj);
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
     * Also notifies the updated list of coordinates where the player can play a card.
     * @param player The player that played the card.
     * @param cardPlayable The card played by the player.
     * @param coord The coordinates where the card has been placed, relative to the player's field.
     * @param points The points given by the card.
     * @param availableCoords The list of updated coordinates where the player can play a card.
     */
    public void notifyPlayedCard(PlayerLobby player, CardPlayableIF cardPlayable, Coordinates coord,
                                 int points, List<Coordinates> availableCoords){
        for (GameListener listener : listeners) {
            listener.updatePlayedCard(player, cardPlayable, coord, points,availableCoords);
        }
    }

    /**
     * Notifies the view that a card has been picked by a player. <br>
     * @param player The player that picked the card.
     * @param updatedVisibleCards The updated list of visible cards in the common field (size 6).
     * @param pickedCard The card picked by the player
     */
    public void notifyPickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards, CardPlayableIF pickedCard){
        for (GameListener listener : listeners) {
            listener.updatePickedCard(player, updatedVisibleCards, pickedCard);
        }
    }

    /**
     * Notifies the view that the points given by Objective cards (common and personal) have been calculated. <br>
     * @param pointsMap A map containing the updated points of each player.
     */
    public void notifyPointsCalculated(Map<PlayerLobby, Integer> pointsMap){
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
     * Notifies the listeners about ending of game.
     * After this notify, the server should not respond to any other request from the clients
     */
    protected void notifyEndGame() {
        for (GameListener listener : listeners){
            listener.updateEndGame();
        }
    }



    /**
     * Notifies the view that the game is in the playing phase. <br>
     */
    public void notifyInGame(){
        for (GameListener listener : listeners){
            listener.updateInGame();
        }
    }

    /**
     * Notifies the view that the game is in the final phase. <br>
     * @param turnsToEnd Number of turns to reach the end of the turn-based phase
     */
    public void notifyFinalPhase(int turnsToEnd){
        for (GameListener listener : listeners){
            listener.updateFinalPhase(turnsToEnd);
        }
    }

    /**
     * Notifies the view that a chat message was sent.
     *
     * @param sender    of the message
     * @param receivers of the message
     * @param text      content of the message
     */
    public void transmitChatMessage(PlayerLobby sender, List<PlayerLobby> receivers, String text){
        for(GameListener listener : listeners){
            if(receivers.contains(listener.getPlayer()) || sender.equals(listener.getPlayer()))
                listener.updateChatMessage(sender,receivers,text);
        }
    }
}