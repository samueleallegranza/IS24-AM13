package it.polimi.ingsw.am13.model.card;

/**
 * Represents a card that can be played in player's field sometime during the game.
 * Hence, it could be a resource card, a gold card or a starter card.
 * It can return the card's id and its visible side (if it's visible in someone's field of in the commond field)
 */
public interface CardPlayableIF {

    /**
     * @return Id of the card
     */
    String getId();

    /**
     * @return The visible side of the card. If the card is not in field, returns <code>null</code>.
     */
    Side getVisibleSide();

}
