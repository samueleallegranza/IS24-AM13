package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.network.socket.message.command.MsgCommand;

import java.io.Serializable;

public class MsgResponseError extends MsgResponse implements Serializable {
    private Exception exception;
    private String Message;

    public MsgResponseError(String command, Exception exception, String message) {
        super(command, "error");
        this.exception = exception;
        Message = message;
    }
}
