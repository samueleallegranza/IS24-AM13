package it.polimi.ingsw.am13.network.socket.message.response;

import java.io.Serializable;

public class MsgResponseFinalPhase extends MsgResponse implements Serializable {

    private final int turnsToEnd;
    public MsgResponseFinalPhase(int turnsToEnd) {
        super("resFinalPhase");
        this.turnsToEnd = turnsToEnd;
    }

    public int getTurnsToEnd() {
        return turnsToEnd;
    }
}
