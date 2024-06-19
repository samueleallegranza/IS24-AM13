package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.network.socket.message.Message;

/**
 * Abstract class for a response message, which is sent to the client by the server
 */
//todo aggiungi java doc di metodi e attributi di questa classe e delle sue sottoclassi
public abstract class MsgResponse extends Message {
    private final String type;

    public MsgResponse(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
