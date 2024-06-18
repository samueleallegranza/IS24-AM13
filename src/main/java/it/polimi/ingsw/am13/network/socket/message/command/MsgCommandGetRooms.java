package it.polimi.ingsw.am13.network.socket.message.command;

import java.io.Serializable;

/**
 * Command message which is sent to get the existing rooms. It is empty
 */
public class MsgCommandGetRooms extends MsgCommand implements Serializable {

    // Empty

    /**
     * Empty constructor
     */
    public MsgCommandGetRooms() {
        super();
    }
}
