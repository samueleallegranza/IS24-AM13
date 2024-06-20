package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.network.socket.message.Message;

/**
 * Abstract class for a response message, which is sent to the client by the server
 */
public abstract class MsgResponse extends Message {
    /**
     * The type of the response, which is useful to associate it to the corresponding request
     */
    private final String type;

    /**
     * Builds a new response message with the given type
     * @param type the type of the response
     */
    public MsgResponse(String type) {
        this.type = type;
    }

    /**
     * @return the type of the response
     */
    public String getType() {
        return type;
    }
}
