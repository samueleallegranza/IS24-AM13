package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestCard {

    @Test
    public void testCardInField() throws InvalidCardCreationException {
        Card card = new CardResource("bho", Color.FUNGUS, Corner.generateEmptyCorners(), 1);
        assertNull(card.getVisibleSide());
        card.placeCardInField(Side.SIDEFRONT);
        assertEquals(card.getVisibleSide(), Side.SIDEFRONT);
        card.placeCardInField(Side.SIDEBACK);
        assertEquals(card.getVisibleSide(), Side.SIDEBACK);
        card.removeCardFromField();
        assertNull(card.getVisibleSide());
    }
}
