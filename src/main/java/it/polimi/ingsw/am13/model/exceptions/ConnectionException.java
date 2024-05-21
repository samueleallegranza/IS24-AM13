package it.polimi.ingsw.am13.model.exceptions;

import java.io.Serializable;

/**
 * This exception is thrown when there is attempt to reconnect a player which is already connected,
 * or disconnect a player which has already been disconnected.
 */
public class ConnectionException extends Exception implements Serializable {
    public ConnectionException(){
        super();
    }

    public ConnectionException(String msg){
        super(msg);
    }
}
