package it.polimi.ingsw.am13.network.rmi;

import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface representing gameController for the RMI connection. Hence this interface is exposed to the network as RMI remote interface.
 * The controller is the controller given to a particular players using RMI for connection. This implies the methods called
 * via this gameController are implicitly referred to the players it belongs to.
 * <br>
 * This is a controller for a single game/match
 */
public interface GameControllerRMIIF extends Remote {

    /**
     * Updates the ping of the player's listener by setting it to the current time
     */
    void updatePing() throws RemoteException;

    /**
     * Method callable only once during INIT phase.
     * It plays the starter card of the player on the passed side
     * @param side The side of the starting card
     * @throws GameStatusException If game phase is not INIT
     * @throws InvalidPlayCardException Positioning error of the card at coordinates (0,0).
     */
    void playStarter(Side side) throws RemoteException, InvalidPlayCardException, GameStatusException;

    /**
     * Method callable only during INIT phase. Sets the personal objective of the player according to his choice.
     * The objective card must be between the 2 possible cards given to the player. They can be retrieved with
     * <code>fetchPersonalObjectives(player)</code>
     * @param cardObj the objective chosen by the player
     * @throws GameStatusException if this method isn't called in the INIT phase
     * @throws InvalidChoiceException if the objective card does not belong to the list of the possible objective cards for the player
     * @throws VariableAlreadySetException if this method has been called before for the player
     */
    void choosePersonalObjective(CardObjectiveIF cardObj)
            throws RemoteException, InvalidChoiceException, VariableAlreadySetException, GameStatusException;

    /**
     * Plays a given card side on the field, at the given coordinates
     * @param card Card which is being played. It must be in player's hand
     * @param side Indicates whether the card is going to be played on the front or on the back
     * @param coord Coordinates in the field of the player where the card is going to be positioned
     * @throws RequirementsNotMetException If the requirements for playing the specified card in player's field are not met
     * @throws InvalidPlayCardException If the player doesn't have the specified card
     * @throws GameStatusException If this method is called in the INIT or CALC POINTS phase
     */
    void playCard(CardPlayableIF card, Side side, Coordinates coord)
            throws RemoteException, RequirementsNotMetException, InvalidPlayCardException, GameStatusException;

    /**
     * Picks one of the 6 cards on the table, and makes the game proceed to the next Turn.
     * If there is no other turn to play after this one, it adds the objective points and calculates the winner.
     * @param card A playable card that should be in the field
     * @throws InvalidDrawCardException if the passed card is not on the table
     * @throws GameStatusException if this method is called in the INIT or CALC POINTS phase
     */
    void pickCard(CardPlayableIF card)
            throws RemoteException, InvalidDrawCardException, GameStatusException;

    /**
     * @return Unique number indicating the actual game model (hence the game/match to its all entirety)
     */
    int getGameId() throws RemoteException;

    /**
     *
     * @return the players of the match
     */
    List<PlayerLobby> getPlayers() throws RemoteException;
}
