package it.polimi.ingsw.am13.controller;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of players who are willing to start playing or are already playing.
 * When the room is created, the number of players to reach in order to start the game is set and cannot be modified.
 * The players listed by this class can be added/removed until the target number is reached. After that moment, the
 * players present in the room are the definitive ones.
 * The gameController can be set only once the target number of players is reached.
 */
public class Room {
    // TODO: perché usi una lista di GameListener e non ListenerHandler ()
    /**
     * GameListeners for the players in the room.
     * The size is >0 and <=nPlayersTarget and cannot change once the maximum size is reached
     */
    private final List<GameListener> listeners;

    /**
     * Target number of players to reach in order to start the game
     */
    private final int nPlayersTarget;

    /**
     * Game controller for the started game. If the game has not started yet, it is null
     */
    private GameController gameController;

    /**
     * Creates a nuw room with only the specified player, and sets the target number of players
     * @param player Player who created the room. It is automatically added to the room
     * @param nPlayersTarget Target number of players to reach in order to start the game for this room
     * @throws LobbyException If the target number of players is <2 or >4
     */
    public Room(GameListener player, int nPlayersTarget) throws LobbyException {
        if(nPlayersTarget<2 || nPlayersTarget>4)
            throw new LobbyException("The target number of players must be between 2 and 4");
        this.listeners = new ArrayList<>();
        this.listeners.add(player);
        this.nPlayersTarget = nPlayersTarget;
        gameController = null;
    }

    /**
     * @return List of players in this room
     */
    public List<PlayerLobby> getPlayers() {
        return listeners.stream().map(GameListener::getPlayer).toList();
    }

    /**
     * @return List of listeners of the players in this room
     */
    public List<GameListener> getListeners() {
        return listeners;
    }

    /**
     * @return Target number of players to reach in order to start the game
     */
    public int getnPlayersTarget() {
        return nPlayersTarget;
    }

    /**
     * @return Game controller for the started game. If the game has not started yet, it is null
     */
    public GameController getGameController() {
        return gameController;
    }

    /**
     * Sets the gameController for the room, if it has not been set yet and if the target number of players is reached.
     * This should be done once the game associated to this room has actually started.
     * @param gameController Game controller for the started game of the room
     * @throws LobbyException If the gameController has been already set or if the target number of players is not reached.
     */
    public void setGameController(GameController gameController) throws LobbyException {
        if(!(listeners.size()==nPlayersTarget && gameController==null))
            throw new LobbyException("Cannot set the gameController, the target number of players is not reached or the controller was already set");
        this.gameController = gameController;
    }

    /**
     * @return True if the room is full (that is if the number of current players equals the target number of players), false otherwise
     */
    public boolean isFull() {
        return nPlayersTarget==listeners.size();
    }

    /**
     * Add the given player to the room. If the room is already full, no other player can join it.
     * @param player Listener of the player to add
     * @throws LobbyException If the room is already full.
     */
    public void joinRoom(GameListener player) throws LobbyException {
        if(listeners.size() == nPlayersTarget)
            throw new LobbyException("This room is already full");
        listeners.add(player);
    }

    /**
     * Removes the given player from the room. If the room is already full, no player in it can leave.
     * @param player Listener of the player to remove
     * @return True if the room gets empty after removing the given player, false otherwise
     * @throws LobbyException If the room is already full, or the given players is not in the room
     */
    public boolean leaveRoom(PlayerLobby player) throws LobbyException {
        if(listeners.size()==nPlayersTarget)
            throw new LobbyException("The target number of players has been reached, no one can leave");
        GameListener toRemove = listeners.stream().filter(l -> l.getPlayer().equals(player))
                .findFirst().orElseThrow(() -> new LobbyException("The room does not contain the specified players"));
        listeners.remove(toRemove);
        return listeners.isEmpty();
    }
}
