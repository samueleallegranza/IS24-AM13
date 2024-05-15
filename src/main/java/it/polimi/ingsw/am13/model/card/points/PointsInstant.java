package it.polimi.ingsw.am13.model.card.points;

import it.polimi.ingsw.am13.model.card.CardSidePlayableIF;
import it.polimi.ingsw.am13.model.card.Resource;
import it.polimi.ingsw.am13.model.player.FieldIF;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representation of points of a playable card side of the type "x points when you play this card on this side"
 */
public class PointsInstant implements PointsPlayable, Serializable {
    /**
     * This variable represents how many points the card gives
     */
    private final int points;

    /**
     * The only constructor of the class. The only way to set the parameters of the class is to instantiate the object via this constructor
     * @param points how many points the card gives
     */
    public PointsInstant(int points) {
        this.points = points;
    }

    /**
     * This method returns the points given by the card side
     * @param cardSide side of the playable card that gives these points to the player
     * @param field Field of the player for whom  you want to calculate the points given by the card objective.
     * @return how many points the card gives
     */
    @Override
    public int calcPoints(CardSidePlayableIF cardSide, FieldIF field) {
        return points;
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
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointsInstant that = (PointsInstant) o;
        return points == that.points;
    }

    @Override
    public int hashCode() {
        return Objects.hash(points);
    }
}
