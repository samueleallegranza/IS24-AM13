package it.polimi.ingsw.am13.model.player;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;

import java.io.Serializable;
import java.util.*;

/**
 * Class which represents the status of the game field of a specific player. It keeps track of played cards and visible
 * resources. It performs actions and queries related to the player's field.
 * -> Card positions are referenced with Coordinates (see Coordinates class for a detailed description of the system)
 * -> Resource counters are stored in a Map in the form of (Resource, count) key-value pair
 */
public class Field implements FieldIF, Serializable {
    /**
     * Description of the player's card positions using a sparse matrix representation.
     * They are stored in a Map in the form of (Coordinates, CardSidePlayable) key-value pair.
     */
    private final Map<Coordinates, CardSidePlayable> field;

    /**
     * Resource counters stored in a Map in the form of (Resource, count) key-value pair.
     * Please note that 'NO_RESOURCE' resources are tracked too.
     */
    private final Map<Resource, Integer> resources;

    /**
     * Constructor of the Field. It initializes all resource counters to 0.
     */
    public Field() {
        field = new HashMap<>();
        resources = new HashMap<>();

        // Initialization of resources
        for(Resource res: Resource.values())
            resources.put(res, 0);
    }

    /**
     * @return A list of all coordinated where a card has been already positioned.
     * The list is empty if no cards has been placed yet. For how the game works, the coordinate (0,0), is the list
     * is not empty, must be present
     */
    @Override
    public List<Coordinates> getCoordinatesPlaced() {
        return new ArrayList<>(field.keySet());
    }

    /**
     * Checks if the coordinated passed as parameters has a card placed on it
     * @param coord Coordinates to be checked if a card is on it
     * @return True if a card is on the indicated coordinated, false otherwise
     */
    @Override
    public boolean isCoordPlaced(Coordinates coord) {
        return field.containsKey(coord);
    }

    /**
     * Retrieves the placed card in the Field at given coordinates.
     * @param coords Coordinates of the card.
     * @return If present, the card side at given coordinates. If not present, null.
     */
    @Override
    public CardSidePlayable getCardSideAtCoord(Coordinates coords){
        return field.getOrDefault(coords, null);
    }

    /**
     * Retrieves the root of the field's cards (the starter card in position (0,0)).
     * @return Starter card side placed on the Field.
     * @throws InvalidCoordinatesException If called too early in the game, starter card could not have been placed yet.
     */
    public CardSidePlayable getRoot() throws InvalidCoordinatesException {
        Coordinates origin = new Coordinates(0,0);
        return this.getCardSideAtCoord(origin);
    }

    /**
     * Increase by 1 the counter of the given resource.
     * @param res Resource to be incremented.
     */
    private void addResource(Resource res) {
        this.resources.computeIfPresent(res, (key, val) -> val + 1);
    }

    /**
     * Decrease by 1 the counter of the given resource.
     * @param res Resource to be decremented.
     */
    private void removeResource(Resource res) {
        this.resources.computeIfPresent(res, (key, val) -> val - 1);
    }

    /**
     * Retrieves the number of visible resources in the Field.
     * @param res Resource of which count needs to be retrieved.
     */
    private int getResourceCount(Resource res) {
        return this.resources.get(res);
    }

    /**
     * Checks if a card is placeable in a certain position of the Field applying the rules described in the rulebook.
     * A card can be placed at given coordinates if and only if the following requirements are satisfied:
     *  1) Coordinates are valid (checked in Coordinates class)
     *  2) There's no card placed at given coordinates yet
     *  3) There is at least a card in one of the surrounding coordinates
     *  4) All the existing surrounding corners are placeable
     * @param coords Coordinates to be checked
     * @return true if a new card can be placed in the spot, false otherwise
     */
    private boolean cardIsPlaceableAtCoord(Coordinates coords) {
        // handle special coordinates (0,0) reserved for the starter card, it needs different checks
        if((coords.getPosX() == 0) && (coords.getPosY() == 0) && !this.field.containsKey(coords))
            return true;

        // check if the spot is already occupied by a card
        if(this.field.containsKey(coords))
            return false;

        // calculate surrounding coordinates of the given one
        List<Coordinates> surroundingCoords = coords.fetchNearCoordinates();
        // set the corner index to be checked for every surrounding coordinate
        int[] surroundingCornerIdx = {2, 3, 0, 1}; // clockwise corner checks: 'lr', 'll', 'ul', 'ur'

        // the spot is valid if and only if the following requirements are satisfied:
        // 1) there is at least a card in one of the surrounding spots
        // 2) all the surrounding corners are placeable
        boolean hasAdjacent = false;
        boolean allPlaceable = true;
        CardSidePlayable currCard;
        // perform checks in clockwise order starting from upper left spot
        for(int i=0; i<4; i++) {
            currCard = getCardSideAtCoord(surroundingCoords.get(i));
            // check if the card exists
            if (currCard != null) {
                hasAdjacent = true;
                // check if the card's corner of our interest is placeable
                if (!currCard.getCorners().get(surroundingCornerIdx[i]).isPlaceable()) {
                    allPlaceable = false;
                    break; // no need to iterate further
                }
            }
        }

        // return if the spot is valid by combining the two requirements
        return allPlaceable && hasAdjacent;
    }

