package it.polimi.ingsw.am13.model.exceptions;

/**
 * Exception thrown when the player tries to place a card on the field in an invalid spot
 */
public class InvalidPlayCardException extends ModelException {

    public InvalidPlayCardException() {
        super();
    }

    public InvalidPlayCardException(String msg) {
        super(msg);
    }
}
