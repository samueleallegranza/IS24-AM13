package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.card.points.PointsInstant;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCardSidePlayable {
    /**
     * This test verifies that the CardSide can be constructed correctly, and correctly calculates the points
     */
    @Test
    public void testCalcPoints(){
        int npoints=3;
        Map<Resource,Integer> requirements=new HashMap<>();
        List<Corner> corners=new ArrayList<>();
        List<Resource> centerResources=new ArrayList<>();
        PointsInstant points=new PointsInstant(npoints);
        CardSidePlayable starterFront=new CardSidePlayable(requirements,corners,centerResources,points, Color.NO_COLOR);
        assertEquals(npoints,starterFront.calcPoints());
    }
}
