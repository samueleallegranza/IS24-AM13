package it.polimi.ingsw.am13.network.rmi;

import it.polimi.ingsw.am13.client.network.rmi.GameListenerClientRMI;
import it.polimi.ingsw.am13.client.network.rmi.GameListenerClientRMIIF;
import it.polimi.ingsw.am13.controller.*;
import it.polimi.ingsw.am13.model.exceptions.ConnectionException;
import it.polimi.ingsw.am13.model.exceptions.GameStatusException;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Version of class representing lobby for the RMI connection. Hence this class is exposed to the network as RMI remote object.
 * The methods who allow players to join a room/game must receive a <code>{@link GameListenerClientRMI}</code> already instantiated,
 * so that the server can use them to give update to the corresponding clients via RMI
 * <br><br>
 *
 * Controller used to manage multiple games, that is to handle the lobby of players who are playing or are in a room willing to start a game.<br>
 *
 * It stores the server game listeners of players in all the rooms.
 * A player joining the lobby can create a new room, setting the number of player for the future game to start, or can join an existing room.
 * (In the moment they join/create a room, they decide their nickname and their token)
 * Until game is not actually started, the players can be added and removed. If the room remains empty, it is automatically deleted. <br>
 *
 * When the right number of players is reached, the game automatically starts, adn the associated <code>{@link GameController}</code> is created.
 * The players in the room are given the remote object <code>{@link GameControllerRMI}</code> via RMI update-
 * The players who were in the room in that moment are so the definite ones for that game and they cannot change.
 * All the rooms and the already started games are associated to a unique gameId.
 */
public class LobbyRMI extends UnicastRemoteObject implements LobbyRMIIF {

    // TODO: non dovrebbe servire transient sugli attributi, ma ragionaci meglio

    //TODO: pensa se implementare i registri RMI qua o su ServerMain

    /**
     * Map between players and their corresponding listeners.
     * The listeners are specific for the RMI connection.
     * Hence note that these are only part of the clients, in particular those who chose to communicate via RMI
     */
    private final Map<PlayerLobby, GameListenerServerRMI> mapLis;

    //TODO: it should not need pattern singleton also for LobbyRMI, it is uniquely exposed to the network via rmi registry binding...

    /**
     * Stored instance of lobby (only for not to keep calling <code>Lobby.getInstance()</code>)
     */
    private final Lobby lobby;

    public LobbyRMI() throws RemoteException {
        super();
        this.mapLis = new HashMap<>();
        lobby = Lobby.getInstance();
    }

    /**
     * @return List of all rooms in the lobby, both the ones with game starter and not already started
     */
    public synchronized List<RoomIF> getRooms() throws RemoteException {
        return lobby.getRooms();
    }

    /**
     * Creates a new Room, for now populated only with the specified player.
     * The gameId is automatically found as the next suitable gameId, hence it is different from the other ones created before by the lobby
     * In case of success, the player who created the room is notified.
     * @param playerListener Client listener of the first player who creates the game which will start in the future
     * @param nPlayers The number of players to start the game, chosen by the player who creates the room
     * @throws LobbyException If the player has a nickName already chosen by another player in the lobby
     */
    public synchronized void createRoom(GameListenerClientRMIIF playerListener, int nPlayers) throws LobbyException, RemoteException {
        GameListenerServerRMI lis = new GameListenerServerRMI(playerListener, playerListener.getPlayer());
        lobby.createRoom(lis, nPlayers);
        mapLis.put(playerListener.getPlayer(), lis);
    }

    /**
     * Adds a players to an existing room, specified by the given gameId.
     * In case of success, the players in that room are notified, and receive an update for the created gameController, too
     * If the room with the newly joined players if full, it makes the game start (and notify the players of this).
     * @param gameId Id of the room the player wants to join
     * @param playerListener Client listener of the player to add to that room
     * @throws LobbyException If the player has a nickName already chosen by another player in the lobby,
     * or if the room with the given gameId does not exist,
     * or if it exists but the room is already full
     */
    public synchronized void joinRoom(int gameId, GameListenerClientRMIIF playerListener) throws LobbyException, RemoteException {
        GameListenerServerRMI lis = new GameListenerServerRMI(playerListener, playerListener.getPlayer());
        lobby.joinRoom(gameId, lis);
        mapLis.put(playerListener.getPlayer(), lis);
    }

    /**
     * Removes a players from the existing room (specified by the given gameId) they joined.
     * In case of success, it notifies the players in the room
     * If the room becomes empty, it is automatically removed, as if it has never been created.
     * @param player Player to remove from that room
     * @throws LobbyException If the specified player is not in any existing rooms
     */
    public synchronized void leaveRoom(PlayerLobby player) throws LobbyException, RemoteException {
        GameListenerServerRMI lis = mapLis.get(player);
        if(lis == null)
            throw new LobbyException("Player doesn't exist in lobby");
        lobby.leaveRoom(lis);
        mapLis.remove(player);
    }

    /**
     * Reconnects a disconnected player for the already started game they took part in.
     * Triggers the notification for other players, and the update for the gameController for the reconnected player.
     * @param playerListener Client listener of the player to reconnect
     * @throws LobbyException If the given player is not among players of any started game
     * @throws ConnectionException If the player was already connected
     * @throws GameStatusException if any of the methods called directly or indirectly by this method are called in wrong game phase
     * (generic error, should not happen)
     */
    public synchronized void reconnectPlayer(GameListenerClientRMIIF playerListener) throws LobbyException, RemoteException, ConnectionException, GameStatusException {
        GameListenerServerRMI lis = new GameListenerServerRMI(playerListener, playerListener.getPlayer());
        lobby.reconnectPlayer(lis);
        mapLis.put(playerListener.getPlayer(), lis);
    }

}
