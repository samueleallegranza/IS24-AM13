package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.card.points.PointsInstant;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import it.polimi.ingsw.am13.model.exceptions.VariableAlreadySetException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TestCorner {
    /**
     * This test verifies that isPlaceable and addLink are working correctly
     */
    @Test
    public void testCheckPlaceable() throws InvalidCardCreationException {
        Resource r=Resource.FUNGUS;
        Corner corner=new Corner(r);
        assertEquals(r,corner.getResource());
        assertTrue(corner.isPlaceable());
        corner.coverCorner();
        assertFalse(corner.isPlaceable());
        int npoints=3;
        CardSidePlayable cardSidePlayable=new CardSidePlayable(new HashMap<>(),Corner.generateEmptyCorners(),new ArrayList<>(),new PointsInstant(npoints), Color.NO_COLOR);
        try{
            corner.addLinkToCard(cardSidePlayable);
            assertEquals(corner.getLink(),cardSidePlayable);
        }
        catch (VariableAlreadySetException variableAlreadySetException){
            System.out.println("The link was already set before");
        }
    }
}
