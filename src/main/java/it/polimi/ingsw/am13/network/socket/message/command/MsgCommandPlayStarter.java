package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Side;

import java.io.Serializable;

public class MsgCommandPlayStarter extends MsgCommand implements Serializable {
    private final Side side;

    public MsgCommandPlayStarter(Side side) {
        super();
        this.side = side;
    }

    public Side getSide() {
        return side;
    }
}
