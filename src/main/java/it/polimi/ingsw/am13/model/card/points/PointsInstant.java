package it.polimi.ingsw.am13.model.card.points;

import it.polimi.ingsw.am13.model.card.CardSidePlayable;
import it.polimi.ingsw.am13.model.player.Field;

/**
 * Representation of points of a playable card side of the type "x points when you play this card on this side"
 */
public class PointsInstant implements PointsPlayable {
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
    public int calcPoints(CardSidePlayable cardSide, Field field) {
        return points;
    }
}
