package it.polimi.ingsw.am13.network.socket.message.response;

import java.io.Serializable;

/**
 * Response message that is sent to a client whenever one of his commands causes an exception
 */
public class MsgResponseError extends MsgResponse implements Serializable {
    private final Exception exception;

    public MsgResponseError(Exception exception) {
        super("error");
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}
