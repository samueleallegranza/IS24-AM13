package it.polimi.ingsw.am13.client.gamestate;

import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.GameStatus;
import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GameState implements Serializable {


    //TODO MESSAGGIO PER GLI ALTRI: ho creato la classe GameStateHandler per fase gli update, lasciando
    // i metodi di GameState più puliti solo con getter e setter puri


    /**
     * The players in the game, mapped via their player lobby
     */
    private final Map<PlayerLobby, PlayerClient> players;

    /**
     * The list of cards that are visible to the player (the two tops aof the decks and the four cards that are drawable)
     */
    private List<CardPlayableIF> pickables;

    /**
     * The list of objectives that are common to all players
     */
    private final List<CardObjectiveIF> commonObjectives;

    /**
     * The current game status
     */
    private GameStatus gameStatus;

    /**
     * The current player
     */
    private PlayerLobby currentPlayer;

    /**
     * The first player (from which the rounds start)
     */
    private final PlayerLobby firstPlayer;


    //TODO implementalo veramente
    public GameState(GameModelIF model) {
        firstPlayer = null;
        commonObjectives = null;
        players = null;
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
