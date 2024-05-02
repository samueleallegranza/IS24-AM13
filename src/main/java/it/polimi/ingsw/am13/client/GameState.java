package it.polimi.ingsw.am13.client;

import it.polimi.ingsw.am13.model.GameStatus;
import it.polimi.ingsw.am13.model.card.CardObjective;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.player.PlayerIF;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {
    /**
     * The list of players in the game
     */
    private List<PlayerIF> players;

    /**
     * The list of cards that are visible to the player (the two tops aof the decks and the four cards that are drawable)
     */
    private List<CardPlayableIF> visibleCards;
    /**
     * The list of objectives that are common to all players
     */
    private List<CardObjective> commonObjectives;
    /**
     * The current game status
     */
    private GameStatus gameStatus;
    /**
     * The current player
     */
    private PlayerIF currentPlayer;





}
