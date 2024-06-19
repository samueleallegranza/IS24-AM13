package it.polimi.ingsw.am13.model.exceptions;

/**
 * Exception thrown when the player tries to draw a card and the deck is empty
 */
public class InvalidDrawCardException extends ModelException {
    /**
     * Constructor of the exception.
     * @param msg the message to be shown when the exception will be handled.
     */
    public InvalidDrawCardException(String msg){super(msg);}
}
