package it.polimi.ingsw.am13.model.card.points;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import it.polimi.ingsw.am13.model.exceptions.VariableAlreadySetException;
import it.polimi.ingsw.am13.model.player.Field;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPointsCorner {
    /**
     * This test verifies that the constructor correctly instantiates PointsIstant, and that calcPoints returns the correct value
     * In particular, it checks that by adding two links, the calculated value is 2*points
     */
    @Test
    public void testSetGet() throws InvalidCardCreationException {
        int points=2;
        Map<Resource,Integer> req=new HashMap<>();
        List<Corner> corners= Arrays.asList(new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.PLANT));
        List<Resource> centerResources=new ArrayList<>();
        PointsInstant cardPoints=new PointsInstant(3);
        PointsCorner pointsCorner=new PointsCorner(points);
        assertEquals(points,pointsCorner.getPointsMultiplier());
        assertEquals(Resource.NO_RESOURCE,pointsCorner.getPointsResource());
        assertTrue(pointsCorner.isCornerTypePoints());
        PointsCorner pointsCornerCopy=new PointsCorner(points);
        assertEquals(pointsCorner, pointsCornerCopy);
        CardSidePlayable cardSide=new CardSidePlayable(req,corners,centerResources,cardPoints, Color.NO_COLOR,
                "",
                Side.SIDEFRONT);
        Field field=new Field();
        assertEquals(0,pointsCorner.calcPoints(cardSide,field));
        try {
            corners.get(0).addLinkToCard(cardSide);
            corners.get(1).addLinkToCard(cardSide);
            assertEquals(2*points,pointsCorner.calcPoints(cardSide,field));
        }
        catch (VariableAlreadySetException e){
            System.out.println("The variable was already set before");
        }
    }
}
