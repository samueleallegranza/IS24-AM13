package it.polimi.ingsw.am13.model.exceptions;

/**
 * This exception is thrown when there is attempt to add a card to the hand of a player who has already three cards
 * (i.e. his hand is already full).
 */
public class PlayerHandException extends ModelException {
    /**
     * Constructor of the exception.
     * @param message the message to be shown when the exception will be handled.
     */
    public PlayerHandException(String message) {
        super(message);
    }
}
