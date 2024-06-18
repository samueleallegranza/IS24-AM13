package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.card.points.PointsObjective;
import it.polimi.ingsw.am13.model.card.points.PointsPattern;
import it.polimi.ingsw.am13.model.card.points.PointsSet;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import it.polimi.ingsw.am13.model.player.Field;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the active side (front) of an objective card.
 * It stores the information about the points guaranteed by the card.
 */
public class CardSideObjectiveActive implements Serializable {

    /**
     * Information about the points guaranteed by the card this side is part of.
     */
    private final PointsObjective points;

    /**
     * Creates a new immutable card side for an objective card of type "x points for each set of resources/objects"
     * @param points How many points the card gives for each complete set present in field
     * @param set The set of resources/objects that must be present to obtain the points
     */
    public CardSideObjectiveActive(int points, Map<Resource, Integer> set) {
        this.points = new PointsSet(set, points);
    }

    /**
     * Creates a new immutable card side for an objective card of type "x points for each pattern with 3 card in a
     * certain position and with certain colors"
     * @param points How many points the card gives for each complete non-intersecting pattern is present in field
     * @param color1 Color of upper card
     * @param color2 Color of middle card
     * @param color3 Color of bottom card
     * @param pos12 Position of middle card with respect to upper card. It can only be -1 (to the left), 0 (right under), 1 (to the right)
     * @param pos23 Position of bottom card with respect to middle card. It can only be -1 (to the left), 0 (right under), 1 (to the right)
     * @throws InvalidCardCreationException If pos12 or pos23 are not among their possible values
     */
    public CardSideObjectiveActive(int points, Color color1, Color color2, Color color3, int pos12, int pos23)
            throws InvalidCardCreationException {
        this.points = new PointsPattern(color1, color2, color3, pos12, pos23, points);
    }

    public PointsObjective getPoints() {
        return points;
    }

    /**
     * Calculates the points guaranteed by the card for the field passed as parameter
     * @param field Field of the player for whom you want to calculate the points given by the card.
     * @return Number of points guaranteed by the card objective
     */
    public int calcPoints(Field field) {
        return points.calcPoints(field);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardSideObjectiveActive that = (CardSideObjectiveActive) o;
        return Objects.equals(points, that.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(points);
    }
}
