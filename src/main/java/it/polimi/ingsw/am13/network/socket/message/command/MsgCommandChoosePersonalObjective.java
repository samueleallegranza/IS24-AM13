package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.model.card.CardObjectiveIF;

import java.io.Serializable;

public class MsgCommandChoosePersonalObjective extends MsgCommand implements Serializable {
    private final CardObjectiveIF card;

    public MsgCommandChoosePersonalObjective(CardObjectiveIF card) {
        super();
        this.card = card;
    }

    public CardObjectiveIF getCard() {
        return card;
    }
}
