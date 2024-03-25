package it.polimi.ingsw.am13.model.exceptions;
/**
 * Exception thrown when CardFactory attempts to create an invalid card.
 */
public class InvalidCardCreationException extends ModelException{
    public InvalidCardCreationException (String msg){ super(msg);}
}
