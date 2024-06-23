package it.polimi.ingsw.am13.controller;

import it.polimi.ingsw.am13.model.exceptions.ConnectionException;
import it.polimi.ingsw.am13.model.exceptions.GameStatusException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayersNumberException;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.model.player.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    //TODO: rivedi meglio i synchronized, dovrebbero servire ovunque ma bho

    /**
     * Unique instance of this class (implementing Singleton pattern)
     */
    private static Lobby instance = null;

    /**
     * Controllers of the games already started (mapped via their gameId)
     */
    private final Map<Integer, GameController> controllers;

    /**
     * All rooms created, with both games started or not (mapped via their gameId)
     */
    private final Map<Integer, Room> rooms;

    private Lobby() {
        rooms = new HashMap<>();
        controllers = new HashMap<>();
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
     * @return List of all rooms in the lobby, both the ones with game starter and not already started
     */
    public synchronized List<RoomIF> getRooms() {
        return new ArrayList<>(rooms.values());
    }

    /**
     * Checks if specified nickname is valid, that is if it's not already chosen by someone else in the lobby
     * @param player Listener of the player whose nickname is to be checked if it is valid
     * @return False if player is valid (has not been already chosen), true otherwise
     */
    private boolean isNickInvalid(GameListener player) {
        String nick = player.getPlayer().getNickname();
        return rooms.values().stream().flatMap(r -> r.getPlayers().stream()).map(PlayerLobby::getNickname).toList().contains(nick);
    }

    /**
     * Checks if the color of spcified player's token is valid, that is if it's not already chosen by someone else in the room
     * @param player Listener of the player whose nickname is to be checked if it is valid
     * @param room Room the player wants to join
     * @return False if player is valid (has not been already chosen), true otherwise
     */
    private boolean isTokenInvalid(GameListener player, Room room) {
        ColorToken color = player.getPlayer().getToken().getColor();
        return room.getPlayers().stream().map(PlayerLobby::getToken).map(Token::getColor).toList().contains(color);
    }

    /**
     * Creates a new Room, for now populated only with the specified player.
     * The gameId is automatically found as the next suitable gameId, hence it is different from the other ones created before by the lobby
     * In case of success, the player who created the room is notified.
     * @param player First player who creates the game which will start in the future
     * @param nPlayers The number of players to start the game, chosen by the player who creates the room
     * @throws LobbyException If the player has a nickName already chosen by another player in the lobby,
     * or if the given target number of players to reach is &lt;2 or &gt;4
     */
    public synchronized void createRoom(GameListener player, int nPlayers) throws LobbyException {
        if(isNickInvalid(player))
            throw new LobbyException("Player " + player.getPlayer() + " is already present in a game");
        int gameId = rooms.keySet().stream().mapToInt(Integer::intValue).max().orElse(-1) + 1;
        Room room = new Room(gameId, player, nPlayers);
        rooms.put(gameId, room);
    }

    /**
     * Adds a players to an existing room, specified by the given gameId.
     * In case of success, the players in that room are notified.
     * If the room with the newly joined players if full, it makes the game start (and notifies the players of this).
     * @param gameId Id of the room the player wants to join
     * @param player Listener of the player to add to that room
     * @throws LobbyException If
     * <ul>
     *     <li>the player has a nickName already chosen by another player in the lobby</li>
     *     <li>or if the color chosen is already taken</li>
     *     <li>or if the room with the given gameId does not exist</li>
     *     <li>or if it exists but the room is already full</li>
     * </ul>
     */
    public synchronized void joinRoom(int gameId, GameListener player) throws LobbyException {
        Room room = rooms.get(gameId);
        if(room == null || room.isGameStarted())
            throw new LobbyException("This game (" + gameId + ") does not exist or has already started");
        if(isNickInvalid(player))
            throw new LobbyException("Player " + player.getPlayer() + " is already present in a game");
        if(isTokenInvalid(player, room))
            throw new LobbyException("Chosen token " + player.getPlayer().getToken() + " is not a valid choice in this room");
        room.joinRoom(player);
        try {
            if(room.isGameStarted())
                startGame(gameId);
        } catch (InvalidPlayersNumberException e) {
            // Should never happen
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes a players from the existing room (specified by the given gameId) they joined.
     * In case of success, it notifies the players in the room
     * If the room becomes empty, it is automatically removed, as if it has never been created. (it does not notify the players of this)
     * @param player Player to remove from that room
     * @throws LobbyException If the specified player is not in any existing room
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
     * @param gameId Game to start
     * @throws LobbyException If the room corresponding to the given gameId does not exist or has not started the game yet
     * @throws InvalidPlayersNumberException If the game contains only 1 player
     */
    private synchronized void startGame(int gameId) throws LobbyException, InvalidPlayersNumberException {
        /* Oss: If a player crashed while being in a room, and the game for that room starts, they will be considered still
            connected, and will be disconnected by ping system of gameController
        */
        Room room = rooms.get(gameId);
        if(room==null || !room.isGameStarted())
            throw new LobbyException("This game (" + gameId + ") does not exist or has already started");
        GameController controller = new GameController(room);
        controllers.put(gameId, controller);
    }

    /**
     * Ends the given started game, by removing it from the stored games
     * @param gameId Id of the started game to end
     * @throws LobbyException If the specified game does not exist
     */
    public synchronized void endGame(int gameId) throws LobbyException {
        Room room = rooms.get(gameId);
        if(room==null || !room.isGameStarted())
            throw new LobbyException("This game (" + gameId + ") does not exist or is not started.");
        room.notifyEndGame();
        rooms.remove(gameId);
        controllers.remove(gameId);
        System.out.println("Game " + gameId + " removed");
    }

    /**
     * Reconnects a disconnected player for the already started game they took part in.
     * Triggers the notification for other players
     * @param player Listener of the player to reconnect
     * @return GameController for the game the player reconnected to
     * @throws LobbyException If the given player (considering nickname and color of token) is not among players of any started game
     * @throws ConnectionException If the player was already connected
     * @throws GameStatusException if any of the methods called directly or indirectly by this method are called in wrong game phase
     * (generic error, should not happen)
     */
    public synchronized GameController reconnectPlayer(GameListener player) throws LobbyException, ConnectionException, GameStatusException {
        GameController controller = controllers.values().stream().filter(c -> c.getPlayers().contains(player.getPlayer()))
                .findFirst().orElseThrow(() -> new LobbyException("The player " + player.getPlayer() + " is not associated to a started game"));
        try {
            controller.reconnectPlayer(player);
        } catch (InvalidPlayerException e) {    // shouldn't happen, as I check before this point for the game associated
            throw new RuntimeException(e);
        }
        return controller;
    }

    //TODO: ripensaci
    public static void resetLobby() {
        instance = new Lobby();
    }
}
