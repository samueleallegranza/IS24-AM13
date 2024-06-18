package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.model.card.Side;

import java.io.Serializable;

/**
 * Command message which is sent to play the starter card
 */
public class MsgCommandPlayStarter extends MsgCommand implements Serializable {
    /**
     * The side on which the starter card should be played
     */
    private final Side side;

    /**
     * Creates the message by setting the attribute to the parameter
     * @param side on which the starter card should be played
     */
    public MsgCommandPlayStarter(Side side) {
        super();
        this.side = side;
    }

    /**
     *
     * @return the side on which the starter card should be played
     */
    public Side getSide() {
        return side;
    }
}
