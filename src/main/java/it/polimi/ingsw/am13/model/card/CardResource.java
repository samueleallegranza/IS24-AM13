package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.card.points.PointsInstant;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a resource car, that can be played in player's field sometime during the game.
 * All information about the card and how it is being played are inherited by <code>CardPlayable</code>
 */
public class CardResource extends CardPlayable {

    /**
     * Creates a new card setting all its immutable attributes
     * The card is created not visible in field
     * @param id Id of the card
     * @param front Front side of the card
     * @param back Back side of the card
     */
    public CardResource(String id, CardSidePlayable front, CardSidePlayable back) {
        super(id, front, back);
    }

    /**
     * Creates a new card setting all immutable attributs. Creates alse the sides
     * @param id Id of the card
     * @param color Color of both sides of the card. Cannot be NO_COLOR
     * @param frontCorners List of the 4 corners eventually with resources of the front side of the card
     * @param points Number of points guaranteed by the front side of the card
     * @throws InvalidCardCreationException If the list of corners is not of size 4 or color is NO_COLOR
     */
    public CardResource(String id, Color color, List<Corner> frontCorners, int points) throws InvalidCardCreationException {
        super(id,
                new CardSidePlayable(
                        new HashMap<>(),
                        frontCorners,
                        new ArrayList<>(),
                        new PointsInstant(points),
                        color,
                        id,
                        Side.SIDEFRONT
                ), new CardSidePlayable(
                        new HashMap<>(),
                        Corner.generateEmptyCorners(),
                        List.of(color.correspondingResource()),
                        new PointsInstant(0),
                        color,
                        id,
                        Side.SIDEBACK
                )
        );
        if(color.correspondingResource() == Resource.NO_RESOURCE)
            throw new InvalidCardCreationException("The color is not valid");
    }
}
