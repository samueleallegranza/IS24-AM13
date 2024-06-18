package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.model.card.CardObjectiveIF;

import java.io.Serializable;

/**
 * Command message which is sent to choose the personal objective
 */
public class MsgCommandChoosePersonalObjective extends MsgCommand implements Serializable {
    /**
     * The chosen personal objective
     */
    private final CardObjectiveIF card;

    /**
     * Creates the message by setting the attribute to the parameter
     * @param card the chosen personal objective
     */
    public MsgCommandChoosePersonalObjective(CardObjectiveIF card) {
        super();
        this.card = card;
    }

    /**
     *
     * @return the chosen personal objective
     */
    public CardObjectiveIF getCard() {
        return card;
    }
}