    /**
     * Retrieves the List of coordinates in which new cards can be placed. It returns all and only the coordinates
     * for which cardIsPlaceableAtCoord() returns true for the actual Field situation.
     * @return A list of coordinates in which a card can be placed.
     */
    public List<Coordinates> getAvailableCoords() {
        List<Coordinates> surroundingCoords;
        Coordinates targetCoord;
        Set<Coordinates> availableCoordSet = new HashSet<>();

        // loop over coordinates of cards placed on the field
        for(Coordinates currCoord: this.field.keySet()) {

            // calculate surrounding coordinates of the current one
            surroundingCoords = currCoord.fetchNearCoordinates();

            // loop over surrounding coordinates and save the playable ones in a Set
            for(int cornerIdx=0; cornerIdx<4; cornerIdx++) {
                targetCoord = surroundingCoords.get(cornerIdx);
                if(cardIsPlaceableAtCoord(targetCoord)) {
                    availableCoordSet.add(targetCoord);
                }
            }
        }

        // transform the Set into an ArrayList which contains all the playable coordinates and return it
        return new ArrayList<>(availableCoordSet);
    }

    /**
     * Play a card side at given coordinates on the Field. Automatically updates the counters of visible resources.
     * If the card can't be placed because of invalid Coordinates, or startCard is not properly initialized, InvalidPlayCardException is thrown.
     * If the card can't be placed because of requirements not met, RequirementsNotMetException is thrown.
     * @param card The card side to be placed on the Field.
     * @param coords Coordinates where to place the new card.
     * @throws InvalidPlayCardException Positioning error of the card at given coordinates, or startCard is not yet initialized,
     * or first side played as (0,0) is not a side of startCard.
     * @throws RequirementsNotMetException At least one Requirement is not satisfied for the given card.
     */
    public void playCardSide(CardSidePlayable card, Coordinates coords) throws InvalidPlayCardException, RequirementsNotMetException {
        // A move can be done only after startCard has been set, and the first move can be only to play a side of startCard in (0,0)
        if(field.isEmpty() && !coords.hasCoords(0,0))
            throw new InvalidPlayCardException("Trying to play without playing starter card before");

        // check if card is placeable at given coord, if not throw Exception
        if(!cardIsPlaceableAtCoord(coords)) {
            throw new InvalidPlayCardException("Card " + card + " not placeable at coords " + coords);
        }

        // check if every card's requirement is met immediately before placing the new card. If not throw Exception
        Map<Resource, Integer> cardRequirements = card.getRequirements();
        for(Resource currResource: cardRequirements.keySet())
            if(getResourceCount(currResource) < cardRequirements.get(currResource))
                throw new RequirementsNotMetException();

        // remove the resources of covered corners from the set of visible ones and update coverage
        //      calculate surrounding coordinates of the given one
        List<Coordinates> surroundingCoords = coords.fetchNearCoordinates();
        //      set the corner index to be checked for every surrounding coordinate
        int[] surroundingCornerIdx = {2, 3, 0, 1}; // clockwise corner checks: 'lr', 'll', 'ul', 'ur'

        // loop over each corner that will be covered by new card
        Resource coveredResource;
        Corner coveredCorner;
        CardSidePlayable coveredCard;
        for(int i=0; i<4; i++) {
            // decrement by 1 the corner resource from the visible resources set, if there's one
            if(getCardSideAtCoord(surroundingCoords.get(i)) != null) {
                coveredCard = getCardSideAtCoord(surroundingCoords.get(i));
                coveredCorner = getCardSideAtCoord(surroundingCoords.get(i)).getCorners().get(surroundingCornerIdx[i]);

                // decrement covered resource of corner
                coveredResource = coveredCorner.getResource();
                removeResource(coveredResource);

                // update coverage & linkage of corner
                coveredCorner.coverCorner();
                try {
                    coveredCorner.addLinkToCard(card);
                } catch (VariableAlreadySetException e) {
                    throw new InvalidPlayCardException();
                }

                // update linkage of new card
                try {
                    card.getCorners().get(i).addLinkToCard(coveredCard);
                } catch (VariableAlreadySetException e) {
                    throw new InvalidPlayCardException();
                }
            }
        }

        // add card resources to the visible resources set
        for(Corner currCorner: card.getCorners())
            addResource(currCorner.getResource());  // corner resources

        for(Resource centerRes: card.getCenterResources())
            addResource(centerRes);                 // center resources

        // finally, place the card on the field at the given coordinates
        field.put(coords, card);
    }

    /**
     * Getter for visible resource counters of the Field.
     * The resource counters are stored in a Map in the form of (Resource, count) key-value pair.
     * @return Map containing the counters of visible resources
     */
    @Override
    public Map<Resource, Integer> getResourcesInField() {
        return this.resources;
    }
}
