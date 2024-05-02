package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.network.socket.message.Message;

public abstract class MsgResponse extends Message {
    private final String command;
    private final String type;

    public MsgResponse(String command, String type) {
        this.command = command;
        this.type = type;
    }

    public String getCommand() {
        return command;
    }

    public String getType() {
        return type;
    }
}
