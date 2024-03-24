package it.polimi.ingsw.am13.model.card;

public abstract class CardPlayable extends Card {

    private final CardSidePlayable front;
    private final CardSidePlayable back;

    public CardPlayable(String id, CardSidePlayable front, CardSidePlayable back) {
        super(id);
        this.front = front;
        this.back = back;
    }

    public CardSidePlayable getPlayedCardSide() {
        return getPlayedSide()==Side.SIDEFRONT ? front : back;
    }

    public CardSidePlayable getFront() {
        return front;
    }

    public CardSidePlayable getBack() {
        return back;
    }
}
