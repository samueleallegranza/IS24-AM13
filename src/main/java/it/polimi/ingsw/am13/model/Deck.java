package it.polimi.ingsw.am13.model;
import it.polimi.ingsw.am13.model.card.Card;

import java.util.Collections;
import java.util.LinkedList;

public class Deck<T extends Card>{
    private LinkedList<T> deck;

    public Deck(LinkedList<T> deck){
        this.deck = deck;
    }

    public void shuffle(){
        Collections.shuffle(deck);
    }

    public T draw(){
        return deck.pollFirst();
    }

}