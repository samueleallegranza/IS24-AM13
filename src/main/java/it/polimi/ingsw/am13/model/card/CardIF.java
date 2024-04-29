package it.polimi.ingsw.am13.model.card;

import java.io.Serializable;

public interface CardIF extends Serializable {

    /**
     * @return Id of the card
     */
    String getId();

    /**
     * @return The visible side of the card. If the card is not in field, returns <code>null</code>.
     */
    Side getVisibleSide();
}
