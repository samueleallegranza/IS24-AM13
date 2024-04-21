package it.polimi.ingsw.am13.model.exceptions;

/**
 * This exception is thrown when there is attempt to add a card to the hand of a player who has already three cards
 * (i.e. his hand is already full).
 */
public class PlayerHandException extends ModelException {
    public PlayerHandException(String message) {
        super(message);
    }
}
