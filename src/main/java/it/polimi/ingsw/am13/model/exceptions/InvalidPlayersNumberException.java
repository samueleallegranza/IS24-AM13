package it.polimi.ingsw.am13.model.exceptions;

import java.io.Serializable;

/**
 * This exception is thrown when the player number is not valid (ie &lt;2 or &gt;4)
 */
//TODO: controlla meglio descrizione e suo utilizzo, al limite cambia nome
// (ora la usiamo anche se i token sono sbagliati)
public class InvalidPlayersNumberException extends Exception implements Serializable {
    public InvalidPlayersNumberException() {
        super();
    }
    public InvalidPlayersNumberException(String message) {
        super(message);
    }

}
