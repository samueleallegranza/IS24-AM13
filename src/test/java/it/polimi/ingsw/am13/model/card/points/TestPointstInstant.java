package it.polimi.ingsw.am13.model.card.points;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import it.polimi.ingsw.am13.model.player.Field;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestPointstInstant {
    /**
     * This test verifies that the constructor correctly instantiates PointsIstant, and that calcPoints returns the correct value
     */
    @Test
    public void testSetGet() throws InvalidCardCreationException {
        int points=5;
        Map<Resource,Integer> req=new HashMap<>();
        List<Corner> corners = Corner.generateEmptyCorners();
        List<Resource> centerResources=new ArrayList<>();
        PointsInstant cardPoints=new PointsInstant(points);
        CardSidePlayable cardSide=new CardSidePlayable(req,corners,centerResources,cardPoints, Color.NO_COLOR,
                "",
                Side.SIDEFRONT);
        Field field=new Field();
        assertEquals(points,cardPoints.getPointsMultiplier());
        assertEquals(Resource.NO_RESOURCE,cardPoints.getPointsResource());
        assertFalse(cardPoints.isCornerTypePoints());
        assertEquals(new PointsInstant(points),cardPoints);
        assertEquals(Integer.valueOf(points),cardPoints.calcPoints(cardSide,field));
    }
}
