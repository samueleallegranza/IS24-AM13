package it.polimi.ingsw.am13.model.card.points;

import it.polimi.ingsw.am13.model.card.Color;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import it.polimi.ingsw.am13.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.am13.model.player.FieldIF;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.*;

/**
 * Representation of an objective card of type "x points for each pattern with 3 card in a certain position and with certain colors"
 * (fourth type of objective card presented in rulebook).
 * Note that, according to rules, the patterns cannot intersect, and the points are calculating with the maximum number of disjunctive patterns found.
 * The pattern is represented starting from the upper card by using two vectors that can be (-1,-1), (0,-1), (1,-1).
 * The vectors represent where the card right below the previous one is: in order, the next card is to the left / below / to the right of the previous card.
 * An object instantiated from this class is immutable.
 */
public class PointsPattern implements PointsObjective {

    /**
     * Color of the upper card
     */
    private final Color color1;
    /**
     * Color of the bottom card
     */
    private final Color color3;
    /**
     * Color of the middle card
     */
    private final Color color2;
    /**
     * Vector representing position of middle card with respect to upper card
     * Mathmatically vec12 = (coordinates of middle card) - (coordinates of upper card)
     * It can be only one among (-1,-1), (0,-1), (1,-1)
     */
    private final Coordinates vec12;
    /**
     * Vector representing position of bottom card with respect to upper card
     * Mathematically vec13 = (coordinates of bottom card) - (coordinates of upper card)
     * It can be only one among (-2,-2), (-1,-2), (1,-2), (2,-2)
     */
    private final Coordinates vec13;
    /**
     * Represetns how many points the card gives for each complete non-intersecting pattern is present in field
     */
    private final int points;

    /**
     *
     * @param color1 Color of upper card
     * @param color2 Color of middle card
     * @param color3 Color of bottom card
     * @param pos12 Position of middle card with respect to upper card. It can only be -1 (to the left), 0 (right under), 1 (to the right)
     * @param pos23 Position of bottom card with respect to middle card. It can only be -1 (to the left), 0 (right under), 1 (to the right)
     * @param points How many points the card gives for each complete non-intersecting pattern is present in field
     * @throws InvalidParameterException If pos12 or pos23 are not among their possible values
     */
    public PointsPattern(Color color1, Color color2, Color color3, int pos12, int pos23, int points) throws InvalidCardCreationException {
        if(pos12!=-1 && pos12!=0 && pos12!=1)
            throw new InvalidCardCreationException("Invalid parameter pos12 (" + pos12 + ") for object points " + this);
        if(pos23!=-1 && pos23!=0 && pos23!=1)
            throw new InvalidCardCreationException("Invalid parameter pos12 (" + pos12 + ") for object points " + this);

        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        try {
            vec12 = new Coordinates(pos12, (pos12==0)?-2:-1);
            vec13 = vec12.add(pos23, (pos23==0)?-2:-1);
        } catch (InvalidCoordinatesException e) {
            // Should never happen
            throw new RuntimeException(e);
        }
        this.points = points;
    }

    public Color getColor1() {
        return color1;
    }

    public Color getColor3() {
        return color3;
    }

    public Color getColor2() {
        return color2;
    }

    public Coordinates getVec12() {
        return vec12;
    }

    public Coordinates getVec13() {
        return vec13;
    }

    /**
     * Calculate points of the card, according to how many (at most) non-intersercint patterns with right colors are found.
     * @param field Field of the player for whom you want to calculate the points given by the card objective.
     * @return Number of points guaranteed by the card objective
     */
    @Override
    public int calcPoints(FieldIF field) {
        int count = 0;

        // I search through all coordinates the ones of first card of a patter of this type
        // I then sort these coordinates by largest x, and collect them as a list
        List<Coordinates> firstPossiblePos = field.getCoordinatesPlaced().stream()
                .filter(x -> field.getCardSideAtCoord(x).getColor()==color1)
                .filter(x -> {
                    Coordinates coord2 = x.add(vec12);
                    return field.isCoordPlaced(coord2) && field.getCardSideAtCoord(coord2).getColor()==color2;
                }).filter(x -> {
                    Coordinates coord3 = x.add(vec13);
                    return field.isCoordPlaced(coord3) && field.getCardSideAtCoord(coord3).getColor()==color3;
                }).sorted( (c1, c2) -> Integer.compare(c2.getPosY(), c1.getPosY())).toList();

        //Now I have all possible patterns, but they can still intersect
        //I start from the pattern in the highest part of field, and validate a pattern only if its 3 cards don't intersect a patter above it
        //The list is sorted by decreasing height of patterns, hence I only check if a patter intersects a coordinate already appeared before
        Set<Coordinates> alreadyUsed = new HashSet<>();
        for(Coordinates coord1 : firstPossiblePos) {
            Coordinates coord2 = coord1.add(vec12);
            Coordinates coord3 = coord1.add(vec13);
            if(!alreadyUsed.contains(coord2) && !alreadyUsed.contains(coord3)) {
                count++;
                alreadyUsed.add(coord1);
                alreadyUsed.add(coord2);
                alreadyUsed.add(coord3);
            }
        }

        return count * points;
    }

    /**
     * @return Points multiplier of the objective card (how many points are given for each pattern/set satisfied in the field)
     */
    @Override
    public int getPointsMultiplier() {
        return points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointsPattern that = (PointsPattern) o;
        return points == that.points && color1 == that.color1 && color3 == that.color3 && color2 == that.color2 && Objects.equals(vec12, that.vec12) && Objects.equals(vec13, that.vec13);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color1, color3, color2, vec12, vec13, points);
    }
}
