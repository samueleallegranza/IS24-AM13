package it.polimi.ingsw.am13.model.card.points;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.Field;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TestPointsPattern {

    final CardSidePlayable starter = new CardSidePlayable(
            new HashMap<>(),
            Arrays.asList(new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE)),
            new ArrayList<>(),
            new PointsInstant(0),
            Color.NO_COLOR,
            "",
            Side.SIDEFRONT
    );  // nothing

    public TestPointsPattern() throws InvalidCardCreationException {
    }

    private void playCard(Field field, Color color, int x, int y) {
        try {
            field.playCardSide(
                    new CardSidePlayable(
                            new HashMap<>(),
                            Arrays.asList(new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE)),
                            new ArrayList<>(),
                            new PointsInstant(0),
                            color,
                            "",
                            Side.SIDEFRONT
                    ), new Coordinates(x, y)
            );
        } catch (InvalidPlayCardException | RequirementsNotMetException | InvalidCoordinatesException |
                 InvalidCardCreationException e) {
            throw new RuntimeException(e);
        }
    }
    private void playStart(Field field) {
        try {
            field.playCardSide(starter, new Coordinates(0,0));
        } catch (InvalidPlayCardException | RequirementsNotMetException | InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPointsPatternValidParam() {
        assertThrows(InvalidCardCreationException.class, () -> new PointsPattern(null, null, null,
                -2,-1, 2));
        assertThrows(InvalidCardCreationException.class, () -> new PointsPattern(null, null, null,
                0,2, 2));
        assertThrows(InvalidCardCreationException.class, () -> new PointsPattern(null, null, null,
                -2,2, 2));
        assertDoesNotThrow(() -> new PointsPattern(null, null, null,
                0,1, 2));
    }

    @Test
    public void testCalcPoints() throws InvalidCardCreationException {
        PointsPattern points = new PointsPattern(Color.FUNGUS, Color.ANIMAL, Color.ANIMAL,
                -1,0, 1);
        // 2 points for pattern F - left - A - under - A

        //verify that the constructor, getters, and equals work correctly
        assertEquals(Color.FUNGUS,points.getColor1());
        assertEquals(Color.ANIMAL,points.getColor2());
        assertEquals(Color.ANIMAL,points.getColor3());
        try {
            assertEquals(new Coordinates(-1,-1),points.getVec12());
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }

        try {
            assertEquals(new Coordinates(-1,-3),points.getVec13());
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
        assertEquals(1,points.getPointsMultiplier());
        assertEquals(new PointsPattern(Color.FUNGUS, Color.ANIMAL, Color.ANIMAL,
                -1,0, 1),points);


        Field field = new Field();
        playStart(field);

        playCard(field, Color.FUNGUS, -1,-1);
        playCard(field, Color.ANIMAL, -2,-2);
        playCard(field, Color.NO_COLOR, -3,-3);
        assertEquals(0, points.calcPoints(field));

        playCard(field, Color.ANIMAL, -2,-4);
        assertEquals(1, points.calcPoints(field));

        playCard(field, Color.FUNGUS, -1,-5);
        playCard(field, Color.ANIMAL, -2,-6);
        playCard(field, Color.NO_COLOR, -3,-7);
        playCard(field, Color.ANIMAL, -2,-8);
        assertEquals(2, points.calcPoints(field));

        // Now I add a Fungus such that 3 pattern intersect. Only 2 should be counted
        playCard(field, Color.FUNGUS, -1,-3);
        assertEquals(2, points.calcPoints(field));

        // If I add another patter at the same level, it should not be a problem
        playCard(field, Color.FUNGUS, 1,-1);
        playCard(field, Color.ANIMAL, 0,-2);
        playCard(field, Color.ANIMAL, 0,-4);
        assertEquals(3, points.calcPoints(field));
    }
}