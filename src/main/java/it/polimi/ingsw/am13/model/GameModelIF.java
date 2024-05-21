package it.polimi.ingsw.am13.model;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.GameStatusException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.am13.model.player.FieldIF;
import it.polimi.ingsw.am13.model.player.PlayerIF;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;
import java.util.Map;

/**
 * Immutable version of {@link GameModel} class. <br>
 * It is used to provide a read-only version of the game status to the view,
 * by providing only the necessary methods to fetch the data.
 */
public interface GameModelIF {

    /**
     * @return Unique number indicating the actual game model (hence the game/match to its all entirety)
     */
    int getGameId();

    /**
     * @return List of players, decontextualized from game (their nicknames/tokens, nothing else)
     */
    List<PlayerLobby> fetchPlayersLobby();

    /**
     * @return List of players
     */
    List<PlayerIF> fetchPlayers();

    /**
     * The first player of the match is the one that will do the first play of the card in phase IN_GAME
     * @return The first player of the match
     */
    PlayerLobby fetchFirstPlayer();

    /**
     * @return the player who is playing in the current turn.
     * Null if the game phase is different from IN_GAME or FINAL_PHASE (the 2 phases divided in turns)
     */
    PlayerLobby fetchCurrentPlayer();

    /**
     * @return the status of the game. See class <code>{@link GameStatus}</code> for more details
     */
    GameStatus fetchGameStatus();

    /**
     * @return A map of points accumalated till this point for each player.
     * If the turn-bases phases haven't started yet, the points will be 0
     */
    Map<PlayerLobby, Integer> fetchPoints();

    /**
     * List of all visible cards (that are pickable during turn phases).
     * The list is of size 6, with order: top of deck (with <code>getVisibleSide()==Side.SIDEBACK</code>),
     * and 2 visible cards (with <code>getVisibleSide()==Side.SIDEFRONT</code>), and repetion of this.
     * Elements can be null. If a deck is empty but both its cards are present, only the first element of the set of 3 will be null.
     * Besides this first element of the set, also one or both of the other ones can be null (if it remains only one or no cards
     * of this type to be picked)
     * @return a new list containing the 6 cards on the table that can be picked by players
     * The order: top of resource deck, 2 visible resource cards, top of gold deck, 2 visible gold cards
     */
    List<CardPlayableIF> fetchPickables();

    /**
     * Returns the visible cards for the common objectives.
     * Note that the deck of objective cards is always visible, with top card visible from its back side.
     * Hence, this information is not included in the list
     * @return the list of the 2 common objectives.
     */
    List<CardObjectiveIF> fetchCommonObjectives();

    /**
     * Method callable only during INIT phase. Returns the 2 possible objective cards the player can choose
     * @param player who should be contained in players
     * @return a list containing the two objective cards the player can choose from
     * @throws GameStatusException if this method isn't called in the INIT phase
     * @throws InvalidPlayerException if the player is not one of the players of this match
     */
    List<CardObjectiveIF> fetchPersonalObjectives(PlayerLobby player) throws InvalidPlayerException, GameStatusException;

    /**
     * @param player One of the players of the match
     * @return The starter card assigned to player. Null if player has not been assigned a starter card yet
     * @throws InvalidPlayerException If the player is not among the playing players in the match
     */
    CardStarterIF fetchStarter(PlayerLobby player) throws InvalidPlayerException;

    /**
     * @param player one of the players of the match
     * @return the cards in the hand of the player. The list if empty if player has no cards yet
     * @throws InvalidPlayerException if the player is not one of the players of the match
     */
    List<CardPlayableIF> fetchHandPlayable(PlayerLobby player) throws InvalidPlayerException;

    /**
     * @param player one of the players of the match
     * @return the personal objective of the player. Null if it hasn't been initialized yet
     * @throws InvalidPlayerException if the passed player is not one of the players of the match
     */
    CardObjectiveIF fetchHandObjective(PlayerLobby player) throws InvalidPlayerException;

    /**
     * @return the List of coordinates in which new cards can be played
     * If game has not started yet, the list is empty
     * @throws InvalidPlayerException If player is not among this match's players
     */
    List<Coordinates> fetchAvailableCoord(PlayerLobby player) throws InvalidPlayerException;

    /**
     * @param player Player whose field is returned
     * @return Field (interface) of the given player
     * @throws InvalidPlayerException If the player is not among the playing players in the match
     */
    FieldIF fetchPlayerField(PlayerLobby player) throws InvalidPlayerException;

}
