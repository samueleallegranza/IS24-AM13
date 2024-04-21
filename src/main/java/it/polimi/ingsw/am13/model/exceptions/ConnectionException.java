package it.polimi.ingsw.am13.model.exceptions;

/**
 * This exception is thrown when there is attempt to reconnect a player which is already connected,
 * or disconnect a player which has already been disconnected.
 */
public class ConnectionException extends Exception{
    public ConnectionException(){
        super();
    }

    public ConnectionException(String msg){
        super(msg);
    }
}
