package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.card.points.PointsPlayable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface CardSidePlayableIF extends Serializable {

    /**
     * @return the resources at the center of the card side
     */
    List<Resource> getCenterResources();

    /**
     * @return the resources needed to play this card side(it's empty if no resource is required)
     */
    Map<Resource, Integer> getRequirements();

    /**
     * @return the color of this card
     */
    Color getColor();

    /**
     * @return Number of corners covered by this card of that other card cover on this one.
     * Fow how the game works, except for the starter card when no other card is added, it will be always <=3
     */
    int calcCornersCovered();

    /**
     * Get the list of resources present in the 4 angles of the card (clockwise order)
     * @return A list of card's corner resources
     */
    ArrayList<Resource> getCornerResources();

    /**
     *
     * @return list of 4 booleans, true if the corresponding corners is covered
     */
    List<Boolean> getCoveredCorners();

    /**
     * Returns the object which describes all the matter related to points
     * @return PointsPlayable interface of the card side
     */
    PointsPlayable getPoints();

    /**
     * Return the list of corners
     * @return list of card corners (in order, they are: upper left, upper right, lower right, lower left)
     */
    List<Corner> getCorners();

    /**
     * Returns the id of the card
     */
    String getId();

    /**
     * @return Side corresponding to this cardSide
     */
    Side getSide();

    /**
     * @return A new instance identical to the object
     */
    CardSidePlayableIF clone();
}
