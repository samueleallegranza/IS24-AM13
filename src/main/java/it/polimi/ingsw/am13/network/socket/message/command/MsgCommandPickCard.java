package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.model.card.CardPlayableIF;

import java.io.Serializable;

public class MsgCommandPickCard extends MsgCommand implements Serializable {
    private final CardPlayableIF card;

    public MsgCommandPickCard(CardPlayableIF card) {
        this.card = card;
    }

    public CardPlayableIF getCard() {
        return card;
    }
}
