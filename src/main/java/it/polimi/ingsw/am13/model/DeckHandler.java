package it.polimi.ingsw.am13.model;
import it.polimi.ingsw.am13.model.card.Card;

import java.util.LinkedList;
import java.util.List;

public class DeckHandler<T extends Card>{
    private Deck<T> deck;
    private List<T> visibleCards;

    public DeckHandler(LinkedList<T> deck){
        this.deck = new Deck<T>(deck);
        this.deck.shuffle();
        for (int i = 0; i < 6; i++) {
            visibleCards.add(drawFromDeck());
        }
    }

    public T drawFromDeck(){
        return deck.draw();
    }

    public T drawFromTable(int visibleIndex){
        return visibleCards.get(visibleIndex);
    }

}
