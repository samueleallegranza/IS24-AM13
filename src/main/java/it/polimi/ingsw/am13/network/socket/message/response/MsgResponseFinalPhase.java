package it.polimi.ingsw.am13.network.socket.message.response;

import java.io.Serializable;

/**
 * Response message when the game reaches the final phase
 */
public class MsgResponseFinalPhase extends MsgResponse implements Serializable {
    /**
     * The number of turns left before the end of the game
     */
    private final int turnsToEnd;

    /**
     * Builds a new response message with the given number of turns left
     * @param turnsToEnd the number of turns left before the end of the game
     */
    public MsgResponseFinalPhase(int turnsToEnd) {
        super("resFinalPhase");
        this.turnsToEnd = turnsToEnd;
    }

    /**
     * @return the number of turns left before the end of the game
     */
    public int getTurnsToEnd() {
        return turnsToEnd;
    }
}
