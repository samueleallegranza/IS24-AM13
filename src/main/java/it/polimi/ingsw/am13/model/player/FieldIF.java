package it.polimi.ingsw.am13.model.player;

import it.polimi.ingsw.am13.model.card.*;

import java.util.List;
import java.util.Map;

/**
 * Interface representing a player's field.
 * It exposes methods to build the actual field and query some possibly useful information for the game about the field.
 */
public interface FieldIF {

    /**
     * @return A list of all coordinated where a card has been already positioned.
     * The list is empty if no cards has been placed yet. For how the game works, the coordinate (0,0), is the list
     * is not empty, must be present
     */
    List<Coordinates> getCoordinatesPlaced();

    /**
     * Checks if the coordinated passed as parameters has a card placed on it
     * @param coord Coordinates to be checked if a card is on it
     * @return True if a card is on the indicated coordinated, false otherwise
     */
    boolean isCoordPlaced(Coordinates coord);

    /**
     * Retrieves the card side of the placed card in the Field at given coordinates.
     * @param coord Coordinates of the card.
     * @return If present, the card side at given coordinates. If not present, null.
     */
    CardSidePlayableIF getCardSideAtCoord(Coordinates coord);

    /**
     * @return A map associating each possible game resources to how many are visible in the present field.
     */
    Map<Resource, Integer> getResourcesInField();

    /**
     * Retrieves the List of coordinates in which new cards can be placed. It returns all and only the coordinates
     * for which cardIsPlaceableAtCoord() returns true for the actual Field situation.
     * @return A list of coordinates in which a card can be placed.
     */
    List<Coordinates> getAvailableCoords();


}
