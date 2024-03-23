package it.polimi.ingsw.am13.model.card.points;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.am13.model.exceptions.InvalidCoordinatesException;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.player.Field;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestPointsSet {

    @Test
    public void testCalcPoints1(){
        try {
            Field field = new Field(null);
            field.playCardSide(new CardSidePlayable(null,
                    Arrays.asList(new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL)),
                    Arrays.asList(Resource.ANIMAL), null, Color.ANIMAL), new Coordinates(0,2));
            field.playCardSide(new CardSidePlayable(null,
                    Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL)),
                    Arrays.asList(Resource.ANIMAL, Resource.PLANT), null, Color.ANIMAL), new Coordinates(0,4));
            // 7 animal, 2 plant

            Map<Resource, Integer> resForPoints = new HashMap<>();
            resForPoints.put(Resource.ANIMAL, 2);
            resForPoints.put(Resource.PLANT, 1);
            PointsSet points = new PointsSet(resForPoints, 2);

            assertEquals(4, points.calcPoints(field));

            // Now 1 plant is not visible
            field.getCardAt(new Coordinates(0,4)).getCorners().get(0).coverCorner();
            assertEquals(2, points.calcPoints(field));
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }
}
