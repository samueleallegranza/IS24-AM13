package it.polimi.ingsw.am13.controller;

import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of players who are willing to start playing or are already playing.
 * When the room is created, the number of players to reach in order to start the game is set and cannot be modified.
 * The players listed by this class can be always added/removed, remaining the size between 0 and the target number chosen.
 * The first time that target number is reached, the room is set as "game started"
 * Note that the room starts managing the listeners for the players, notifying them for other player joinig/leaving
 * the room.
 */
public class Room extends ListenerHandler implements RoomIF {

    /**
     * GameId for the room, denoting a game yet not started or already started
     */
    private final int gameId;

    /**
     * Target number of players to reach in order to start the game
     */
    private final int nPlayersTarget;

    /**
     * Flag indicating if the game for this is started or not.
     * It can change only once, when the room gets full for the first time
     */
    private boolean gameStarted;

    /**
     * List of players that are in the game, null if the game has not started yet
     */
    private List<PlayerLobby> playersInGame;

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
        super();
        this.gameId = gameId;
        if(nPlayersTarget<2 || nPlayersTarget>4)
            throw new LobbyException("The target number of players must be between 2 and 4");
        this.nPlayersTarget = nPlayersTarget;
        gameStarted = false;
        playersInGame = null;
        joinRoom(player);
    }

    /**
     * @return List of players in this room
     */
    public List<PlayerLobby> getPlayers() {
        return getListeners().stream().map(GameListener::getPlayer).toList();
    }

    /**
     * @return List of players that are in the game.
     * If the game has not started yet, it corresponds to the players currently in the room
     */
    @Override
    public List<PlayerLobby> getPlayersInGame() {
        return playersInGame==null ? getPlayers() : new ArrayList<>(playersInGame);
    }

    /**
     * @return GameId for the room
     */
    public int getGameId() {
        return gameId;
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
     * If the game has not started yet, it adds the given player to the room and notifies all players currently in the room of this.
     * If, after this, the room gets full, it sets the game for the room as started (Note: it does not notify the players for this)
     * @param player Listener of the player to add
     * @throws LobbyException If the room is already full, or if the game has already started
     */
    public void joinRoom(GameListener player) throws LobbyException {
        if(gameStarted)
            throw new LobbyException("The game has already started");
        if(getListeners().size() == nPlayersTarget)
            throw new LobbyException("This room is already full");
        addListener(player);
        notifyPlayerJoinedRoom(player.getPlayer());
        if(getListeners().size() == nPlayersTarget) {
            this.gameStarted = true;
            this.playersInGame = getPlayers();
        }
    }


    /**
     * If the game has started, it reconnects the given player to the room.
     * If the room is already full, surely no other player can join it.
     * It also notifies all players currently in the room of this.
     * @param player Listener of the player to add
     * @param model Model representing the current situation to give to the reconnected player
     * @throws LobbyException If the room is already full, or if the game has not started yet
     */
    public void reconnectToRoom(GameListener player, GameModelIF model, GameController controller) throws LobbyException {
        if(!gameStarted)
            throw new LobbyException("The game has not started");
        if(getListeners().size() == nPlayersTarget)
            throw new LobbyException("This room is already full");
        addListener(player);
        notifyPlayerReconnected(player.getPlayer(), controller, model);
    }

    /**
     * It removes the given player from the room.
     * It also notifies all players currently in the room of this.
     * @param player Listener of the player to remove
     * @return True if the room gets empty after removing the given player, false otherwise
     * @throws LobbyException If the room is already empty, or the given players is not in the room
     */
    public boolean leaveRoom(PlayerLobby player) throws LobbyException {
        if(getListeners().isEmpty())
            throw new LobbyException("The room is empty");
        GameListener lis;
        try {
            lis = removeListener(player);
        } catch (InvalidPlayerException e) {
            throw new LobbyException("The listener of the player in not in the listener list");
        }

        System.out.println("\n");
        getListeners().forEach(System.out::println);
        System.out.println("\n");


        if(!gameStarted) {
            notifyPlayerLeftRoom(player);
            lis.updatePlayerLeftRoom(player);
            // Also the just removed player must be notified
        }
        else
            notifyPlayerDisconnected(player);
            // In this case (network crash) the removed player must not be notified
        return getListeners().isEmpty();
    }



    // NOTIFIES HANDLED BY ROOM

    /**
     * Notify the view that a Player has entered a Room.
     * This method is to be used only when the game hasn't started yet.
     * @param player The player that entered the room
     */
    private void notifyPlayerJoinedRoom(PlayerLobby player){
        for (GameListener listener : getListeners()) {
            listener.updatePlayerJoinedRoom(player);
        }
    }

    /**
     * Notify the view that a Player has left a Room.
     * This method is to be used only when the game hasn't started yet.
     * @param player The player that left the room
     */
    private void notifyPlayerLeftRoom(PlayerLobby player){
        for (GameListener listener : getListeners()) {
            listener.updatePlayerLeftRoom(player);
        }
    }

    /**
     * Notifies the view that a player has disconnected from the game. <br>
     * @param player The player that has disconnected from the game.
     */
    private void notifyPlayerDisconnected(PlayerLobby player){
        for (GameListener listener : getListeners()){
            if(listener.getPlayer().equals(player))
                listener.updateCloseSocket();
            else
                listener.updatePlayerDisconnected(player);
        }
    }

    /**
     * Notifies the view that a player has reconnected to the game. <br>
     * @param player The player that has reconnected to the game.
     * @param model The {@link GameModelIF} containing the updated version of the game.
     */
    private void notifyPlayerReconnected(PlayerLobby player, GameController controller, GameModelIF model){
        for (GameListener listener : getListeners()){
            if(listener.getPlayer().equals(player))
                listener.updateGameModel(model, controller, player);
            else
                listener.updatePlayerReconnected(player);
        }
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder(String.format("[%s] Room %d: %d / %d players",
                gameStarted ? "started" : "not started",
                gameId,
                getListeners().size(),
                nPlayersTarget));
        for(PlayerLobby p : getPlayers()) {
            text.append("\n\t\t- ").append(p);
        }
        return text.toString();
    }
}
