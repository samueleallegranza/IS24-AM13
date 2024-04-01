package it.polimi.ingsw.am13.model;
import it.polimi.ingsw.am13.model.card.Card;
import it.polimi.ingsw.am13.model.card.CardPlayable;
import it.polimi.ingsw.am13.model.exceptions.InvalidDrawCardException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
/**
 * This class represents a deck and two visible cards
 * which sit on the Field at all times.
 * @param <T> A subclass of {@link Card}
 */
public class DeckHandler<T extends Card>{
    /**
     * This attributes contains the deck of cards.
     */
    private final Deck<T> deck;

    /**
     * It stores the deck's two cards which are visible at all times on the field.
     */
    private final List<T> visibleCards;

    /**
     * Creates a new {@link Deck} and the two visible cards by drawing them from the deck.
     * @param deck LinkedList of the deck's cards
     */
    public DeckHandler(LinkedList<T> deck){
        this.deck = new Deck<T>(deck);
        this.deck.shuffle();
        visibleCards=new ArrayList<>();
        try {
            visibleCards.add(drawFromDeck());
            visibleCards.add(drawFromDeck());
        }
        catch (InvalidDrawCardException e){
            System.out.println("The passed list in not sufficiently big to initialize the deck");
        }
    }

    /**
     * Draws the Card at the top of this deck
     * @return a subclass of {@link Card}
     * @throws InvalidDrawCardException if the deck is empty
     */
    public T drawFromDeck() throws InvalidDrawCardException {
        //System.out.println("deckk");
        return deck.draw();
    }

    /**
     * Draws a card from the visible ones anc unveils its replacement by drawing it from the
     * @param visibleIndex It's the spot from which the card is taken
     *                     It can only be either 0 or 1
     * @return the chosen card
     * @throws IndexOutOfBoundsException if <code>visibleIndex</code> is an invalid index.
     * @throws InvalidDrawCardException if the deck is empty after taking a visible card from the field.
     */
    public T drawFromTable(int visibleIndex) throws InvalidDrawCardException{
        T card = visibleCards.get(visibleIndex);
        visibleCards.remove(visibleIndex);
        if(!deck.isEmpty())
            visibleCards.add(visibleIndex, deck.draw());
        return card;
    }

    /**
     * Retrieves a copy of the chosen visible card without removing it.
     * This method should not be used to carry out the game flow.
     * @param visibleIndex It's the spot from which the card is taken
     *                     It can only be either 0 or 1
     * @return the chosen card
     * @throws IndexOutOfBoundsException if <code>visibleIndex</code> is an invalid index.
     */
    public T showFromTable(int visibleIndex) {
        return visibleCards.get(visibleIndex);
    }

    /**
     * Checks if this deck is empty.
     * @return <code>true</code> if this deck is empty.
     */
    public boolean isDeckEmpty(){
        return deck.isEmpty();
    }

    /**
     * Retrieves a copy of the first card of the deck without removing it.
     * This method should not be used to carry out the game flow.
     * @return the first card of the deck
     * @throws InvalidDrawCardException if the deck is empty
     */
    public T getDeckTop() throws InvalidDrawCardException {
        return deck.getTop();
    }

    public List<T> getVisibleCards(){
        return visibleCards;
    }
    public List<T> getPickables() throws InvalidDrawCardException{
        List<T> pickableCards=new ArrayList<>();
        if(!deck.isEmpty())
            pickableCards.add(deck.getTop());
        pickableCards.addAll(visibleCards);
        return pickableCards;
    }

    public boolean pickCard(CardPlayable cardPlayable) throws InvalidDrawCardException{
        boolean found=false;
        if(!deck.isEmpty() && getDeckTop()==cardPlayable){
            drawFromDeck();
            found=true;
        }
        for (int i = 0; i < visibleCards.size() && !found; i++) {
            if(showFromTable(i)==cardPlayable){
                drawFromTable(i);
                found=true;
            }
        }
        return found;
    }
}
