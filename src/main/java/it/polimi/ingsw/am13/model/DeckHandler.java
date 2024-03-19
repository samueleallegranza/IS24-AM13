package it.polimi.ingsw.am13.model;
import it.polimi.ingsw.am13.model.card.Card;

import java.util.List;

public class DeckHandler<T extends Card>{
    private Deck<T> deck;
    private List<T> visibleCards;

    public void initDeckHandler(){
        deck.initDeck();
    }

    public T drawFromDeck(){
        return deck.draw();
    }

    public T drawFromTable(int visibleIndex){
        return visibleCards.get(visibleIndex);
    }

}
