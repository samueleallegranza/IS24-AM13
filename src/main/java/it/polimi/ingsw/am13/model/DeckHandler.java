package it.polimi.ingsw.am13.model;
import it.polimi.ingsw.am13.model.card.Card;
import it.polimi.ingsw.am13.model.exceptions.InvalidDrawCardException;

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
    private List<T> visibleCards;

    /**
     * Creates a new {@link Deck} and the two visible cards by drawing them from the deck.
     * @param deck LinkedList of the deck's cards
     * @throws InvalidDrawCardException if the initialized deck is empty
     */
    public DeckHandler(LinkedList<T> deck) throws InvalidDrawCardException{
        this.deck = new Deck<T>(deck);
        this.deck.shuffle();
        visibleCards.add(drawFromDeck());
        visibleCards.add(drawFromDeck());
    }

    /**
     * Draws the Card at the top of this deck
     * @return a subclass of {@link Card}
     * @throws InvalidDrawCardException if the deck is empty
     */
    public T drawFromDeck() throws InvalidDrawCardException {
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
}
