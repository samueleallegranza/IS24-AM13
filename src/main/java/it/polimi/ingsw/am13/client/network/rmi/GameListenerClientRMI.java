package it.polimi.ingsw.am13.client.network.rmi;

import it.polimi.ingsw.am13.client.gamestate.GameStateHandler;
import it.polimi.ingsw.am13.controller.GameController;
import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.network.rmi.GameControllerRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

/**
 * Client-side listener for RMI connection.
 * It is a remote object, and it should be exposed by the client by passing it when it connects to the room/game.
 * It handles the updates the server sends to the clients, by modifying the view and updating the internal game's state
 */
public class GameListenerClientRMI extends UnicastRemoteObject implements Remote {

    /**
     * Player associated to this listener
     */
    private final PlayerLobby player;

    /**
     * Handler of the network, which creates this listener
     */
    private final NetworkHandlerRMI networkHandler;

    /**
     * Handler of the game's state
     */
    private GameStateHandler stateHandler;

    /**
     * Creates a new client-side listener, setting the player it represents and the network's handler which created it
     * @param player Player associated to the listener
     * @param networkHandler Handler of the network which created the listener
     */
    public GameListenerClientRMI(PlayerLobby player, NetworkHandlerRMI networkHandler) throws RemoteException {
        super();
        this.player = player;
        this.networkHandler = networkHandler;
        stateHandler = null;
    }


    //TODO: implementazione da finire con le chiamate ai metodi di view


    /**
     * @return Player associated to this listener
     */
    public PlayerLobby getPlayer() throws RemoteException {
        return player;
    }

    /**
     * A player has joined the room
     * @param player player who joined
     */
    public void updatePlayerJoinedRoom(PlayerLobby player) throws RemoteException {
        //TODO devo solo notificare la view
    }

    /**
     * A player left the room
     * @param player Player who left
     */
    public void updatePlayerLeftRoom(PlayerLobby player) throws RemoteException {
        //TODO devo solo notificate la view
    }

    /**
     * The game has started: starter cards and initial cards have been given to the players.
     * The specified model contains the initial set up for the started game.
     * @param model The game model containing the game status set to INIT.
     */
    public void updateStartGame(GameModelIF model, GameController controller)
            throws RemoteException, InvalidPlayerException {
        GameControllerRMI controllerRMI = new GameControllerRMI(controller, player);
        networkHandler.setController(controllerRMI);
        stateHandler = new GameStateHandler(model);
    }

    /**
     * A player has played their starter card.
     * @param player The player that played the starter card.
     * @param cardStarter The starter card played.
     * @param availableCoords The list of coordinates that are available to play a card
     */
    public void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter, List<Coordinates> availableCoords) throws RemoteException {
        stateHandler.updatePlayedStarter(player, cardStarter, availableCoords);
    }

    /**
     * A player has chosen their personal objective card.
     * @param player The player that chose the personal objective card.
     * @param chosenObj Objective card chosen by the player
     */
    public void updateChosenPersonalObjective(PlayerLobby player, CardObjectiveIF chosenObj) throws RemoteException {
        stateHandler.updateChosenPersonalObjective(player, chosenObj);
    }

    /**
     * The turn has passed to another player.
     * @param player The player that is going to play the next turn.
     */
    public void updateNextTurn(PlayerLobby player) throws RemoteException {
        stateHandler.updateNextTurn(player);
    }

    /**
     * A player plays a card.
     * @param player The player that played the card.
     * @param cardPlayed The card played.
     * @param coord The coordinates where the card has been placed, relative to the player's field.
     * @param points The points given by the card.
     * @param availableCoords The list of coordinates that are available to play a card
     */
    public void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayed, Coordinates coord,
                                 int points, List<Coordinates> availableCoords) throws RemoteException {
        stateHandler.updatePlayedCard(player, cardPlayed, coord, points, availableCoords);
    }

    /**
     * A player picks a card from the common visible cards.
     * @param player The player that picked the card.
     * @param updatedVisibleCards The updated list of visible cards in the common field.
     * @param pickedCard The card picked by the player
     */
    public void updatePickedCard(PlayerLobby player, List<CardPlayableIF> updatedVisibleCards, CardPlayableIF pickedCard) throws RemoteException {
        stateHandler.updatePickedCard(player, updatedVisibleCards, pickedCard);
    }

    /**
     * The points given by Objective cards (common and personal) have been calculated.
     * Following the game's rules, the specified points are the final ones
     * @param pointsMap A map containing the points of each player.
     */
    public void updatePoints(Map<PlayerLobby, Integer> pointsMap) throws RemoteException {
        stateHandler.updatePoints(pointsMap);
    }

    /**
     * The winner has been calculated
     * @param winner The player that has won the game.
     */
    public void updateWinner(PlayerLobby winner) throws RemoteException {
        stateHandler.updateWinner(winner);
    }

    /**
     * The game has ended.
     * After this update, the server should not respond to any other request
     */
    public void updateEndGame() throws RemoteException {
        //TODO devo solo notificare la view
    }

    /**
     * A player has disconnected from the game.
     * @param player The player that has disconnected.
     */
    public void updatePlayerDisconnected(PlayerLobby player) throws RemoteException {
        stateHandler.updatePlayerDisconnected(player);
    }

    /**
     * A player has reconnected to the game.
     * @param player The player that has reconnected.
     */
    public void updatePlayerReconnected(PlayerLobby player) throws RemoteException {
        stateHandler.updatePlayerReconnected(player);
    }

    /**
     * The game is in the final phase.
     */
    public void updateFinalPhase() throws RemoteException {
        stateHandler.updateFinalPhase();
    }

    /**
     * The game has begun the turn-based phase.
     */
    public void updateInGame() throws RemoteException {
        stateHandler.updateInGame();
    }

    /**
     * This method should be called once the player has reconnected an already started game.
     * It updates the entire game's state, i.e. it creates a new game's state starting from the specified model
     * (reconstructing the current situation in game).
     * @param model The updated game model.
     */
    public void updateGameModel(GameModelIF model) throws RemoteException, InvalidPlayerException {
        stateHandler = new GameStateHandler(model);
    }
}
