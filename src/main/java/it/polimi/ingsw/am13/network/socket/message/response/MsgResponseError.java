package it.polimi.ingsw.am13.network.socket.message.response;

import java.io.Serializable;

/**
 * Response message that is sent to a client whenever one of his commands causes an exception
 */
public class MsgResponseError extends MsgResponse implements Serializable {
    /**
     * The exception that has been thrown
     */
    private final Exception exception;

    /**
     * Builds a new response message with the given exception
     * @param exception the exception that has been thrown
     */
    public MsgResponseError(Exception exception) {
        super("error");
        this.exception = exception;
    }

    /**
     * @return the exception that has been thrown
     */
    public Exception getException() {
        return exception;
    }
}
