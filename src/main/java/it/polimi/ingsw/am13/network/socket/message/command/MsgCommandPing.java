package it.polimi.ingsw.am13.network.socket.message.command;

import java.io.Serializable;

/**
 * Command message corresponding to a ping from the client. It is empty
 */
public class MsgCommandPing extends MsgCommand implements Serializable {

    // Empty

    /**
     * Empty constructor
     */
    public MsgCommandPing() {
        super();
    }
}
