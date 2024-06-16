package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCardSideObjectiveActive {

    /**
     * Test the creation of two card side objective active
     */
    @Test
    public void testCreation() {
        Map<Resource, Integer> set = new HashMap<>();
        CardSideObjectiveActive cardSide0 = null;
        try {
            cardSide0 = new CardSideObjectiveActive(2, Color.FUNGUS, Color.FUNGUS, Color.FUNGUS, -1, -1);
        } catch (InvalidCardCreationException e) {
            throw new RuntimeException(e);
        }
        set.put(Resource.FUNGUS, 3);
        CardSideObjectiveActive cardSide1 = new CardSideObjectiveActive(2, set);

        assertEquals(2,cardSide0.getPoints().getPointsMultiplier());
        assertEquals(2,cardSide1.getPoints().getPointsMultiplier());

        assertEquals(new CardSideObjectiveActive(2, set), cardSide1);
    }
}
