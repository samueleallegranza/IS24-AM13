package it.polimi.ingsw.am13.model.card;

public abstract class CardPlayable extends Card {

    final private Color color;

    public CardPlayable(String id, CardSide front, CardSide back, Color color) {
        super(id, front, back);
        this.color = color;
    }

}
