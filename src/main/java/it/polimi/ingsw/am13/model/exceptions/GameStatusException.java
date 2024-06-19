package it.polimi.ingsw.am13.model.exceptions;

import it.polimi.ingsw.am13.model.GameStatus;

/**
 * Exception thrown if an action is done in the wrong game phase. See <code>GameStatus</code> class for more details.
 */
public class GameStatusException extends ModelException{
    /**
     * Constructor of the exception.
     * @param msg the message to be shown when the exception will be handled.
     */
    public GameStatusException(String msg) {
        super(msg);
    }

    /**
     * Constructor of the exception.
     * @param wrongStatus the current game status in which the action cannot be done.
     * @param rightStatus the status in which the action can be done.
     */
    public GameStatusException(GameStatus wrongStatus, GameStatus rightStatus){
        super(wrongStatus==null ? "We are currently in the pre init phase, this method can only be called in the "+rightStatus.toString()+" phase" :
                "We are currently in "+ wrongStatus +" phase, this method can only be called in the "+rightStatus.toString()+" phase");
    }
}
