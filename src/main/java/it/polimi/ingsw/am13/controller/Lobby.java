package it.polimi.ingsw.am13.controller;

import it.polimi.ingsw.am13.model.exceptions.ConnectionException;
import it.polimi.ingsw.am13.model.exceptions.GameStatusException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayersNumberException;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller used to manage multiple games, that is to handle the lobby of players who are playing or are in a room willing to start a game.<br>
 *
 * It stores the game listeners of players in a room which has not starter the game yet.
 * A player joining the lobby can create a new room, setting the number of player for the future game to start, or can join an existing room.
 * (In the moment they join/create a room, they decide their nickname and their token)
 * Until game is not actually started, the players can be added and removed. If the room remains empty, it is automatically deleted. <br>
 *
 * When the right number of players is reached, the game automatically starts, adn the associated <code>gameController</code> is created.
 * The players who were in the room in that moment are so the definite ones for that game and they cannot change.
 * All the rooms and the already started games are associated to a unique gameId.
 */
public class Lobby {

    /**
     * Unique instance of this class (implementing Singleton pattern)
     */
    private static Lobby instance = null;

    //TODO: rivedi meglio i synchronized, dovrebbero servire ovunque ma bho

    /**
     * Games that are already started (mapped via their gameId)
     */
    private final Map<Integer, Room> startedGames;

    /**
     * Rooms that are not playing yet (mapped via their gameId)
     */
    private final Map<Integer, Room> rooms;

    private Lobby() {
        startedGames = new HashMap<>();
        rooms = new HashMap<>();
    }

    /**
     * Fetches the unique instance existing of this class, or first instantiates it (Singleton Pattern)
     * @return Unique instance of Lobby
     */
    public synchronized static Lobby getInstance() {
        if(instance==null)
            instance = new Lobby();
        return instance;
    }

    /**
     * Checks if specified nickname is valid, that is if it's not already chosen by someone else in the lobby
     * @param nick Nickname to check if it is valid
     * @return True if player is valid (has not been already chosen), false otherwise
     */
    private boolean isNickValid(String nick) {
        return rooms.values().stream().flatMap(r -> r.getPlayers().stream()).map(PlayerLobby::getNickname).toList().contains(nick)
                || startedGames.values().stream().flatMap(c -> c.getPlayers().stream()).map(PlayerLobby::getNickname).toList().contains(nick);
    }

    /**
     * Checks if specified nickname is valid, that is if it's not already chosen by someone else in the lobby
     * @param player Listener of the player whose nickname is to be checked if it is valid
     * @return True if player is valid (has not been already chosen), false otherwise
     */
    private boolean isNickInvalid(GameListener player) {
        return !isNickValid(player.getPlayer().getNickname());
    }

    /**
     * Creates a new Room, for now populated only with the specified player.
     * The gameId is automatically found as the next suitable gameId, hence it is different from the other ones created before by the lobby
     * @param player First player who creates the game which will start in the future
     * @param nPlayers The number of players to start the game, chosen by the player who creates the room
     * @return The gameId of the newly created game
     * @throws LobbyException If the player has a nickName already chosen by another player in the lobby
     */
    public synchronized int createRoom(GameListener player, int nPlayers) throws LobbyException {
        if(isNickInvalid(player))
            throw new LobbyException("Player " + player.getPlayer() + " is already present in a game");
        int gameId = rooms.keySet().stream().mapToInt(Integer::intValue).max().orElse(-1) + 1;
        rooms.put(gameId, new Room(player, nPlayers));
        return gameId;
    }

