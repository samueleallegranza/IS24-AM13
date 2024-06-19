package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.card.points.PointsPlayable;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import it.polimi.ingsw.am13.model.player.Field;

import java.util.*;

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
    final private PointsPlayable points;

    /**
     * It stores the color of this card
     */
    final private Color color;

    /**
     * The id of this card
     */
    final private String id;

    /**
     * The side of the card this CardSide corresponds to
     */
    final private Side side;

    public Side getSide() {
        return side;
    }
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
                            PointsPlayable points, Color color, String id, Side side) throws InvalidCardCreationException {
        if(corners.size()!=4)
            throw new InvalidCardCreationException("The card side " + this + " is tried to be created with a list of corners not of size 4");
        if(requirements.containsKey(Resource.NO_RESOURCE) || centerResources.contains(Resource.NO_RESOURCE))
            throw new InvalidCardCreationException("You're trying to create a playable card side with requirements or centerResources containing NO_RESOURCE");
        this.requirements = requirements;
        this.corners = corners;
        this.centerResources = centerResources;
        this.points = points;
        this.color = color;
        this.id=id;
        this.side=side;
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

    /**
     *
     * @return list of 4 booleans, true if the corresponding corners is covered
     */
    @Override
    public List<Boolean> getCoveredCorners() {
        List<Boolean> isCovered=new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            isCovered.add(i,corners.get(i).isCovered());
        }
        return isCovered;
    }

    /**
     * Returns the object which describes all the information related to points
     * @return PointsPlayable interface of the card side
     */
    @Override
    public PointsPlayable getPoints() {
        return this.points;
    }
//
//    @Override
//    public List<boolean> getLinkableCorners() {
//        this.getCorners().
//    }

    /**
     *
     * @param field the field of the player
     * @return the points the player gets when playing this card
     */
    public int calcPoints(Field field){
        return points.calcPoints(this,field);
    }

    @Override
    public CardSidePlayableIF clone() {
        try {
            return new CardSidePlayable(
                    new HashMap<>(requirements),
                    corners.stream().map(Corner::clone).toList(),
                    new ArrayList<>(centerResources),
                    points,     // points should be immutable, it sholdn't need a 'clone' method
                    color,
                    id,
                    side
            );
        } catch (InvalidCardCreationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardSidePlayable that = (CardSidePlayable) o;
        return Objects.equals(id, that.id) && side == that.side;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, side);
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

    public String getId() {
        return id;
    }
}
