package it.polimi.ingsw.am13.model.card.points;

import it.polimi.ingsw.am13.model.card.CardSidePlayable;
import it.polimi.ingsw.am13.model.card.Corner;
import it.polimi.ingsw.am13.model.player.Field;

import java.util.List;

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
    public int calcPoints(CardSidePlayable cardSidePlayable,Field field) {
        int cont=0;
        List<Corner> corners=cardSidePlayable.getCorners();
        for(Corner corner : corners)
            if(corner.getLink()!=null)
                cont++;
        return cont*points;
    }
}