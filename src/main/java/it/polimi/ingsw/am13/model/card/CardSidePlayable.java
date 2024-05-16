package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.card.points.PointsPlayable;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import it.polimi.ingsw.am13.model.player.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents one of the two sides of a playable card
 */
public class CardSidePlayable implements CardSidePlayableIF {

    //TODO: transient in corners dovrebbe andare bene, ma potrebbe dare problemi per calcCornersCovered...

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
    final transient private PointsPlayable points;

    /**
     * It stores the color of this card
     */
    final private Color color;

    /**
     * This is the constructor of a cardSidePlayable
     * @param requirements the resources needed to play this card side(it's empty if no resource is required)
     * @param corners the 4 corners of the card side(in order, they are: upper left, upper right, lower right, lower left)
     * @param centerResources the resources at the center of the card side(it's empty if there are no resources in the center)
     * @param points the points the player gets when playing this card side(use PointsInstant(0) if the card rewards no points)
     * @param color the color of this card
     * @throws InvalidCardCreationException If list of corners is not of size 4, or requirements or centerResources contain <code>Resource.NO_RESOURCE</code>
     */
    public CardSidePlayable(Map<Resource, Integer> requirements, List<Corner> corners, List<Resource> centerResources,
                            PointsPlayable points, Color color) throws InvalidCardCreationException {
        if(corners.size()!=4)
            throw new InvalidCardCreationException("The card side " + this + " is tried to be created with a list of corners not of size 4");
        if(requirements.containsKey(Resource.NO_RESOURCE) || centerResources.contains(Resource.NO_RESOURCE))
            throw new InvalidCardCreationException("You're trying to create a playable card side with requirements or centerResources containing NO_RESOURCE");
        this.requirements = requirements;
        this.corners = corners;
        this.centerResources = centerResources;
        this.points = points;
        this.color = color;
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
     * @return Number of corners covered by this card of that other card cover on this one.
     */
    @Override
    public int calcCornersCovered() {
        return corners.stream().filter(c -> c.getLink()!=null).toList().size();
    }

    /**
     * Get the list of resources present in the 4 angles of the card (clockwise order)
     * @return A list of card's corner resources
     */
    @Override
    public ArrayList<Resource> getCornerResources() {
        ArrayList<Resource> resourceList = new ArrayList<>(4);
        List<Corner> corners = this.getCorners();
        for(Corner c: corners) resourceList.add(c.getResource());
        return resourceList;
    }

    @Override
    public List<Boolean> getCoveredCorners() {
        List<Boolean> isCovered=new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            isCovered.add(i,corners.get(i).isCovered());
        }
        return isCovered;
    }

    @Override
    public PointsPlayable getPoints() {
        return this.points;
    }

    /**
     *
     * @param field the field of the player
     * @return the points the player gets when playing this card
     */
    public int calcPoints(Field field){
        return points.calcPoints(this,field);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardSidePlayable that = (CardSidePlayable) o;
        return Objects.equals(requirements, that.requirements) && Objects.equals(corners, that.corners) && Objects.equals(centerResources, that.centerResources) && Objects.equals(points, that.points) && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(requirements, corners, centerResources, points, color);
    }

    @Override
    public String toString() {
        return "CardSidePlayable{" +
                "requirements=" + requirements +
                ", corners=" + corners +
                ", centerResources=" + centerResources +
                ", points=" + points +
                ", color=" + color +
                '}';
    }
}
