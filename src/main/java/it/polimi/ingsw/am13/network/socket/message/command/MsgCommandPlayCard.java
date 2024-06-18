package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;

import java.io.Serializable;

/**
 * Command message which is sent to play a card
 */
public class MsgCommandPlayCard extends MsgCommand implements Serializable {
    /**
     * The card the player wants to play
     */
    private final CardPlayableIF card;
    /**
     * The coordinates in the field where the card should be played
     */
    private final Coordinates coords;
    /**
     * The side on which the card should be played
     */
    private final Side side;

    /**
     * Creates the message by setting the attributes to the parameters
     * @param card the player wants to play
     * @param coords where the card should be played
     * @param side on which the card should be played
     */
    public MsgCommandPlayCard(CardPlayableIF card, Coordinates coords, Side side) {
        super();
        this.card = card;
        this.coords = coords;
        this.side = side;
    }

    /**
     *
     * @return the card the player wants to play
     */
    public CardPlayableIF getCard() {
        return card;
    }

    /**
     *
     * @return the side on which the card should be played
     */
    public Side getSide() {
        return side;
    }

    /**
     *
     * @return the coordinates in the field where the card should be played
     */
    public Coordinates getCoords() {
        return coords;
    }
}
