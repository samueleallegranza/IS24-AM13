package it.polimi.ingsw.am13.client.gamestate;

import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.GameStatus;
import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.player.PlayerIF;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representation of the state of the game in a certain instant.
 * The representation is not player-specific: it stores information for all the players in the game.
 * It is valid only for the game, hence the listed players cannot change (can only be set to disconnected). This implies
 * that the pre-game phase, where players join the room before it gets full, is not represented.
 */
public class GameState implements Serializable {

    /**
     * The players in the game, mapped via their player lobby
     */
    private final Map<PlayerLobby, PlayerClient> players;

    /**
     * The list of cards that are visible to the player (the two tops aof the decks and the four cards that are drawable).
     * Following game's rules, this should be always of size 6
     */
    private List<CardPlayableIF> pickables;

    /**
     * The list of objectives that are common to all players
     * Following game's rules, this should be always of size 2
     */
    private final List<CardObjectiveIF> commonObjectives;

    /**
     * The current game status
     */
    private GameStatus gameStatus;

    /**
     * The current player in a turn-based phase (the one who must play or pick a card)
     */
    private PlayerLobby currentPlayer;

    /**
     * The first player (from which the rounds start)
     */
    private final PlayerLobby firstPlayer;

    /**
     * Creates a new representation of the game's state starting from the given interface of model
     * @param model Game model from which to retrieve the game's state
     */
    public GameState(GameModelIF model) {
        this.players = new HashMap<>();
        for(PlayerIF p : model.fetchPlayers())
            this.players.put(p.getPlayerLobby(), new PlayerClient(p));
        this.pickables = model.fetchPickables();
        this.commonObjectives = model.fetchCommonObjectives();
        this.gameStatus = model.fetchGameStatus();
        this.currentPlayer = model.fetchCurrentPlayer();
        this.firstPlayer = model.fetchFirstPlayer();
    }

    public PlayerClient getPlayerClient(PlayerLobby playerLobby) {
        return players.get(playerLobby);
    }

    public List<CardPlayableIF> getPickables() {
        return pickables;
    }

    public void setPickables(List<CardPlayableIF> pickables) {
        this.pickables = pickables;
    }

    public List<CardObjectiveIF> getCommonObjectives() {
        return commonObjectives;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public PlayerLobby getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PlayerLobby currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public PlayerLobby getFirstPlayer() {
        return firstPlayer;
    }
}
