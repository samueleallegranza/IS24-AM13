package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.card.points.PointsInstant;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a starter car, that can be played in player's field sometime during the game.
 * All information about the card and how it is being played are inherited by <code>CardPlayable</code>
 */
public class CardStarter extends CardPlayable implements CardStarterIF {

    /**
     * Creates a new card setting all immutable attributes of card itself. Sides already exists
     * The card is created not visible in field
     * @param id Id of the card
     * @param front Front side of the card
     * @param back Back side of the card
     */
    public CardStarter(String id, CardSidePlayable front, CardSidePlayable back) {
        super(id, front, back);
    }

    /**
     * Creates a new card setting all immutable attributs. Creates alse the sides
     * @param id Id of the card
     * @param frontCorners List of corners of the front side
     * @param frontCenterRes List of central resources of the front side (the only side where there are central resources)
     * @param backCorners List of corners of the back side
     */
    public CardStarter(String id, List<Corner> frontCorners, List<Resource> frontCenterRes,
                       List<Corner> backCorners) throws InvalidCardCreationException {
        this(id,
                new CardSidePlayable(
                        new HashMap<>(),
                        frontCorners,
                        frontCenterRes,
                        new PointsInstant(0),
                        Color.NO_COLOR
                ),
                new CardSidePlayable(
                        new HashMap<>(),
                        backCorners,
                        new ArrayList<>(),
                        new PointsInstant(0),
                        Color.NO_COLOR
                ));
    }

}
