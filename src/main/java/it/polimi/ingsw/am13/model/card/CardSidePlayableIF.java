package it.polimi.ingsw.am13.model.card;

import java.util.List;
import java.util.Map;

public interface CardSidePlayableIF {

    /**
     * @return the resources at the center of the card side
     */
    List<Resource> getCenterResources();

    /**
     * @return the resources needed to play this card side(it's empty if no resource is required)
     */
    Map<Resource, Integer> getRequirements();

    /**
     * @return the color of this card
     */
    Color getColor();

    /**
     * @return Number of corners covered by this card of that other card cover on this one.
     * Fow how the game works, except for the starter card when no other card is added, it will be always <=3
     */
    int calcCornersCovered();
}
