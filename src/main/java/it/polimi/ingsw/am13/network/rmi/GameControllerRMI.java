package it.polimi.ingsw.am13.network.rmi;

import it.polimi.ingsw.am13.controller.GameController;
import it.polimi.ingsw.am13.controller.LobbyException;
import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Version of class representing gameController for the RMI connection. Hence this class is exposed to the network as RMI remote object.
 * <br>
 * This is a controller for a single game/match
 */
public class GameControllerRMI extends UnicastRemoteObject implements Remote {

    // TODO: non dovrebbe servire transient sugli attributi, ma ragionaci meglio

    /**
     * Wrapped gameController
     */
    private final GameController gameController;

    /**
     * Map associating players to their listeners. The map can change if players disconnect/reconnect to started game
     */
    private final Map<PlayerLobby, GameListenerServerRMI> mapLis;

    /**
     * Wraps the given gameController to be exposed to the network
     * @param gameController gameController to wrap in order to be exposed to the network
     */
    GameControllerRMI(GameController gameController, Collection<GameListenerServerRMI> mapLis) throws RemoteException {
        super();
        this.gameController = gameController;
        this.mapLis = new HashMap<>();
        for(GameListenerServerRMI lis : mapLis)
            this.mapLis.put(lis.getPlayer(), lis);
    }

    /**
     * Reconnects the player corresponding to the game listener and triggers the notification of this.
     * If more than one player is connected, it stops the reconnection timer
     * If two players are connected, it advances to the next turn
     * @param gameListener one of the listeners of ListenerHandler
     * @throws InvalidPlayerException if the player corresponding to gameListener is not one of the players of the match
     * @throws ConnectionException if the player was already connected when this method was called
     * @throws GameStatusException if any of the methods called directly or indirectly by this method are called in wrong game phase
     */
    void reconnectPlayer(GameListenerServerRMI gameListener) throws RemoteException, InvalidPlayerException,
            ConnectionException, GameStatusException {
        gameController.reconnectPlayer(gameListener);
    }

    /**
     * Disconnects the player corresponding to the game listener and starts the reconnection timer if there is only one player left
     * @param player Player to disconnect from the game
     * @throws InvalidPlayerException if the player corresponding to gameListener is not one of the players of the match
     * @throws ConnectionException if the player had already been disconnected
     */
    public void disconnectPlayer(PlayerLobby player) throws RemoteException, InvalidPlayerException,
            ConnectionException, LobbyException {
        GameListenerServerRMI lis = mapLis.get(player);
        if(lis == null)
            throw new LobbyException("Given player (" + player + ") is not in the lobby");
        gameController.disconnectPlayer(lis);
        mapLis.remove(player);
    }

    /**
     * Updates the ping of the given game listener by setting it to the current time
     * @param playerLobby one of players of the match
     */
    public synchronized void updatePing(PlayerLobby playerLobby) throws RemoteException {
        gameController.updatePing(playerLobby);
    }

    /**
     * Method callable only once per player during INIT phase.
     * It plays the starter card of the given player on the passed side
     * @param player Player who has chosen which side of the starting card he wants to play
     * @param side The side of the starting card
     * @throws InvalidPlayerException If the player is not among the playing players in the match
     * @throws GameStatusException If game phase is not INIT
     * @throws InvalidPlayCardException Positioning error of the card at coordinates (0,0).
     */
    public synchronized void playStarter(PlayerLobby player, Side side) throws RemoteException, InvalidPlayerException,
            InvalidPlayCardException, GameStatusException {
        gameController.playStarter(player, side);
    }

    /**
     * Method callable only during INIT phase. Sets the personal objective of the player according to his choice.
     * The objective card must be between the 2 possible cards given to the player. They can be retrieved with
     * <code>fetchPersonalObjectives(player)</code>
     * @param player one of the players of the match
     * @param cardObj the objective chosen by the player
     * @throws GameStatusException if this method isn't called in the INIT phase
     * @throws InvalidPlayerException if the player is not one of the players of this match
     * @throws InvalidChoiceException if the objective card does not belong to the list of the possible objective cards for the player
     * @throws VariableAlreadySetException if this method has been called before for the player
     */
    public synchronized void choosePersonalObjective(PlayerLobby player, CardObjectiveIF cardObj)
            throws RemoteException, InvalidPlayerException, InvalidChoiceException, VariableAlreadySetException, GameStatusException {
        gameController.choosePersonalObjective(player, cardObj);
    }

    /**
     * Plays a given card side on the field, at the given coordinates
     * @param card Card which is being played. It must be in player's hand
     * @param side Indicates whether the card is going to be played on the front or on the back
     * @param coord Coordinates in the field of the player where the card is going to be positioned
     * @throws RequirementsNotMetException If the requirements for playing the specified card in player's field are not met
     * @throws InvalidPlayCardException If the player doesn't have the specified card
     * @throws GameStatusException If this method is called in the INIT or CALC POINTS phase
     * @throws InvalidPlayerException If the passed player is not the current player
     */
    public void playCard(PlayerLobby playerLobby, CardPlayableIF card, Side side, Coordinates coord)
            throws RemoteException, RequirementsNotMetException, InvalidPlayCardException, GameStatusException, InvalidPlayerException {
        gameController.playCard(playerLobby, card, side, coord);
    }

    /**
     * Picks one of the 6 cards on the table, and makes the game proceed to the next Turn.
     * If there is no other turn to play after this one, it adds the objective points and calculates the winner.
     * @param card A playable card that should be in the field
     * @throws InvalidDrawCardException if the passed card is not on the table
     * @throws GameStatusException if this method is called in the INIT or CALC POINTS phase
     * @throws InvalidPlayerException If the passed player is not the current player
     */
    public void pickCard(PlayerLobby playerLobby, CardPlayableIF card)
            throws RemoteException, InvalidDrawCardException, GameStatusException, InvalidPlayerException {
        gameController.pickCard(playerLobby, card);
    }

    /**
     * @return Unique number indicating the actual game model (hence the game/match to its all entirety)
     */
    public int getGameId() throws RemoteException {
        return gameController.getGameId();
    }

    /**
     *
     * @return the players of the match
     */
    public List<PlayerLobby> getPlayers() throws RemoteException {
        return gameController.getPlayers();
    }
}
