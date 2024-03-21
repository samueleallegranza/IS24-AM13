package it.polimi.ingsw.am13.model.player;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.am13.model.card.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestField {

    @Test
    public void testCountResources() {
        Field field = new Field();
        Map<Coordinates, CardSidePlayable> cards = new HashMap<>();
        cards.put(new Coordinates(0,1), new CardSidePlayable(null,
                Arrays.asList(new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL)),
                Arrays.asList(Resource.ANIMAL), null, Color.ANIMAL));
        cards.put(new Coordinates(0,4), new CardSidePlayable(null,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL)),
                Arrays.asList(Resource.ANIMAL, Resource.PLANT), null, Color.ANIMAL));
        field.setField(cards);

        Map<Resource, Integer> freqs = field.countResources();
        assertTrue(freqs.containsKey(Resource.ANIMAL));
        assertTrue(freqs.containsKey(Resource.PLANT));
        assertFalse(freqs.containsKey(Resource.FUNGUS));
        assertEquals(7,freqs.get(Resource.ANIMAL));
        assertEquals(2,freqs.get(Resource.PLANT));
    }

}
