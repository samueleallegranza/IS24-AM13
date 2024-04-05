package it.polimi.ingsw.am13.model;
import it.polimi.ingsw.am13.model.card.Card;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Side;
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

    //TODO: sistema ritorno di pickables con optional (lista di lunghezza fissa a 6)

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
        this.deck = new Deck<>(deck);
        visibleCards = new ArrayList<>();
        T visibleCard;
        try {
            this.deck.shuffle();
            visibleCard = drawFromDeck();
            visibleCard.placeCardInField(Side.SIDEFRONT);
            visibleCards.add(visibleCard);
            visibleCard = drawFromDeck();
            visibleCard.placeCardInField(Side.SIDEFRONT);
            visibleCards.add(visibleCard);
            this.deck.getTop().placeCardInField(Side.SIDEBACK);
        }
        catch (InvalidDrawCardException e){
            System.out.println("The passed list in not sufficiently big to initialize the deck");
        }
    }

    /**
     * Draws the Card at the top of this deck. Manages visibility of card drawn and new possible card visible
     * @return a subclass of {@link Card}
     * @throws InvalidDrawCardException if the deck is empty
     */
    public T drawFromDeck() throws InvalidDrawCardException {
        return deck.draw();
    }

    //TODO: controlla InvalidDrawCardException

    /**
     * Draws a card from the visible ones anc unveils its replacement by drawing it from the
     * @param visibleIndex It's the spot from which the card is taken
     *                     It can only be either 0 or 1
     * @return the chosen card
     * @throws IndexOutOfBoundsException if <code>visibleIndex</code> is an invalid index.
     * @throws InvalidDrawCardException if the deck is empty after taking a visible card from the field.
     */
    public T drawFromTable(int visibleIndex) throws IndexOutOfBoundsException, InvalidDrawCardException{
        if(visibleIndex!=0 && visibleIndex!=1)
            throw new IndexOutOfBoundsException();
        T card = visibleCards.get(visibleIndex);
        card.removeCardFromField();
        visibleCards.remove(visibleIndex);

        if(!deck.isEmpty()) {
            T cardDrawn = deck.draw();
            cardDrawn.placeCardInField(Side.SIDEFRONT);
            visibleCards.add(visibleIndex, cardDrawn);
        }
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

    //TODO: e se non ci sono alcune di queste carte??
    /**
     * List of all visible cards (that are pickable during turn phases).
     * The list is of size 3, with order: top of deck (with <code>getVisibleSide()==Side.SIDEBACK</code>),
     * and 2 visible cards (with <code>getVisibleSide()==Side.SIDEFRONT</code>)
     * @return List of visible cards (top of deck, 2 visible cards)
     * @throws InvalidDrawCardException ??
     */
    public List<T> getPickables() throws InvalidDrawCardException{
        List<T> pickableCards=new ArrayList<>();
        if(!deck.isEmpty())
            pickableCards.add(deck.getTop());
        pickableCards.addAll(visibleCards);
        return pickableCards;
    }

    //TODO: gestire meglio i casi limite di pescaggio quando non ci sono più carte...

    /**
     * If the specified card is found among the 3 "visible" (top of the deck and the 2 actually visibile), it is picked and returned.
     * The method is thought for cards that can be picked and played in a later moment, hence the specified card must be playable.
     * @param cardPlayable Card to be picked (if present)
     * @return The card picked if present, null otherwise
     * @throws InvalidDrawCardException ???
     */
    public T pickCard(CardPlayableIF cardPlayable) throws InvalidDrawCardException{
        if(!deck.isEmpty() && getDeckTop()==cardPlayable){
            return drawFromDeck();
        }
        for (int i = 0; i < visibleCards.size(); i++) {
            if(showFromTable(i)==cardPlayable){
                return drawFromTable(i);
            }
        }
        return null;
    }
}
