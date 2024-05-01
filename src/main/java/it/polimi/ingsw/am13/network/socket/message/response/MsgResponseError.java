package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.network.socket.message.command.MsgCommand;

import java.io.Serializable;

public class MsgResponseError extends MsgResponse implements Serializable {

    // `sourceCommand` represents the class of the source command (not its content, we don't need it)
    private Class<? extends MsgCommand> sourceCommand;
    private Exception exception;
    private String Message;

    public MsgResponseError(Class<? extends MsgCommand> sourceCommand, Exception exception, String message) {
        this.sourceCommand = sourceCommand;
        this.exception = exception;
        Message = message;
    }
}
