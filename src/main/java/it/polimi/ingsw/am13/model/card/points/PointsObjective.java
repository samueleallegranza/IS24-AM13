package it.polimi.ingsw.am13.model.card.points;

import it.polimi.ingsw.am13.model.player.FieldIF;

/**
 * Representation of points of an objective card.
 * It contains information about how to calculate the points, and allows to actually calculate them given a field.
 * Following the game rules, it should be used to calculate points once the turn-phase is finished.
 */
public interface PointsObjective {

    /**
     * Calculate points of the card, according to information present in class that implements this interface.
     * @param field Field of the player for whom  you want to calculate the points given by the card objective.
     * @return Number of points guaranteed by the card objective
     */
    int calcPoints(FieldIF field);
}
