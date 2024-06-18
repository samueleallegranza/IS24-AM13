package it.polimi.ingsw.am13.network.socket.message.command;

import java.io.Serializable;

/**
 * Command messages which is sent to leave a room. It is empty
 */
public class MsgCommandLeaveRoom extends MsgCommand implements Serializable {

    // Empty

    /**
     * Empty constructor
     */
    public MsgCommandLeaveRoom() {
        super();
    }
}
