package it.polimi.ingsw.am13.model.card;

import java.util.Objects;

/**
 * Represents a generic card of the game.
 * It is identified by a unique id (immutable attribute), and stores other information about the card during the game.
 * The card is present in field if all players can see it. Hence, it could be in one player's field or if it is in one of the 9 spots of the commond field.
 * If the card is in field, <code>visibleSide</code> specifies what side is visible (front or back)
 */
public abstract class Card implements CardIF {

    //TODO: specifica struttura che usiamo per id della carta (idem su costruttore)
    /**
     * Unique id of the card.
     */
    private final String id;

    /**
     * If the card is in the field, this specifies what side is visible.
     * If the card is not in the field, the attribute is null
     */
    private Side visibleSide;

    /**
     * Set the specified id while creating a new card
     * The card is created not visible in the field
     * @param id Id of the card
     */
    Card(String id) {
        this.id = id;
        visibleSide = null;
    }

    /**
     * @return Id of the card
     */
    public String getId() {
        return id;
    }

    /**
     * @return The visible side of the card. If the card is not in field, returns <code>null</code>.
     */
    public Side getVisibleSide() {
        return visibleSide;
    }

    /**
     * Sets the card visible in field, with the visible side specified.
     * @param visibleSide Visible side of the card in the field
     * @throws NullPointerException if the side specified is null
     */
    public void placeCardInField(Side visibleSide) throws NullPointerException {
        if(visibleSide == null)
            throw new NullPointerException("Cannot set card " + this + " visible in field with null visibleSide");
        this.visibleSide = visibleSide;
    }

    /**
     * Removes card from field
     */
    public void removeCardFromField() {
        visibleSide = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Card{" +
                "id='" + id + '\'' +
                ", visibleSide=" + visibleSide +
                '}';
    }
}