    /**
     * Adds a players to an existing room, specified by the given gameId.
     * If the room with the newly joined players if full, it makes the game start
     * @param gameId Id of the room the player wants to join
     * @param player Listener of the player to add to that room
     * @return The gameController for the newly started game if the room gets full, null otherwise
     * @throws LobbyException If the player has a nickName already chosen by another player in the lobby,
     * or if the room with the given gameId does not exist,
     * or if it exists but the room is already full
     */
    public synchronized Room joinRoom(int gameId, GameListener player) throws LobbyException {
        if(!rooms.containsKey(gameId))
            throw new LobbyException("This game (" + gameId + ") does not exist or has already started");
        if(isNickInvalid(player))
            throw new LobbyException("Player " + player.getPlayer() + " is already present in a game");
        Room room= rooms.get(gameId);
        room.joinRoom(player);
        GameController gameController = null;
        try {
            if(room.isFull())
                startGame(gameId);
        } catch (InvalidPlayersNumberException e) {
            // Should never happen
            throw new RuntimeException(e);
        }
        return room;
    }

    /**
     * Removes a players from the existing room (specified by the given gameId) they joined.
     * If the room becomes empty, it is automatically removes, as if it has never been created.
     * @param player Player to remove from that room
     * @throws LobbyException If the specified player is not in any existing rooms
     */
    public synchronized void leaveRoom(GameListener player) throws LobbyException {
        int gameId = rooms.entrySet().stream().filter(entry -> entry.getValue().getPlayers().contains(player.getPlayer())).map(Map.Entry::getKey)
                .findFirst().orElseThrow(() -> new LobbyException("The specified player (" + player.getPlayer() + " is not in any existing room"));
        if(rooms.get(gameId).leaveRoom(player.getPlayer()))
            rooms.remove(gameId);
    }

    /**
     * Starts the game for the room specified by gameId.
     * It creates the associated <code>GameController</code>, actually starting that game
     *
     * @param gameId Game to start
     * @throws LobbyException                If the specified game has not been created (is not among the yet-to-be-started games)
     * @throws InvalidPlayersNumberException If the game contains only 1 player
     */
    private synchronized void startGame(int gameId) throws LobbyException, InvalidPlayersNumberException {
        if(!rooms.containsKey(gameId))
            throw new LobbyException("This game (" + gameId + ") does not exist or has already started");

        /* Oss: If a player crashed while being in a room, and the game for that room starts, they will be considered still
            connected, and will be disconnected by ping system of gameController
        */

        Room room = rooms.get(gameId);
        GameController controller = new GameController(gameId, room.getListeners());
        room.setGameController(controller);
        startedGames.put(gameId, room);
        rooms.remove(gameId);
    }

    /**
     * Ends the given started game, by removing it from the stored games
     * @param gameId Id of the started game to end
     * @throws LobbyException If the specified game does not exist
     */
    public synchronized void endGame(int gameId) throws LobbyException {
        if(!startedGames.containsKey(gameId))
            throw new LobbyException("This game (" + gameId + ") is not running.");
        startedGames.remove(gameId);
    }

    /**
     * Reconnects a disconnected player for the already started game they took part in
     * @param player Listener of the player to reconnect
     * @throws LobbyException If the given player is not among players of any started game
     * @throws ConnectionException If the player was already connected
     * @throws GameStatusException if any of the methods called directly or indirectly by this method are called in wrong game phase
     * (generic error, should not happen)
     */
    public synchronized void reconnectPlayer(GameListener player) throws LobbyException, ConnectionException, GameStatusException {
        Room room = startedGames.values().stream().filter(r -> r.getPlayers().contains(player.getPlayer()))
                .findFirst().orElseThrow(() -> new LobbyException("The player " + player.getPlayer() + " is not associated to a started game"));
        try {
            room.getGameController().reconnectPlayer(player);
        } catch (InvalidPlayerException e) {    // shouldn't happen, as I check before this point for the game associated
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the specified room has already started playing, and fetches the room itself in this case.
     * @param gameId GameId of the room/game to fetch
     * @return Room of the game if it has already started, or null if the room has not started plying or the gameId does not exist at all
     */
    public synchronized Room getRoomStartedGame(int gameId) {
        if(rooms.containsKey(gameId))
            return null;
        return startedGames.get(gameId);
    }
}
