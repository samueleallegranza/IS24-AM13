package it.polimi.ingsw.am13.model.exceptions;

/**
 * Exception thrown when the player tries to draw a card and the deck is empty
 */
public class InvalidDrawCardException extends ModelException {
    public InvalidDrawCardException(String msg){super(msg);}
}
