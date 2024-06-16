package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCardObjective {

    /**
     * Test the construction of a card objective
     */
    @Test
    public void testCreation() {
        Map<Resource, Integer> set = new HashMap<>();
        CardObjective card0 = null;
        try {
            card0 = new CardObjective("o000", 2, Color.FUNGUS, Color.FUNGUS, Color.FUNGUS, -1, -1);
        } catch (InvalidCardCreationException e) {
            throw new RuntimeException(e);
        }
        set.put(Resource.FUNGUS, 3);
        CardObjective card1 = new CardObjective("o008", 2, set);

        assertEquals(2,card0.getPoints().getPointsMultiplier());
        assertEquals(2,card1.getPoints().getPointsMultiplier());
    }
}
