package it.polimi.ingsw.am13.model.card.points;

import it.polimi.ingsw.am13.model.card.CardSidePlayable;
import it.polimi.ingsw.am13.model.player.Field;
/**
 * Representation of the points of a playable card.
 * It contains information about how to calculate the points, and allows to actually calculate them given a field and a card side playable.
 * Following the game rules, it should be used to calculate points when a player plays a card side.
 */
public interface PointsPlayable{
    /**
     * Calculate points of the card, according to information present in class that implements this interface.
     * @param cardSidePlayable side of the playable card that gives these points to the player
     * @param field Field of the player for whom  you want to calculate the points given by the card objective.
     * @return number of points the player gets after playing the card side
     */
    int calcPoints(CardSidePlayable cardSidePlayable, Field field);
}