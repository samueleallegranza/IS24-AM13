package it.polimi.ingsw.am13.model.card.points;

import it.polimi.ingsw.am13.model.card.CardSidePlayableIF;
import it.polimi.ingsw.am13.model.card.Resource;
import it.polimi.ingsw.am13.model.player.FieldIF;

import java.io.Serializable;

/**
 * Representation of the points of a playable card.
 * It contains information about how to calculate the points, and allows to actually calculate them given a field and a card side playable.
 * Following the game rules, it should be used to calculate points when a player plays a card side.
 */
public interface PointsPlayable extends Serializable {
    /**
     * Calculate points of the card, according to information present in class that implements this interface.
     * @param cardSidePlayable side of the playable card that gives these points to the player
     * @param field Field of the player for whom  you want to calculate the points given by the card objective.
     * @return number of points the player gets after playing the card side
     */
    int calcPoints(CardSidePlayableIF cardSidePlayable, FieldIF field);

    /**
     * Return the points multiplier. In other words, the number of points which appears on the card.
     * @return Points multiplier
     */
    int getPointsMultiplier();

    /**
     * Return the required resource to get points by playing the card.
     * @return Resource required
     */
    Resource getPointsResource();

    /**
     * Returns true if the number of points is conditioned to the number of angles the card covers.
     * @return true if PointsCorner, false otherwise
     */
    boolean isCornerTypePoints();
}