package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.card.points.PointsPlayable;

import java.util.List;
import java.util.Map;

/**
 * This class represents one of the two sides of a playable card
 */
public class CardSidePlayable extends CardSide{
    /**
     * It stores the resources needed to play this card side(it's empty if no resource is required)
     */
    final private Map<Resource,Integer> requirements;

    /**
     * It stores the 4 corners of the card side
     * In order, they are: upper left, upper right, lower right, lower left
     */
    final private List<Corner> corners;
    /**
     * This possibly empty list contains the resources at the center of the card side
     */
    final private List<Resource> centerResources;
    /**
     * It stores the information needed to calculate the points the player gets when playing this card side
     */
    final private PointsPlayable points;
    /**
     * It stores the color of this card
     */
    final private Color color;

    /**
     * This is the constructor of a cardSidePlayable
     * @param requirements the resources needed to play this card side(it's empty if no resource is required)
     * @param corners the 4 corners of the card side(in order, they are: upper left, upper right, lower right, lower left)
     * @param centerResources the resources at the center of the card side
     * @param points the points the player gets when playing this card side
     * @param color the color of this card
     */
    public CardSidePlayable(Map<Resource, Integer> requirements, List<Corner> corners, List<Resource> centerResources, PointsPlayable points, Color color) {
        this.requirements = requirements;
        this.corners = corners;
        this.centerResources = centerResources;
        this.points = points;
        this.color=color;
    }

    /**
     * @return the resources at the center of the card side
     */
    public List<Resource> getCenterResources() {
        return centerResources;
    }

    /**
     * @return the 4 corners of the card side(in order, they are: upper left, upper right, lower right, lower left)
     */
    public List<Corner> getCorners() {
        return corners;
    }

    /**
     * @return the resources needed to play this card side(it's empty if no resource is required)
     */
    public Map<Resource, Integer> getRequirements() {
        return requirements;
    }

    /**
     * @return the color of this card
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return the points the player gets when playing this card
     */
    public int calcPoints(){
        return points.calcPoints(this);
    }

}
