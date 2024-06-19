package it.polimi.ingsw.am13.client.gamestate;

import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.GameStatus;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;
import java.util.Map;

/**
 * Class handling the representation of the state of the game ({@link GameState}).
 * There is an "update method" for each game event (hence not events like "joinedRoom"). Each of these methods set the
 * right attributes of the game state to change it according to the event who provoked the change.
 * So if there is the need for manipulating the game state (i.e. creating it and changing it to keep representing the current
 * game's state), it should be done by using this class
 */
public class GameStateHandler {

    /**
     * Game state to handle
     */
    private final GameState state;

    /**
     * Builds a new handler for the representation of the game's state, starting from the interface of the game's model
     * @param model Interface of the game's model from which to build the representation of the game's state and the
     *              corresponding handler
     */
    public GameStateHandler(GameModelIF model) {
        state = new GameState(model);
    }

    /**
     * Builds a new handler for the representation of the game's state, starting from an already built representation
     * @param state Already built representation of the game's state, whose handler is to be created
     */
    public GameStateHandler(GameState state) {
        this.state = state;
    }

    
    /**
     * @return Representation of game's state handled
     */
    public GameState getState() {
        return state;
    }


    /**
     * A player has played their starter card.
     * @param player The player that played the starter card.
     * @param cardStarter The starter card played.
     * @param availableCoords The list of coordinates that are available to play a card
     */
    public void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter, List<Coordinates> availableCoords) {
        state.getPlayerState(player).setStarterCard(cardStarter);
        FieldState field = state.getPlayerState(player).getField();
        field.placeCardSideAtCoord(Coordinates.origin(), cardStarter.getPlayedCardSide());
        field.setAvailableCoords(availableCoords);
    }

    /**
     * A player has chosen their personal objective card.
     * @param player The player that chose the personal objective card.
     * @param chosenObj Objective card chosen by the player
     */
    public void updateChosenPersonalObjective(PlayerLobby player, CardObjectiveIF chosenObj) {
        state.getPlayerState(player).setHandObjective(chosenObj);
    }

    /**
     * The next turn has been reached
     * @param player The player that is going to play the next turn.
     */
    public void updateNextTurn(PlayerLobby player) {
        state.setCurrentPlayer(player);
        if(state.getGameStatus() == GameStatus.FINAL_PHASE)
            state.setTurnsToEnd(state.getTurnsToEnd()-1);
    }

    /**
     * A player has played a card.
     * @param player The player that played the card.
     * @param card The card played.
     * @param coords The coordinates where the card has been placed, relative to the player's field.
     * @param points The points given by the card.
     * @param availableCoords The list of coordinates that are available to play a card
     */
    public void updatePlayedCard(PlayerLobby player, CardPlayableIF card, Coordinates coords, int points, List<Coordinates> availableCoords) {
        // If card == null, it implies this is a 'ghost' move for disconnected player, and I don't have to update anything in gamestate
        if(card != null) {
            PlayerState playerState = state.getPlayerState(player);
            FieldState fieldState = playerState.getField();

            playerState.removeCardPlayed(card);
            playerState.setPoints(points);
            fieldState.placeCardSideAtCoord(coords, card.getPlayedCardSide());
            fieldState.setAvailableCoords(availableCoords);
        }
    }

    /**
     * A player has picked a card from the common visible cards.
     * @param player The player that picked the card.
     * @param updatedVisibleCards The updated list of visible cards in the common field.
     * @param pickedCard The card picked by the player
     */
    public void updatePickedCard(PlayerLobby player, List<CardPlayableIF> updatedVisibleCards, CardPlayableIF pickedCard) {
        // If pickedCard == null, it implies this is a 'ghost' move for disconnected player, and I don't have to update anything in gamestate
        if(pickedCard!=null) {
            state.getPlayerState(player).addCardPicked(pickedCard);
            state.setPickables(updatedVisibleCards);
        }
    }

    /**
     * The points given by Objective cards (common and personal) have been calculated.
     * Following the game's rules, the specified points are the final ones
     * @param pointsMap A map containing the points of each player.
     */
    public void updatePoints(Map<PlayerLobby, Integer> pointsMap) {
        state.setGameStatus(GameStatus.CALC_POINTS);
        state.setCurrentPlayer(null);
        for(PlayerLobby p : pointsMap.keySet())
            state.getPlayerState(p).setPoints(pointsMap.get(p));
    }

    /**
     * The winner has been calculated
     *
     * @param winners The player(s) that won the game.
     */
    public void updateWinner(List<PlayerLobby> winners) {
        state.setGameStatus(GameStatus.ENDED);
        state.setWinner(winners);
    }

    /**
     * A player has disconnected from the game.
     * @param player The player that has disconnected.
     */
    public void updatePlayerDisconnected(PlayerLobby player) {
        state.getPlayerState(player).setConnected(false);
    }

    /**
     * A player has reconnected to the game.
     * @param player The player that has reconnected.
     */
    public void updatePlayerReconnected(PlayerLobby player) {
        state.getPlayerState(player).setConnected(true);
    }

    /**
     * The game is in the final phase.
     * @param turnsToEnd Number of turns to reach the end of the turn-based phase
     */
    public void updateFinalPhase(int turnsToEnd) {
        state.setGameStatus(GameStatus.FINAL_PHASE);
        state.setTurnsToEnd(turnsToEnd);
    }

    /**
     * The game has begun the turn-based phase.
     */
    public void updateInGame() {
        state.setGameStatus(GameStatus.IN_GAME);
        state.setCurrentPlayer(state.getFirstPlayer());

    }

}
