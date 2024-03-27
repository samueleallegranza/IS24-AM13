package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.card.points.PointsInstant;
import it.polimi.ingsw.am13.model.card.points.PointsPlayable;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a golden car, that can be played in player's field sometime during the game.
 * All information about the card and how it is being played are inherited by <code>CardPlayable</code>
 */
public class CardGold extends CardPlayable {

    /**
     * Creates a new card setting all its immutable attributes
     * The card is created not visible in field
     * @param id Id of the card
     * @param front Front side of the card
     * @param back Back side of the card
     */
    public CardGold(String id, CardSidePlayable front, CardSidePlayable back) {
        super(id, front, back);
    }

    /**
     * Creates a new card setting all immutable attributs. Creates alse the sides
     * @param id Id of the card
     * @param color Color of both sides of the card. Cannot be NO_COLOR
     * @param requirements Map representing the requirements to play the card (resource required -> number of that resource required)
     * @param frontCorners List of the 4 corners eventually with resources of the front side of the card
     * @param points Points (already created) guaranteed by the front side of the card
     * @throws InvalidCardCreationException If the list of corners is not of size 4 or color is NO_COLOR
     */
    public CardGold(String id, Color color, Map<Resource, Integer> requirements, List<Corner> frontCorners,
                    PointsPlayable points) throws InvalidCardCreationException {
        super(id,
                new CardSidePlayable(
                        requirements,
                        frontCorners,
                        new ArrayList<>(),
                        points,
                        color
                ), new CardSidePlayable(
                        new HashMap<>(),
                        Corner.generateEmptyCorners(),
                        List.of(color.correspondingResource()),
                        new PointsInstant(0),
                        color
                )
        );
    }

}
