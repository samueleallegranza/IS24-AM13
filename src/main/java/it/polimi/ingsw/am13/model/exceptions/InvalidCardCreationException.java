package it.polimi.ingsw.am13.model.exceptions;
/**
 * Exception thrown when CardFactory attempts to create an invalid card.
 */
public class InvalidCardCreationException extends ModelException{
    /**
     * Constructor of the exception.
     * @param msg the message to be shown when the exception will be handled.
     */
    public InvalidCardCreationException (String msg){ super(msg);}
}
