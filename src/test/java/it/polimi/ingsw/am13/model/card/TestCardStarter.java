package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestCardStarter {

    @Test
    public void testCreation() throws InvalidCardCreationException {
        List<Corner> frontCorners = Arrays.asList(new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL));
        List<Corner> backCorners = Arrays.asList(new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL));
        CardStarter card = new CardStarter("s001",
                frontCorners,
                Arrays.asList(Resource.PLANT, Resource.FUNGUS),
                backCorners
        );

        CardSidePlayable side = card.getFront();
        assertEquals(side.getColor(), Color.NO_COLOR);
        assertTrue(side.getRequirements().isEmpty());
        assertEquals(side.getCorners(), frontCorners);
        assertEquals(side.getCenterResources().size(), 2);
        assertEquals(side.getCenterResources().get(0), Resource.PLANT);
        assertEquals(side.getCenterResources().get(1), Resource.FUNGUS);

        side = card.getBack();
        assertEquals(side.getColor(), Color.NO_COLOR);
        assertTrue(side.getRequirements().isEmpty());
        assertEquals(side.getCorners(), backCorners);
        assertEquals(side.getCenterResources().size(), 0);
    }

}