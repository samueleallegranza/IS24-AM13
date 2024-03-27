package it.polimi.ingsw.am13.model.card;

/**
 * Represents a card that can be played in player's field sometime during the game.
 * Hence, it could be a resource card, a gold card or a starter card.
 * It stores the information about the 2 sides belonging to the card.
 * Other information about the card played during the game are inherited from <code>Card</code>
 */
public abstract class CardPlayable extends Card {

    /**
     * Front (playable) side of this playable card
     */
    private final CardSidePlayable front;
    /**
     * Back (playable) side of this playable card
     */
    private final CardSidePlayable back;

//    private Coordinates coords;
    //TODO: secondo Matteo coords è inutile, pensare se lasciare o togliere

    /**
     * Sets all immutable parameters of a playable card while creating a new one.
     * The card is created not visible in field
     * @param id Id of the card
     * @param front Front side of the card
     * @param back Back side of the card
     */
    public CardPlayable(String id, CardSidePlayable front, CardSidePlayable back) {
        super(id);
        this.front = front;
        this.back = back;
    }

    /**
     * If the card is visible in field, retrieves the side that is visible
     * @return Visible card side of the card. Null if the card is not visible.
     */
    public CardSidePlayable getPlayedCardSide() {
        if(getVisibleSide()==null)
            return null;
        return getVisibleSide()==Side.SIDEFRONT ? front : back;
    }

    /**
     * @return Front side of the card
     */
    public CardSidePlayable getFront() {
        return front;
    }

    /**
     * @return Back side of the card
     */
    public CardSidePlayable getBack() {
        return back;
    }
}
