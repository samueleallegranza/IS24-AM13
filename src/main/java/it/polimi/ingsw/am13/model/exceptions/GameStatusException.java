package it.polimi.ingsw.am13.model.exceptions;

import it.polimi.ingsw.am13.model.GameStatus;

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
