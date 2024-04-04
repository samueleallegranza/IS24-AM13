package it.polimi.ingsw.am13.model.exceptions;

/**
 * This exception is thrown when the player number is not valid (ie <2 or >4)
 */
//TODO: controlla meglio descrizione e suo utilizzo, al limite cambia nome
public class InvalidPlayersNumberException extends Exception{
    public InvalidPlayersNumberException() {
        super();
    }
    public InvalidPlayersNumberException(String message) {
        super(message);
    }

}
