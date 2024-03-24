package it.polimi.ingsw.am13.model.card.points;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.am13.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayCardException;
import it.polimi.ingsw.am13.model.exceptions.RequirementsNotMetException;
import it.polimi.ingsw.am13.model.exceptions.VariableAlreadySetException;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.player.Field;

import java.util.*;

public class TestPointsSet {

    final CardSidePlayable starter = new CardSidePlayable(
            new HashMap<>(),
            Arrays.asList(new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE)),
            new ArrayList<>(),
            new PointsInstant(0),
            Color.NO_COLOR
    );  // nothing
    final CardStarter starter_card = new CardStarter("starter001", starter, starter);

    CardSidePlayable side1 = new CardSidePlayable(
            new HashMap<>(),
            Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE), new Corner(Resource.PLANT), new Corner(Resource.PLANT)),
            List.of(Resource.PLANT),
            new PointsInstant(0),
            Color.NO_COLOR
    );  // 4 plant
    CardSidePlayable side2 = new CardSidePlayable(
            new HashMap<>(),
            Arrays.asList(new Corner(Resource.ANIMAL), new Corner(Resource.NO_RESOURCE), new Corner(Resource.ANIMAL), new Corner(Resource.NO_RESOURCE)),
            List.of(Resource.ANIMAL),
            new PointsInstant(0),
            Color.NO_COLOR
    );  // 3 animal
    CardSidePlayable side3 = new CardSidePlayable(
            new HashMap<>(),
            Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE), new Corner(Resource.PLANT), new Corner(Resource.PLANT)),
            List.of(Resource.PLANT),
            new PointsInstant(0),
            Color.NO_COLOR
    );  // 4 plant
    CardSidePlayable side4 = new CardSidePlayable(
            new HashMap<>(),
            Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE), new Corner(Resource.PLANT), new Corner(Resource.PLANT)),
            List.of(Resource.PLANT),
            new PointsInstant(0),
            Color.NO_COLOR
    );  // 4 plant

    @Test
    public void testCalcPoints1(){
        try {
            Map<Resource, Integer> resForPoints = new HashMap<>();
            resForPoints.put(Resource.ANIMAL, 1);
            resForPoints.put(Resource.PLANT, 2);
            PointsSet points = new PointsSet(resForPoints, 1);

            Field field = new Field();
            CardSidePlayable startSide = new CardSidePlayable(
                    new HashMap<>(),
                    Arrays.asList(new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE)),
                    List.of(),
                    new PointsInstant(0),
                    Color.NO_COLOR
            );
            field.initStartCard(new CardStarter("s0001f", startSide, startSide));
            field.playCardSide(startSide, new Coordinates(0,0)
            );
            // nothing
            assertEquals(0, points.calcPoints(field));
            assertEquals(0, field. getResourcesInField().get(Resource.PLANT));
            assertEquals(0, field.getResourcesInField().get(Resource.ANIMAL));

            field.playCardSide(
                    new CardSidePlayable(
                            new HashMap<>(),
                            Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE), new Corner(Resource.PLANT), new Corner(Resource.PLANT)),
                            List.of(Resource.PLANT),
                            new PointsInstant(0),
                            Color.NO_COLOR
                    ), new Coordinates(1,1)
            );
            // 4 plant
            assertEquals(0, points.calcPoints(field));
            assertEquals(4, field. getResourcesInField().get(Resource.PLANT));
            assertEquals(0, field.getResourcesInField().get(Resource.ANIMAL));

            field.playCardSide(
                    new CardSidePlayable(
                            new HashMap<>(),
                            Arrays.asList(new Corner(Resource.ANIMAL), new Corner(Resource.NO_RESOURCE), new Corner(Resource.ANIMAL), new Corner(Resource.NO_RESOURCE)),
                            List.of(Resource.ANIMAL),
                            new PointsInstant(0),
                            Color.NO_COLOR
                    ), new Coordinates(2,2)
            );
            // 4 plant, 3 animalz
            assertEquals(2, points.calcPoints(field));
            assertEquals(4, field. getResourcesInField().get(Resource.PLANT));
            assertEquals(3, field.getResourcesInField().get(Resource.ANIMAL));

            field.playCardSide(
                    new CardSidePlayable(
                            new HashMap<>(),
                            Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE), new Corner(Resource.PLANT), new Corner(Resource.PLANT)),
                            List.of(Resource.PLANT),
                            new PointsInstant(0),
                            Color.NO_COLOR
                    ), new Coordinates(3,3)
            );
            // 8 plant, 3 animal
            assertEquals(3, points.calcPoints(field));
            assertEquals(8, field. getResourcesInField().get(Resource.PLANT));
            assertEquals(3, field.getResourcesInField().get(Resource.ANIMAL));

            field.playCardSide(
                    new CardSidePlayable(
                            new HashMap<>(),
                            Arrays.asList(new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE)),
                            List.of(Resource.NO_RESOURCE),
                            new PointsInstant(0),
                            Color.NO_COLOR
                    ), new Coordinates(3,1)
            );
            // 8 plant, 2 animal
            assertEquals(2, points.calcPoints(field));
            assertEquals(8, field. getResourcesInField().get(Resource.PLANT));
            assertEquals(2, field.getResourcesInField().get(Resource.ANIMAL));
        } catch (InvalidCoordinatesException | InvalidPlayCardException | RequirementsNotMetException |
                 VariableAlreadySetException e) {
            throw new RuntimeException(e);
        }
    }
}
