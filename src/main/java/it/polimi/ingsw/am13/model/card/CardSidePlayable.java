package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.card.points.PointsPlayable;

import java.util.List;
import java.util.Map;

public class CardSidePlayable extends CardSide{
    private Map<Resource,Integer> requirements;
    private List<Corner> corners;
    private List<Resource> centerResources;
    private PointsPlayable points;

    public List<Corner> getCorners() {
        return corners;
    }
}
