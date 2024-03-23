package it.polimi.ingsw.am13.model.player;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.InvalidCoordinatesException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

public class TestField {

    @Test
    public void testCountResources() {
        try {
            Field field = new Field(null);
            field.playCardSide(new CardSidePlayable(null,
                            Arrays.asList(new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL)),
                            Arrays.asList(Resource.ANIMAL), null, Color.ANIMAL), new Coordinates(0,2));
            field.playCardSide(new CardSidePlayable(null,
                    Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL)),
                    Arrays.asList(Resource.ANIMAL, Resource.PLANT), null, Color.ANIMAL), new Coordinates(0,4));

            Map<Resource, Integer> freqs = field.countResources();
            assertTrue(freqs.containsKey(Resource.ANIMAL));
            assertTrue(freqs.containsKey(Resource.PLANT));
            assertFalse(freqs.containsKey(Resource.FUNGUS));
            assertEquals(7,freqs.get(Resource.ANIMAL));
            assertEquals(2,freqs.get(Resource.PLANT));


            // Now 1 animal is covered
            field.getCardAt(new Coordinates(0,4)).getCorners().get(1).coverCorner();
            freqs = field.countResources();
            assertTrue(freqs.containsKey(Resource.ANIMAL));
            assertTrue(freqs.containsKey(Resource.PLANT));
            assertFalse(freqs.containsKey(Resource.FUNGUS));
            assertEquals(6,freqs.get(Resource.ANIMAL));
            assertEquals(2,freqs.get(Resource.PLANT));
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }

}
