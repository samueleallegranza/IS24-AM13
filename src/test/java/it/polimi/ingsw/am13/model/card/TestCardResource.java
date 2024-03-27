package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestCardResource {

    @Test
    public void testCreation() throws InvalidCardCreationException {
        List<Corner> frontCornersTmp = Arrays.asList(new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL));
        assertThrows(InvalidCardCreationException.class, () -> new CardResource("bho", Color.FUNGUS, frontCornersTmp, 0));
        List<Corner> frontCorners = Arrays.asList(new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL));
        assertThrows(InvalidCardCreationException.class, () -> new CardResource("bho", Color.NO_COLOR, frontCorners, 0));

        CardResource card = new CardResource("bho", Color.FUNGUS, frontCorners, 1);

        CardSidePlayable side = card.getFront();
        assertEquals(side.getColor(), Color.FUNGUS);
        assertTrue(side.getRequirements().isEmpty());
        assertEquals(side.getCorners(), frontCorners);
        assertEquals(side.getCenterResources().size(), 0);

        side = card.getBack();
        assertEquals(side.getColor(), Color.FUNGUS);
        assertTrue(side.getRequirements().isEmpty());
        assertEquals(side.getCorners().size(), 4);
        for(Corner c : side.getCorners())
            assertEquals(c.getResource(), Resource.NO_RESOURCE);
        assertEquals(side.getCenterResources().size(), 1);
        assertEquals(side.getCenterResources().getFirst(), Resource.FUNGUS);
    }

}