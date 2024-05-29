package it.polimi.ingsw.am13.model.card;

/**
 * Represents a card that can be played in player's field sometime during the game.
 * Hence, it could be a resource card, a gold card or a starter card.
 * It can return the card's id and its visible side (if it's visible in someone's field of in the commond field)
 */
public interface CardPlayableIF extends CardIF {

    /**
     * @return The card side which is visible in the field (player's field or common field)
     * Null if the card is not visible in the field
     */
    CardSidePlayableIF getPlayedCardSide();

    /**
     * @param side Side whose card side is to be retrieved
     * @return Card side corresponding to specified side
     */
    CardSidePlayableIF getSide(Side side);
}
