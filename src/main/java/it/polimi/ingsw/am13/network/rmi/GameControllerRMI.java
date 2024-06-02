package it.polimi.ingsw.am13.network.rmi;

import it.polimi.ingsw.am13.controller.GameController;
import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Version of class representing gameController for the RMI connection. Hence this class is exposed to the network as RMI remote object.
 * The controller is the controller given to a particular players using RMI for connection. This implies the methods called
 * via this gameController are implicitly referred to the players it belongs to.
 * <br>
 * This is a controller for a single game/match
 */
public class GameControllerRMI extends UnicastRemoteObject implements GameControllerRMIIF {

    // TODO: non dovrebbe servire transient sugli attributi, ma ragionaci meglio

    /**
     * Wrapped gameController
     */
    private final GameController gameController;
    private final PlayerLobby player;

    /**
     * Wraps the given gameController to be exposed to the network, and associates this specific controller to the given player
     * @param gameController gameController to wrap in order to be exposed to the network
     * @param player Player (client RMI) who will use this gameController
     * @throws InvalidPlayerException If the given player is not present in the game
     */
    public GameControllerRMI(GameController gameController, PlayerLobby player) throws RemoteException, InvalidPlayerException {
        super();
        this.gameController = gameController;
        if(!gameController.getPlayers().contains(player))
            throw new InvalidPlayerException("The given listener is of a player not present in the game");
        this.player = player;
    }
    // In this way all methods cannot throw InvalidException (I'm sure this controller is associated to a player present in the game)

    /**
     * Logging function which notifies the command which has been received on the server console .
     * @param cmd Command name
     */
    private void logCommand(String cmd) {
        System.out.printf("[RMI][%s] Received Command %s\n", player.getNickname(), cmd);
    }

    /**
     * Updates the ping of the player's listener by setting it to the current time
     */
    public synchronized void updatePing() throws RemoteException {
        gameController.updatePing(player);
    }

    /**
     * Method callable only once during INIT phase.
     * It plays the starter card of the player on the passed side
     * @param side The side of the starting card
     * @throws GameStatusException If game phase is not INIT
     * @throws InvalidPlayCardException Positioning error of the card at coordinates (0,0).
     */
    public synchronized void playStarter(Side side) throws RemoteException,
            InvalidPlayCardException, GameStatusException {
        logCommand("playStarter");
        try {
            gameController.playStarter(player, side);
        } catch (InvalidPlayerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method callable only during INIT phase. Sets the personal objective of the player according to his choice.
     * The objective card must be between the 2 possible cards given to the player. They can be retrieved with
     * <code>fetchPersonalObjectives(player)</code>
     * @param cardObj the objective chosen by the player
     * @throws GameStatusException if this method isn't called in the INIT phase
     * @throws InvalidChoiceException if the objective card does not belong to the list of the possible objective cards for the player
     * @throws VariableAlreadySetException if this method has been called before for the player
     */
    public synchronized void choosePersonalObjective(CardObjectiveIF cardObj)
            throws RemoteException, InvalidChoiceException, VariableAlreadySetException, GameStatusException {
        logCommand("chooseObj");
        try {
            gameController.choosePersonalObjective(player, cardObj);
        } catch (InvalidPlayerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Plays a given card side on the field, at the given coordinates
     * @param card Card which is being played. It must be in player's hand
     * @param side Indicates whether the card is going to be played on the front or on the back
     * @param coord Coordinates in the field of the player where the card is going to be positioned
     * @throws RequirementsNotMetException If the requirements for playing the specified card in player's field are not met
     * @throws InvalidPlayCardException If the player doesn't have the specified card
     * @throws GameStatusException If this method is called in the INIT or CALC POINTS phase
     */
    public void playCard(CardPlayableIF card, Side side, Coordinates coord)
            throws RemoteException, RequirementsNotMetException, InvalidPlayCardException, GameStatusException {
        logCommand("playCard");
        try {
            gameController.playCard(player, card, side, coord);
        } catch (InvalidPlayerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Picks one of the 6 cards on the table, and makes the game proceed to the next Turn.
     * If there is no other turn to play after this one, it adds the objective points and calculates the winner.
     * @param card A playable card that should be in the field
     * @throws InvalidDrawCardException if the passed card is not on the table
     * @throws GameStatusException if this method is called in the INIT or CALC POINTS phase
     */
    public void pickCard(CardPlayableIF card)
            throws RemoteException, InvalidDrawCardException, GameStatusException {
        logCommand("pickCard");
        try {
            gameController.pickCard(player, card);
        } catch (InvalidPlayerException e) {
            throw new RuntimeException(e);
        }
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

    /**
     * Transmits a chat message (while this message will not cause any change in the model, nor will it be stored,
     * it follows the same path as all the other messages)
     *
     * @param receivers of the message
     * @param text      content of the message
     */
    @Override
    public void transmitChatMessage(List<PlayerLobby> receivers, String text) throws RemoteException{
        if((receivers.size()!=1 && receivers.size()!=gameController.getPlayers().size()-1)
        || (!gameController.getPlayers().containsAll(receivers)) || receivers.contains(player))
            throw new RuntimeException();
        gameController.transmitChatMessage(player,receivers,text);
    }
}
