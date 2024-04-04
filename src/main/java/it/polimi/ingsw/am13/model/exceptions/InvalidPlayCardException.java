package it.polimi.ingsw.am13.model.exceptions;

/**
 * Exception related to wrong playing of a card by a player.
 * It could be thrown when the player tries to place a card on the field in an invalid spot,
 * or when the player tries to play a card that they don't have
 */
// TODO: controlla meglio se la descrizione corrisponde a come la usiamo
public class InvalidPlayCardException extends ModelException {

    public InvalidPlayCardException() {
        super();
    }

    public InvalidPlayCardException(String msg) {
        super(msg);
    }
}
