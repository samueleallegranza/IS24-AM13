package it.polimi.ingsw.am13.model;
import it.polimi.ingsw.am13.model.card.Card;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.exceptions.InvalidDrawCardException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
     * The size is always 2. If deck is not empty, both cards are not-null.
     * If the deck is empty, one or both of the cards can be null.
     * The list is order-preserving (if a card is placed in pos 1, it will remain in pos 1 till it's removed)
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

    /**
     * Draws a card from the visible ones anc unveils its replacement by drawing it from the
     * @param visibleIndex It's the spot from which the card is taken
     *                     It can only be either 0 or 1
     * @return the chosen card
     * @throws IndexOutOfBoundsException if <code>visibleIndex</code> is an invalid index.
     * @throws InvalidDrawCardException If the visible card is not present (is null)
     */
    public T drawFromTable(int visibleIndex) throws IndexOutOfBoundsException, InvalidDrawCardException{
        if(visibleIndex!=0 && visibleIndex!=1)
            throw new IndexOutOfBoundsException();
        T card = visibleCards.get(visibleIndex);
        if(card==null)
            throw new InvalidDrawCardException("");
        card.removeCardFromField();
        visibleCards.remove(visibleIndex);

        if(!deck.isEmpty()) {
            T cardDrawn = deck.draw();
            cardDrawn.placeCardInField(Side.SIDEFRONT);
            visibleCards.add(visibleIndex, cardDrawn);
        } else
            visibleCards.add(visibleIndex, null);
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

    /**
     * List of all visible cards (that are pickable during turn phases).
     * The list is of size 3, with order: top of deck (with <code>getVisibleSide()==Side.SIDEBACK</code>),
     * and 2 visible cards (with <code>getVisibleSide()==Side.SIDEFRONT</code>).
     * Elements can be null. If the deck is empty but both cards are present, only the first element will be null.
     * Besides first element, also one or both of the other ones can be null (if it remains only one or no cards
     * of this type to be picked)
     * @return A new list of visible cards (top of deck, 2 visible cards). The size is 3
     */
    public List<T> getPickables() {
        List<T> pickableCards=new ArrayList<>();
        try {
            pickableCards.add(deck.getTop());
        } catch (InvalidDrawCardException e) {
            pickableCards.add(null);
        }
//        if(!deck.isEmpty())
//            pickableCards.add(deck.getTop());
//        else
//            pickableCards.add(null);
        pickableCards.addAll(visibleCards);
        return pickableCards;
    }

    /**
     * List of all visible cards (that are pickable during turn phases).
     * The list is of size 3, with order: top of deck (with <code>getVisibleSide()==Side.SIDEBACK</code>),
     * and 2 visible cards (with <code>getVisibleSide()==Side.SIDEFRONT</code>).
     * If the deck is empty but both cards are present, only the first optional will be empty.
     * Besides first element, also one or both of the other ones can be empty optionals
     * (if it remains only one or no cards of this type to be picked)
     * @return List of visible cards (top of deck, 2 visible cards)
     */
    public List<Optional<T>> getPickablesOptional() {
        List<Optional<T>> pickableCards=new ArrayList<>();
        try {
            pickableCards.add(Optional.of(deck.getTop()));
        } catch (InvalidDrawCardException e) {
            pickableCards.add(Optional.empty());
        }
        for(T c : visibleCards)
            pickableCards.add(Optional.ofNullable(c));
        return pickableCards;
    }

    /**
     * If the specified card is found among the 3 "visible" (top of the deck and the 2 actually visibile), it is picked and returned.
     * The method is thought for cards that can be picked and played in a later moment, hence the specified card must be playable.
     * @param cardPlayable Card to be picked (if present)
     * @return The card picked if present, null otherwise
     * @throws InvalidDrawCardException ???
     */
    public T pickCard(CardPlayableIF cardPlayable) throws InvalidDrawCardException{
        if(!deck.isEmpty() && getDeckTop().equals(cardPlayable)){
            return drawFromDeck();
        }
        for (int i = 0; i < visibleCards.size(); i++) {
            if(cardPlayable.equals(showFromTable(i))) {
                return drawFromTable(i);
            }
        }
        return null;
    }
}
