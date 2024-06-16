package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.card.points.PointsInstant;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import it.polimi.ingsw.am13.model.player.Field;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestCardSidePlayable {
    /**
     * This test verifies that the CardSide can be constructed correctly, and correctly calculates the points
     */
    @Test
    public void testCalcPoints() throws InvalidCardCreationException {
        int npoints=3;
        Map<Resource,Integer> requirements=new HashMap<>();
        List<Corner> corners = Corner.generateEmptyCorners();
        List<Resource> centerResources=new ArrayList<>();
        PointsInstant points=new PointsInstant(npoints);
        CardSidePlayable starterFront=new CardSidePlayable(requirements,corners,centerResources,points, Color.NO_COLOR,
                "",
                Side.SIDEFRONT);
        Field field=new Field();

        assertEquals(Side.SIDEFRONT,starterFront.getSide());
        assertEquals("",starterFront.getId());
        assertEquals(points,starterFront.getPoints());
        List<Boolean> coveredCorners=starterFront.getCoveredCorners();
        for(Boolean isCovered : coveredCorners)
            assertFalse(isCovered);
        assertEquals(npoints,starterFront.calcPoints(field));
    }
}
