package it.polimi.ingsw.am13.controller;

import it.polimi.ingsw.am13.model.exceptions.InvalidPlayersNumberException;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller used to manage different games, that is to handle the lobby of players who are playing or are willing to start a game.
 * It stores the game listeners of players who want to start a game. Until game is not actually started, the players can be added and removed.
 * When the game has been started, the associated <code>gameController</code> is created. Subsequently, the players are fixed for that game and they cannot change
 * All the created or yet-to-be-created games are associated to a unique gameId.
 */
public class Lobby {

    /**
     * Unique instance of this class (implementing Singleton pattern)
     */
    private static Lobby instance = null;

    /**
     * Games that are already started (mapped via their gameId)
     */
    private final Map<Integer, GameController> startedGames;

    /**
     * gameId of games yet to be started, associated to a list of can-be players for that game.
     */
    private final Map<Integer, List<GameListener>> creatingGames;

    private Lobby() {
        startedGames = new HashMap<>();
        creatingGames = new HashMap<>();
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

    //TODO: rivedi meglio i synchronized, dovrebbero servire ovunque
    //TODO: i nickname devono essere univoci, anche tra diverse partite??

    /**
     * Checks if given player is playing in a started game or is present in one of the yet-to-be-started games
     * @param playerListener <code>GameListener</code> of the players to check if they are present
     * @return True if player is among players of a started game or among players waiting to start a game. False otherwise
     */
    private boolean isPlayerPresent(GameListener playerListener) {
        PlayerLobby playerLobby = playerListener.getPlayer();
        return creatingGames.values().stream().flatMap(ls -> ls.stream().map(GameListener::getPlayer)).toList().contains(playerListener.getPlayer())
                || startedGames.values().stream().flatMap(c -> c.getPlayers().stream()).toList().contains(playerListener.getPlayer());
    }

    /**
     * Creates a new game with the specified player as a "yet-to-be-started" game.
     * More specifically, it finds the next suitable gameId, and associates that gameId to a list of possible player to start
     * that game, for now only populated by the given player
     * @param player First player who creates the game which will start in the future
     * @return The gameId of the newly created game
     * @throws LobbyException If the players has a nickName already chosen by another player in the lobby
     */
    public synchronized int createGame(GameListener player) throws LobbyException {
        if(isPlayerPresent(player))
            throw new LobbyException("Player " + player.getPlayer() + " is already present in a game");
        int gameId = creatingGames.keySet().stream().mapToInt(Integer::intValue).max().orElse(-1) + 1;
        List<GameListener> players = new ArrayList<>();
        players.add(player);
        creatingGames.put(gameId, players);
        return gameId;
    }

    /**
     * Adds a players to a existing "yet-to-be-started" game, specified by the given gameId
     * @param gameId Id of the game the players wants to be added to
     * @param player Player to add to that game
     * @throws LobbyException If the player has a nickName already chosen by another player in the lobby,
     * or if the game with the given gameId does not exist,
     * or if it exists but it contains already 4 players (the maximum number of players allowed for a game)
     */
    public synchronized void addPlayerToLobby(int gameId, GameListener player) throws LobbyException {
        if(!creatingGames.containsKey(gameId))
            throw new LobbyException("This game (" + gameId + ") does not exist or has already started");
        if(isPlayerPresent(player))
            throw new LobbyException("Player " + player.getPlayer() + " is already present in a game");
        List<GameListener> players = creatingGames.get(gameId);
        if(players.size() == 4)
            throw new LobbyException("This lobby contains already the maximum number of players");
        players.add(player);
    }

    /**
     * Removes a players from a existing "yet-to-be-started" game, specified by the given gameId
     * @param gameId Id of the game the players wants to be added to
     * @param player Player to remove from that game
     * @throws LobbyException If the game with the given gameId does not exist,
     * or if it exists but it does not contain the specified player to be removed
     */
    public synchronized void removePlayerFromLobby(int gameId, GameListener player) throws LobbyException {
        if(!creatingGames.containsKey(gameId))
            throw new LobbyException("This game (" + gameId + ") does not exist or has already started");
        List<GameListener> players = creatingGames.get(gameId);
        if(!players.contains(player))
            throw new LobbyException("The specified game (" + gameId + ") does not contains the specified player to be removed");
        players.remove(player);
    }

    /**
     * Reconnect a disconnected player for the already started game they take part in
     * @param player Listener of the player to reconnect
     * @throws LobbyException If the given player is not among players of any started game
     */
    public synchronized void reconnectPlayer(GameListener player) throws LobbyException {
        List<GameController> games = startedGames.values().stream()
                .filter(c -> c.getPlayers().contains(player.getPlayer())).toList();
        if(games.size() != 1)
            throw new LobbyException("The player " + player.getPlayer() + " is not associated to a started game");
        GameController game = games.getFirst();
        //TODO chiama qualcosa su questo gamecontroller per dirgli di riconnettere il player con quel gamelistenr
    }

    /**
     * Set the specified yet-to-be-started game to started. It creates the associated <code>GameController</code>
     * @param gameId Game to start
     * @return <code>GameController</code> created for that game
     * @throws LobbyException If the specified game is not created (is not among the yet-to-be-started games)
     * @throws InvalidPlayersNumberException If the game contains only 1 player
     */
    public synchronized GameController startGame(int gameId) throws LobbyException, InvalidPlayersNumberException {
        if(!creatingGames.containsKey(gameId))
            throw new LobbyException("This game (" + gameId + ") does not exist or has already started");

        GameController controller = new GameController(gameId, creatingGames.get(gameId));
        startedGames.put(gameId, controller);
        creatingGames.remove(gameId);
        return controller;
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
        //TODO: devo chiamare qualcosa anche in GameController??
    }
}
