package it.polimi.ingsw.am13.model.exceptions;

import java.io.Serializable;

/**
 * This exception is thrown when the player number is not valid (ie &lt;2 or &gt;4), or there are different
 * players who have the same token
 */
//TODO: la usiamo anche se ci sono token uguali per giocatori diversi, valuta se cambiare nome
public class InvalidPlayersNumberException extends Exception implements Serializable {
    public InvalidPlayersNumberException() {
        super();
    }
    public InvalidPlayersNumberException(String message) {
        super(message);
    }

}
