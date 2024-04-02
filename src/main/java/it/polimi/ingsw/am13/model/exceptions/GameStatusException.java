package it.polimi.ingsw.am13.model.exceptions;

import it.polimi.ingsw.am13.model.GameStatus;

/**
 * Exception thrown if an action is done in the wrong game phase. See <code>GameStatus</code> class for more details.
 */
public class GameStatusException extends ModelException{
    public GameStatusException() {
    }

    public GameStatusException(String msg) {
        super(msg);
    }
    public GameStatusException(GameStatus wrongStatus, GameStatus rightStatus){
        super("We are currently in "+wrongStatus.toString()+" phase, this method can only be called in the "+rightStatus.toString()+" phase");
    }
}
