package it.polimi.ingsw.am13.model.exceptions;

import java.io.Serializable;

/**
 * This exception is thrown when there is attempt to reconnect a player which is already connected,
 * or disconnect a player which has already been disconnected.
 */
public class ConnectionException extends Exception implements Serializable {
    /**
     * Constructor of the exception.
     * @param msg the message to be shown when the exception will be handled.
     */
    public ConnectionException(String msg){
        super(msg);
    }
}
