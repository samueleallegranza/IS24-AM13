package it.polimi.ingsw.am13.client.gamestate;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import it.polimi.ingsw.am13.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayCardException;
import it.polimi.ingsw.am13.model.exceptions.RequirementsNotMetException;
import it.polimi.ingsw.am13.model.player.Field;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestFieldState {

    @Test
    public void testPlaceCardSideAtCoord() throws InvalidCardCreationException, RequirementsNotMetException, InvalidPlayCardException, InvalidCoordinatesException {
        CardFactory cardFactory = new CardFactory();
        List<CardResource> cards = cardFactory.createCardsResource();
        CardSidePlayable cs1 = cards.getFirst().getSide(Side.SIDEBACK);
        Field field = new Field();
        field.playCardSide(cs1, Coordinates.origin());

        FieldState fieldState = new FieldState(field);
        assertEquals(1, fieldState.getPlacedCoords().size());
        assertTrue(fieldState.getPlacedCoords().contains(Coordinates.origin()));
        assertEquals(4, fieldState.getAvailableCoords().size());
        assertTrue(fieldState.getAvailableCoords().containsAll(field.getAvailableCoords()));
        assertEquals(cs1, fieldState.getCardSideAtCoord(Coordinates.origin()));

        Coordinates c2 = new Coordinates(1, 1);
        CardSidePlayable cs2 = cards.get(1).getSide(Side.SIDEBACK);
        assertNull(fieldState.getCardSideAtCoord(c2));      // Now no card is at coord c2 yet
        fieldState.placeCardSideAtCoord(c2, cs2);
        assertEquals(2, fieldState.getPlacedCoords().size());       // there are the new coordinates added
        assertTrue(fieldState.getPlacedCoords().contains(c2));
        assertEquals(cs1, fieldState.getCardSideAtCoord(Coordinates.origin()));     // but available coords are not updated
        assertEquals(4, fieldState.getAvailableCoords().size());
        assertTrue(fieldState.getAvailableCoords().containsAll(field.getAvailableCoords()));

        field.playCardSide(cs2, c2);        // Now i play the card also in field of model, that calculates the new avlb coords
        fieldState.setAvailableCoords(field.getAvailableCoords());
        assertEquals(6, fieldState.getAvailableCoords().size());
        assertTrue(fieldState.getAvailableCoords().containsAll(field.getAvailableCoords()));
    }

    @Test
    public void testResourcesInField() throws InvalidCardCreationException, InvalidCoordinatesException {
        CardFactory cardFactory = new CardFactory();
        List<CardResource> cards = cardFactory.createCardsResource();
        CardSidePlayable cs1 = cards.getFirst().getSide(Side.SIDEBACK);
        FieldState field = new FieldState(new Field());
        field.placeCardSideAtCoord(Coordinates.origin(), cs1);

        Map<Resource, Integer> resources = field.getResourcesInField();
        Map<Resource, Integer> expectedRes = new HashMap<>();
        for(Resource r : Resource.values())
            expectedRes.put(r, 0);
        expectedRes.replace(cs1.getColor().correspondingResource(), 1);
        for(Resource r : Resource.values())
            if(r != Resource.NO_RESOURCE)
                assertEquals(expectedRes.get(r), resources.get(r));

        CardSidePlayable cs2 = cards.getFirst().getSide(Side.SIDEFRONT);
        field.placeCardSideAtCoord(new Coordinates(1,1), cs2);

        resources = field.getResourcesInField();
        for(Resource r : cs2.getCornerResources())
            expectedRes.replace(r, expectedRes.get(r)+1);
        //(there are not center resources, it i a resource card)
        for(Resource r : Resource.values())
            if(r != Resource.NO_RESOURCE)
                assertEquals(expectedRes.get(r), resources.get(r));
    }
}