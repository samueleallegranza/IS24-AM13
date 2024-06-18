package it.polimi.ingsw.am13.network.socket.message.response;

import java.io.Serializable;

/**
 * Response message when the game reaches the final phase
 */
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
