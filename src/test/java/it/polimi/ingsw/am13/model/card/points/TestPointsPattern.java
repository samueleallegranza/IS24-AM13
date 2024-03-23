package it.polimi.ingsw.am13.model.card.points;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayCardException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPointsPatternException;
import it.polimi.ingsw.am13.model.exceptions.RequirementsNotMetException;
import it.polimi.ingsw.am13.model.player.Field;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestPointsPattern {

    @Test
    public void testPointsPatternValidParam() {
        assertThrows(InvalidPointsPatternException.class, () -> new PointsPattern(null, null, null,
                -2,-1, 2));
        assertThrows(InvalidPointsPatternException.class, () -> new PointsPattern(null, null, null,
                0,2, 2));
        assertThrows(InvalidPointsPatternException.class, () -> new PointsPattern(null, null, null,
                -2,2, 2));
        assertDoesNotThrow(() -> new PointsPattern(null, null, null,
                0,1, 2));
    }

    @Test
    public void testCalcPoints() throws InvalidPointsPatternException {
        try {
            PointsPattern points = new PointsPattern(Color.FUNGUS, Color.ANIMAL, Color.ANIMAL,
                    -1,0, 1);
            // 2 points for pattern left - under

            Field field = new Field(null);
            field.playCardSide(new CardSidePlayable(null, null, null,
                    null, Color.FUNGUS), new Coordinates(0,2));
            field.playCardSide(new CardSidePlayable(null, null, null,
                    null, Color.ANIMAL), new Coordinates(-1,1));
            field.playCardSide(new CardSidePlayable(null, null, null,
                    null, Color.ANIMAL), new Coordinates(-1,-1));

            assertEquals(1, points.calcPoints(field));

            // Now I add another patter non-intersecting right under the previous one.
            // There's a column of 4 Animal cards in x=-1
            field.playCardSide(new CardSidePlayable(null, null, null,
                    null, Color.FUNGUS), new Coordinates(0,-2));
            field.playCardSide(new CardSidePlayable(null, null, null,
                    null, Color.ANIMAL), new Coordinates(-1,-3));
            field.playCardSide(new CardSidePlayable(null, null, null,
                    null, Color.ANIMAL), new Coordinates(-1,-5));

            assertEquals(2, points.calcPoints(field));

            // Now I add another patter intersecting between the previous ones.
            // There's also a column of 3 Fungus cards in x=0
            field.playCardSide(new CardSidePlayable(null, null, null,
                    null, Color.FUNGUS), new Coordinates(0,0));

            assertEquals(2, points.calcPoints(field));

            //If I add another patter at same level, it should count
            field.playCardSide(new CardSidePlayable(null, null, null,
                    null, Color.FUNGUS), new Coordinates(2,0));
            field.playCardSide(new CardSidePlayable(null, null, null,
                    null, Color.ANIMAL), new Coordinates(1,-1));
            field.playCardSide(new CardSidePlayable(null, null, null,
                    null, Color.ANIMAL), new Coordinates(1,-3));

            assertEquals(3, points.calcPoints(field));

        } catch (InvalidCoordinatesException | InvalidPlayCardException | RequirementsNotMetException e) {
            throw new RuntimeException(e);
        }
    }
}