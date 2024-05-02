package it.polimi.ingsw.am13.network.socket.message.response;

import java.io.Serializable;

public class MsgResponseIngame extends MsgResponse implements Serializable {
    public MsgResponseIngame(String command, String type) {
        super(command, type);
    }
}
