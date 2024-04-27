package it.polimi.ingsw.am13.controller;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;

/**
 * Represents a set of players who are willing to start playing or are already playing.
 * When the room is created, the number of players to reach in order to start the game is set and cannot be modified.
 * The players listed by this class can be added/removed until the target number is reached. After that moment, the
 * players present in the room are the definitive ones.
 * The gameController can be set only once the target number of players is reached.
 * Note that the room starts managing the listeners for the players, notifying them for other player joinig/leaving
 * the room, and for the game start
 */
public class Room {

    /**
     * GameId for the room, denoting a game yet not started or already started
     */
    private final int gameId;

    /**
     * Target number of players to reach in order to start the game
     */
    private final int nPlayersTarget;

    /**
     * Game controller for the started game. If the game has not started yet, it is null
     */
    private GameController gameController;

    /**
     * Handler of the gameListeners for the players in the room.
     * The number of listeners is >=0 and <=nPlayersTarget
     */
    private final ListenerHandler listenerHandler;

    /**
     * Creates a nuw room with only the specified player, and sets the target number of players.
     * The handler of listener is created in this moment.
     * It also notifies the player who created the room that he has jointed successfully.
     * @param gameId GameId for the new room
     * @param player Player who created the room. It is automatically added to the room
     * @param nPlayersTarget Target number of players to reach in order to start the game for this room
     * @throws LobbyException If the target number of players is <2 or >4
     */
    public Room(int gameId, GameListener player, int nPlayersTarget) throws LobbyException {
        this.gameId = gameId;
        if(nPlayersTarget<2 || nPlayersTarget>4)
            throw new LobbyException("The target number of players must be between 2 and 4");
        this.listenerHandler = new ListenerHandler();
        this.nPlayersTarget = nPlayersTarget;
        gameController = null;
        joinRoom(player);
    }

    /**
     * @return List of players in this room
     */
    public List<PlayerLobby> getPlayers() {
        return listenerHandler.getListeners().stream().map(GameListener::getPlayer).toList();
    }

    /**
     * @return GameId for the room
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * @return Handler of the gameListeners for the players in the room.
     * The number of listeners is >=0 and <=nPlayersTarget
     */
    public ListenerHandler getListenerHandler() {
        return listenerHandler;
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
     * @return True if the room is full (that is if the number of current players equals the target number of players), false otherwise
     */
    public boolean isFull() {
        return nPlayersTarget==listenerHandler.getListeners().size();
    }

    /**
     * If the game has not started yet, it adds the given player to the room.
     * If the room is already full, no other player can join it.
     * It also notifies all players currently in the room.
     * @param player Listener of the player to add
     * @throws LobbyException If the room is already full, or the game has already started
     */
    public void joinRoom(GameListener player) throws LobbyException {
        if(gameController != null)
            throw new LobbyException("The game has already started");
        if(listenerHandler.getListeners().size() == nPlayersTarget)
            throw new LobbyException("This room is already full");
        listenerHandler.addListener(player);
        listenerHandler.notifyPlayerJoinedRoom(player.getPlayer());
    }

    /**
     * If the game has not started yet, it temoves the given player from the room.
     * If the room is already full, no player in it can leave.
     * It also notifies all players currently in the room.
     * @param player Listener of the player to remove
     * @return True if the room gets empty after removing the given player, false otherwise
     * @throws LobbyException If the room is already full, or the game has already started,
     * or the given players is not in the room
     */
    public boolean leaveRoom(GameListener player) throws LobbyException {
        if(gameController != null)
            throw new LobbyException("The game has already started");
        if(listenerHandler.getListeners().size()==nPlayersTarget)
            throw new LobbyException("The target number of players has been reached, no one can leave");
        listenerHandler.removeListener(player);
        listenerHandler.notifyPlayerLeftRoom(player.getPlayer());
        return listenerHandler.getListeners().isEmpty();
    }

    /**
     * Starts the game for this room. This implies setting the gameController for the started game,
     * and it can be done only if it has not been set yet and if the target number of players is reached.
     * @param gameController Game controller for the started game of the room
     * @throws LobbyException If the gameController has been already set or if the target number of players is not reached.
     */
    public void startGameForRoom(GameController gameController) throws LobbyException {
        if(!(listenerHandler.getListeners().size()==nPlayersTarget && gameController==null))
            throw new LobbyException("Cannot set the gameController, the target number of players is not reached or the controller was already set");
        this.gameController = gameController;
        listenerHandler.notifyGameBegins();
    }
}
