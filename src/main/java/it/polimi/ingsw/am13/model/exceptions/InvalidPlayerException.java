package it.polimi.ingsw.am13.model.exceptions;

/**
 * This exception was the player passed to a method is not valid, either because he doesn't belong to the match
 * or because he isn't allowed to call that method at that moment (i.e. he is playing or picking a card when it's not his turn).
 */
public class InvalidPlayerException extends ModelException{
    public InvalidPlayerException() {
    }

    public InvalidPlayerException(String msg) {
        super(msg);
    }
}
