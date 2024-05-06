package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;

import java.io.Serializable;

public class MsgCommandPlayCard extends MsgCommand implements Serializable {
    private final CardPlayableIF card;
    private final Coordinates coords;
    private final Side side;

    public MsgCommandPlayCard(CardPlayableIF card, Coordinates coords, Side side) {
        this.card = card;
        this.coords = coords;
        this.side = side;
    }

    public CardPlayableIF getCard() {
        return card;
    }

    public Side getSide() {
        return side;
    }

    public Coordinates getCoords() {
        return coords;
    }
}
