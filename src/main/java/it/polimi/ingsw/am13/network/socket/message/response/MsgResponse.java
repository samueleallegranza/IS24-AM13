package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.network.socket.message.Message;

public abstract class MsgResponse extends Message {
    private final String type;

    public MsgResponse(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
