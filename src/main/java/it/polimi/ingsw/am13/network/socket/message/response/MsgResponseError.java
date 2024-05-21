package it.polimi.ingsw.am13.network.socket.message.response;

import java.io.Serializable;

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
