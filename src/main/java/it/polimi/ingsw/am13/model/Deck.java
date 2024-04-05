package it.polimi.ingsw.am13.model;
import it.polimi.ingsw.am13.model.card.Card;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.exceptions.InvalidDrawCardException;

import java.util.Collections;
import java.util.LinkedList;

/**
 * This class represent a physical deck of cards
 * @param <T> A subclass of {@link Card}
 */
public class Deck<T extends Card>{
    /**
     * This attribute represents a deck using a LinkedList
     */
    private final LinkedList<T> deck;

    /**
     * This is the constructor of a Deck
     * @param deck LinkedList of the deck's cards
     */
    public Deck(LinkedList<T> deck){
        this.deck = deck;
    }

    /**
     * Shuffles this deck randomly, and sets visibility of the new top card
     */
    public void shuffle(){
        Collections.shuffle(deck);
    }

    /**
     * Draws the Card at the top of this deck. Manages visibility of card drawn and new possible card visible
     * @return a subclass of {@link Card}
     * @throws InvalidDrawCardException if the deck is empty
     */
    public T draw() throws InvalidDrawCardException {
        T card = deck.pollFirst();
        if (card == null) {
            throw new InvalidDrawCardException("Draw failed. The deck is empty.");
        }
        card.removeCardFromField();
        if(!deck.isEmpty())
            deck.getFirst().placeCardInField(Side.SIDEBACK);
        return card;
    }

    /**
     * Retrieves a copy of the first card of this deck without removing it.
     * This method should not be used to carry out the game flow.
     * @return the first card of this deck
     * @throws InvalidDrawCardException if the deck is empty
     */
    public T getTop() throws InvalidDrawCardException{
        if (isEmpty())
            throw new InvalidDrawCardException("The deck is empty.");
        return deck.getFirst();
    }

    /**
     * Checks if this deck is empty.
     * @return <code>true</code> if this deck is empty.
     */
    public boolean isEmpty(){
        return deck.isEmpty();
    }

    /**
     * Returns the number of remaining cards in this deck.
     * @return the number of remaining cards in this deck.
     */
    public int getSize(){
        return deck.size();
    }


}