package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.exceptions.VariableAlreadySetException;

/**
 * This is a class that represents one of the four corners of the side of a card
 */
public class Corner {
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
     *This is a constructor of a visible corner
     * @param resource
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

}
