package it.polimi.ingsw.am13.model.card.points;

import it.polimi.ingsw.am13.model.card.CardSidePlayableIF;
import it.polimi.ingsw.am13.model.card.Resource;
import it.polimi.ingsw.am13.model.player.FieldIF;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * Representation of points of a playable card side of the type
 * "x points for each resource of type y present on the field immediately after you play it"
 */
public class PointsResource implements PointsPlayable, Serializable {
    /**
     * This variable represents the number of points this card gives for each resource
     */
    final private int points;

    /**
     * This variable represents the resource required to get points by playing this card
     */
    final private Resource resource;

    /**
     * The only constructor of the class. The only way to set the parameters of the class is to instantiate the object via this constructor
     * @param points the amount of points this card gives for each resource
     * @param resource the resource required to get points by playing this card
     */
    public PointsResource(int points, Resource resource) {
        this.points = points;
        this.resource = resource;
    }

    /**
     * This method calculates the points given by the card side, according to how many resources of the required type, which is indicated on the card itself, are visible.
     * @param cardSidePlayable side of the playable card that gives these points to the player
     * @param field Field of the player for whom  you want to calculate the points given by the card objective.
     * @return the number of points the player receives after playing this card
     */
    @Override
    public int calcPoints(CardSidePlayableIF cardSidePlayable, FieldIF field) {
        Map<Resource,Integer> freqs=field.getResourcesInField();
        return freqs.get(resource)*points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointsResource that = (PointsResource) o;
        return points == that.points && resource == that.resource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(points, resource);
    }
}
