package it.polimi.ingsw.am13.network.socket.message.command;

import java.io.Serializable;

public class MsgCommandLeaveRoom extends MsgCommand implements Serializable {

    // Empty

    public MsgCommandLeaveRoom(String nickname) {
        super(nickname);
    }
}
