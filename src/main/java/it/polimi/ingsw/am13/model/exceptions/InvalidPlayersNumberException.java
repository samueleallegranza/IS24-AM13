package it.polimi.ingsw.am13.model.exceptions;

import java.io.Serializable;

/**
 * This exception is thrown when the player number is not valid (ie &lt;2 or &gt;4), or there are different
 * players who have the same token
 */
public class InvalidPlayersNumberException extends Exception implements Serializable {
    public InvalidPlayersNumberException() {
        super();
    }

    /**
     * Constructor of the exception.
     * @param message the message to be shown when the exception will be handled.
     */
    public InvalidPlayersNumberException(String message) {
        super(message);
    }

}
