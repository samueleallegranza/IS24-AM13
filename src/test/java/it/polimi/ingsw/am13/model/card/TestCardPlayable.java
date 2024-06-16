package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.card.points.PointsInstant;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TestCardPlayable {

    @Test
    public void testGetPlayedCardSide() throws InvalidCardCreationException {
        CardSidePlayable front = new CardSidePlayable(
                new HashMap<>(),
                Corner.generateEmptyCorners(),
                new ArrayList<>(),
                new PointsInstant(0),
                Color.PLANT,
                "",
                Side.SIDEFRONT
        );
        CardSidePlayable back = new CardSidePlayable(
                new HashMap<>(),
                Corner.generateEmptyCorners(),
                new ArrayList<>(),
                new PointsInstant(0),
                Color.PLANT,
                "",
                Side.SIDEBACK
        );
        CardPlayable card = new CardResource("bho", front, back);
        assertNull(card.getPlayedCardSide());
        card.placeCardInField(Side.SIDEFRONT);
        assertEquals(card.getPlayedCardSide(), front);
        card.placeCardInField(Side.SIDEBACK);
        assertEquals(card.getPlayedCardSide(), back);
    }
}