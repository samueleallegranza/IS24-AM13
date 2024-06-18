package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.model.card.CardPlayableIF;

import java.io.Serializable;

/**
 * Command message which is sent to pick a card
 */
public class MsgCommandPickCard extends MsgCommand implements Serializable {
    /**
     * The card that the player wants to pick
     */
    private final CardPlayableIF card;

    /**
     * Creates the message by setting the attribute to the parameter
     * @param card that the player wants to pick
     */
    public MsgCommandPickCard(CardPlayableIF card) {
        super();
        this.card = card;
    }

    /**
     *
     * @return the card that the player wants to pick
     */
    public CardPlayableIF getCard() {
        return card;
    }
}
