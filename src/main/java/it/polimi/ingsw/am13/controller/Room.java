package it.polimi.ingsw.am13.controller;

import it.polimi.ingsw.am13.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;

/**
 * Represents a set of players who are willing to start playing or are already playing.
 * When the room is created, the number of players to reach in order to start the game is set and cannot be modified.
 * The players listed by this class can be added/removed until the target number is reached. After that moment, the
 * players present in the room are the definitive ones.
 * Note that the room starts managing the listeners for the players, notifying them for other player joinig/leaving
 * the room, and for the game to start.
 */
public class Room implements RoomIF {

    /**
     * GameId for the room, denoting a game yet not started or already started
     */
    private final int gameId;

    /**
     * Target number of players to reach in order to start the game
     */
    private final int nPlayersTarget;

    /**
     * Handler of the gameListeners for the players in the room.
     * The number of listeners is >=0 and <=nPlayersTarget
     */
    private transient final ListenerHandler listenerHandler;
    //Transient as I don't want it to go to the network

    //TODO: Così dovrei evitare di inviare sulla rete ListenerHandler, ma controlla questo aspetto

    /**
     * Flag indicating if the game for this is started or not.
     * It can change only once, when the room gets full for the first time
     */
    private boolean gameStarted;

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
        gameStarted = false;
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
     * @return Flag indicating if the game for this is started or not.
     * It can change only once, when the room gets full for the first time
     */
    public boolean isGameStarted() {
        return gameStarted;
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
        if(gameStarted)
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
        if(gameStarted)
            throw new LobbyException("The game has already started");
        if(listenerHandler.getListeners().size()==nPlayersTarget)
            throw new LobbyException("The target number of players has been reached, no one can leave");
        try {
            listenerHandler.removeListener(player.getPlayer());
        } catch (InvalidPlayerException e) {
            throw new LobbyException("The listener of the player in not in the listener list");
        }
        listenerHandler.notifyPlayerLeftRoom(player.getPlayer());
        return listenerHandler.getListeners().isEmpty();
    }

    /**
     * Starts the game for this room. This can be done only if it has not been set yet and if the target number of players is reached.
     * @throws LobbyException If the game has already started or if the target number of players is not reached.
     */
    public void startGameForRoom(GameController controller) throws LobbyException {
        if(listenerHandler.getListeners().size()!=nPlayersTarget || gameStarted)
            throw new LobbyException("Cannot start the game, the target number of players is not reached or the game has already started");
        gameStarted = true;
        listenerHandler.notifyGameBegins(controller);
    }

    /**
     * Ends the game for this room. The room should not be used anymore after this method
     */
    public void endGameForRoom() {
        listenerHandler.notifyEndGame();
    }
}
