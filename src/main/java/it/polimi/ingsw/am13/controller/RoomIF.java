package it.polimi.ingsw.am13.controller;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.List;

/**
 * Interface representing a game room.
 * It is possible to check the players currently in the room, whether the game has started and whether the room is currently room.
 * Moreover, it can be retrieved the immutable parameters gameId and nPlayersTarget
 */
public interface RoomIF extends Serializable {

    /**
     * @return List of players currently in this room
     */
    List<PlayerLobby> getPlayers();

    /**
     * @return List of players that are in the game.
     * If the game has not started yet, it corresponds to the players currently in the room
     */
    List<PlayerLobby> getPlayersInGame();

    /**
     * @return GameId for the room
     */
    int getGameId();

    /**
     * @return Target number of players to reach in order to start the game
     */
    int getnPlayersTarget();

    /**
     * @return Flag indicating if the game for this is started or not.
     * It can change only once, when the room gets full for the first time
     */
    boolean isGameStarted();


}
