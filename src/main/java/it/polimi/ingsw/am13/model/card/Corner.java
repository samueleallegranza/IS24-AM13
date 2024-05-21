package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.exceptions.VariableAlreadySetException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This is a class that represents one of the four corners of the side of a card
 */
public class Corner implements Serializable {
    /**
     * This attribute represents whether the corner is visible or not
     */
    private final boolean visibility;

    /**
     * This attribute represents whether the corner has been covered by another corner or not
     */
    private boolean covered;
    /**
     * This attribute represents the resource contained in this corner
     */
    private final Resource resource;
    /**
     * This attribute represents the card side that is played on or under this corner
     */
    private CardSidePlayable link;

    /**
     * This is a constructor of a visible corner
     * @param resource Resource present on the corner.
     *                 If the corner is visible but has no resource, use <code>Resource.NO_RESOURCE</code>
     */
    public Corner(Resource resource){
        this.visibility=true;
        this.resource=resource;
        covered=false;
        link=null;
    }

    /**
     * This is a constructor of a corner that isn't visible.
     * A non-visible corner can only contain NO_RESOURCE as a resource
     */
    public Corner(){
        this.visibility=false;
        this.resource=Resource.NO_RESOURCE;
        covered=false;
        link=null;
    }

    /**
     * @return the resource contained in this corner
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * @return the link contained in this corner
     */
    public CardSidePlayable getLink() {
        return link;
    }

    /**
     * @return whether the corner has been covered by another corner or not
     */
    public boolean isCovered() {
        return covered;
    }

    /**
     * This method covers the Corner by setting the covered variable to true
     */
    public void coverCorner(){
        covered=true;
    }

    /**
     * @return whether another corner can be played on top of this corner
     */
    public boolean isPlaceable(){
        return !covered && visibility;
    }

    /**
     * This method creates a link from this corner to a cardSidePlayable
     * @param cardSidePlayable the cardSidePlayable to which the link will be created
     * @throws VariableAlreadySetException when the link isn't null, ie it was already set outside the constructor before
     */
    public void addLinkToCard(CardSidePlayable cardSidePlayable) throws VariableAlreadySetException {
        if(link!=null) throw new VariableAlreadySetException();
        link=cardSidePlayable;
    }

    /**
     * Utility method useful for tests. Generates a list of four visible empty corners (as in back side of resource cards)
     * @return New list of four visible empty corners (as in back side of resource cards)
     */
    public static List<Corner> generateEmptyCorners() {
        return Arrays.asList(new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE),
                new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Corner corner = (Corner) o;
        return visibility == corner.visibility && resource == corner.resource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(visibility, resource);
    }
}
