package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.card.points.PointsObjective;

/**
 * Represents an objective card.
 * It can return the card's id and its visible side (if it's visible in someone's field of in the commond field)
 */
public interface CardObjectiveIF extends CardIF {

    PointsObjective getPoints();

}
