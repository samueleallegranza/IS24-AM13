package it.polimi.ingsw.am13.model.exceptions;

/**
 * Exception related to wrong playing of a card by a player.
 * It could be thrown when the player tries to place a card on the field in an invalid spot,
 * or when the player tries to play a card that they don't have
 */
public class InvalidPlayCardException extends ModelException {

    public InvalidPlayCardException() {
        super();
    }

    /**
     * Constructor of the exception.
     * @param msg the message to be shown when the exception will be handled.
     */
    public InvalidPlayCardException(String msg) {
        super(msg);
    }
}
