package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestColor {

    @Test
    public void testCorrespondingResource() throws InvalidCardCreationException {
        Color c = Color.NO_COLOR;
        assertThrows(InvalidCardCreationException.class, c::correspondingResource);

        c = Color.INSECT;
        assertEquals(Resource.INSECT, c.correspondingResource());
        c = Color.ANIMAL;
        assertEquals(Resource.ANIMAL, c.correspondingResource());
        c = Color.FUNGUS;
        assertEquals(Resource.FUNGUS, c.correspondingResource());
        c = Color.PLANT;
        assertEquals(Resource.PLANT, c.correspondingResource());
    }
}