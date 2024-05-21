package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.card.points.PointsInstant;
import it.polimi.ingsw.am13.model.card.points.PointsPlayable;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestCardGold {

    @Test
    public void testCreation() throws InvalidCardCreationException {
        PointsPlayable points = new PointsInstant(1);
        String id = "bho";
        List<Corner> frontCornersTmp = Arrays.asList(new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL));
        Map<Resource, Integer> reqs = new HashMap<>();
        reqs.put(Resource.NO_RESOURCE, 3);
        assertThrows(InvalidCardCreationException.class, () -> new CardGold(id, Color.FUNGUS, reqs, frontCornersTmp, points));

        List<Corner> frontCorners = Arrays.asList(new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL));
        assertThrows(InvalidCardCreationException.class, () -> new CardGold(id, Color.FUNGUS, reqs, frontCorners, points));

        reqs.remove(Resource.NO_RESOURCE);
        reqs.put(Resource.PLANT, 1);
        reqs.put(Resource.FUNGUS, 2);
        CardGold card = new CardGold(id, Color.FUNGUS, reqs, frontCorners, points);

        CardSidePlayable side = card.getSide(Side.SIDEFRONT);
        assertEquals(side.getColor(), Color.FUNGUS);
        assertEquals(side.getRequirements(), reqs);
        assertEquals(side.getCorners(), frontCorners);
        assertEquals(side.getCenterResources().size(), 0);

        side = card.getSide(Side.SIDEBACK);
        assertEquals(side.getColor(), Color.FUNGUS);
        assertTrue(side.getRequirements().isEmpty());
        assertEquals(side.getCorners().size(), 4);
        for(Corner c : side.getCorners())
            assertEquals(c.getResource(), Resource.NO_RESOURCE);
        assertEquals(side.getCenterResources().size(), 1);
        assertEquals(side.getCenterResources().getFirst(), Resource.FUNGUS);
    }

}