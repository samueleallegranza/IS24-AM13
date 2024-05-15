package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import it.polimi.ingsw.am13.model.player.Field;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an objective card.
 * It stores only the active/front side (the back side has no information). It's then the side that store information
 * about the points guaranted by the card itself.
 */
public class CardObjective extends Card implements CardObjectiveIF {

    /**
     * Active/front side of the card, where information about the points is stored.
     */
    private transient final CardSideObjectiveActive front;

    /**
     * Creates a new immutable objective card, with points of type "x points for each set of resources/objects"
     * @param id Id of the card. Look at <code>class Card</code> for more information
     * @param points How many points the card gives for each complete set present in field
     * @param set The set of resources/objects that must be present to obtain the points
     */
    public CardObjective(String id, int points, Map<Resource, Integer> set) {
        super(id);
        front = new CardSideObjectiveActive(points, set);
    }

    /**
     * Creates a new immutable card side for an objective card of type "x points for each pattern with 3 card in a
     * certain position and with certain colors"
     * @param id Id of the card. Look at <code>class Card</code> for more information
     * @param points How many points the card gives for each complete non-intersecting pattern is present in field
     * @param color1 Color of upper card
     * @param color2 Color of middle card
     * @param color3 Color of bottom card
     * @param pos12 Position of middle card with respect to upper card. It can only be -1 (to the left), 0 (right under), 1 (to the right)
     * @param pos23 Position of bottom card with respect to middle card. It can only be -1 (to the left), 0 (right under), 1 (to the right)
     * @throws InvalidParameterException If pos12 or pos23 are not among their possible values
     */
    public CardObjective(String id, int points, Color color1, Color color2, Color color3, int pos12, int pos23)
            throws InvalidCardCreationException {
        super(id);
        front = new CardSideObjectiveActive(points, color1, color2, color3, pos12, pos23);
    }

    /**
     * Calculates the points guaranteed by the card for the field passed as parameter
     * @param field Field of the player for whom you want to calculate the points given by the card.
     * @return Number of points guaranteed by the card objective
     */
    public int calcPoints(Field field) {
        return front.calcPoints(field);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CardObjective that = (CardObjective) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }
}
