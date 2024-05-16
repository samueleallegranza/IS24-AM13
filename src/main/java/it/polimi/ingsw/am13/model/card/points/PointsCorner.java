package it.polimi.ingsw.am13.model.card.points;

import it.polimi.ingsw.am13.model.card.CardSidePlayableIF;
import it.polimi.ingsw.am13.model.card.Resource;
import it.polimi.ingsw.am13.model.player.FieldIF;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representation of points of a playable card side of the type "x points for each corner covered by this card when you play it"
 */
public class PointsCorner implements PointsPlayable {

    /**
     * Represents how many points the card gives for each corner it covers
     */
    private final int points;

    /**
     * The only constructor of the class. The only way to set the parameters of the class is to instantiate the object via this constructor
     * @param points how many points the card gives for each corner it covers
     */
    public PointsCorner(int points) {
        this.points = points;
    }

    /**
     * This method calculates the points given by the card side, according to how many corners it covers
     * @param cardSidePlayable side of the playable card that gives these points to the player
     * @param field Field of the player for whom  you want to calculate the points given by the card objective.
     * @return the number of points the player receives after playing this card
     */
    @Override
    public int calcPoints(CardSidePlayableIF cardSidePlayable, FieldIF field) {
        return cardSidePlayable.calcCornersCovered()*points;
    }

    @Override
    public int getPointsMultiplier() {
        return this.points;
    }

    @Override
    public Resource getPointsResource() {
        return Resource.NO_RESOURCE;
    }

    @Override
    public boolean isCornerTypePoints() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointsCorner that = (PointsCorner) o;
        return points == that.points;
    }

    @Override
    public int hashCode() {
        return Objects.hash(points);
    }
}