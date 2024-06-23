package it.polimi.ingsw.am13.client.gamestate;

import it.polimi.ingsw.am13.model.card.CardSidePlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Corner;
import it.polimi.ingsw.am13.model.card.Resource;
import it.polimi.ingsw.am13.model.player.FieldIF;

import java.io.Serializable;
import java.util.*;

/**
 * Representation of one player's field.
 * It stores information about the placed cards, and the available coordinates where to place new cards
 */
public class FieldState implements Serializable {

    /**
     * Map between coordinates and cards placed on them
     * Once placed, a card cannot be removed
     */
    private final Map<Coordinates, CardSidePlayableIF> field;

    /**
     * List of available coordinates where to place other cards during the game
     */
    private List<Coordinates> availableCoords;

    /**
     * Creates a new representation of a player's field starting from the interface of that field itself
     * @param field Field to build the representation of
     */
    public FieldState(FieldIF field) {
        this.field = new LinkedHashMap<>();
        for(Coordinates c : field.getCoordinatesPlaced())
            this.field.put(c, field.getCardSideAtCoord(c).clone());
        this.availableCoords = new ArrayList<>(field.getAvailableCoords());
    }

    /**
     * @return List of coordinates where there is a card placed.
     * The list is ordered from the first placed coordinates to the latest ones
     */
    public List<Coordinates> getPlacedCoords() {
        return new ArrayList<>(field.keySet());
    }

    /**
     * @param coord Coordinates of the placed card to retrieve
     * @return Card side of the card placed on the given coordinates, or null if no card is placed on those coordinates
     */
    public CardSidePlayableIF getCardSideAtCoord(Coordinates coord) {
        return field.get(coord);
    }

    /**
     * Sets the given coordinates with the given card side placed on them
     * @param coord Coordinates where the card is placed
     * @param cardSide Card side placed on the given coordinates
     */
    void placeCardSideAtCoord(Coordinates coord, CardSidePlayableIF cardSide) {
        field.put(coord, cardSide);
        for(Coordinates c : coord.fetchNearCoordinates()) {
            CardSidePlayableIF cs = field.get(c);
            if(cs != null) {
                Corner corner;
                if(c.getPosX()<coord.getPosX() && c.getPosY()>coord.getPosY())
                    corner = cs.getCorners().get(2);
                else if(c.getPosX()>coord.getPosX() && c.getPosY()>coord.getPosY())
                    corner = cs.getCorners().get(3);
                else if(c.getPosX()>coord.getPosX() && c.getPosY()<coord.getPosY())
                    corner = cs.getCorners().get(0);
                else
                    corner = cs.getCorners().get(1);
                corner.coverCorner();
            }
        }
    }

    /**
     * @return List of available coordinates where to place other cards during the game
     */
    public List<Coordinates> getAvailableCoords() {
        return availableCoords;
    }

    /**
     * Sets the list of available coordinates where to place other cards during the game.
     * (It does not create another list)
     * @param availableCoords New list of available coordinates where to place other cards during the game
     */
    void setAvailableCoords(List<Coordinates> availableCoords) {
        this.availableCoords = availableCoords;
    }

    public Map<Resource, Integer> getResourcesInField() {
        Map<Resource, Integer> resources = new HashMap<>();
        for(Resource r : Resource.values())
            if(r != Resource.NO_RESOURCE)
                resources.put(r, 0);

        for(CardSidePlayableIF card : field.values()) {
            for(Resource r : card.getCenterResources())
                resources.replace(r, resources.get(r)+1);
            for(Corner c : card.getCorners())
                if(c.getResource()!=Resource.NO_RESOURCE && !c.isCovered()) {
//                    System.out.println(card.getId()+" "+c.getResource());
                    resources.replace(c.getResource(), resources.get(c.getResource()) + 1);
                }
        }
        return resources;
    }
}
