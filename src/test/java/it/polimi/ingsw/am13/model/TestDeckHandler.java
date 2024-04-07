package it.polimi.ingsw.am13.model;
import it.polimi.ingsw.am13.model.card.CardFactory;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.CardResource;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import it.polimi.ingsw.am13.model.exceptions.InvalidDrawCardException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TestDeckHandler {
    private final CardFactory factory = new CardFactory();
    @Test
    public void testDeckHandler() throws InvalidCardCreationException, InvalidDrawCardException {
        DeckHandler<CardResource> handler = new DeckHandler<>(factory.createCardsResource());
        List<CardResource> visibleCards = handler.getVisibleCards();
        CardResource top = handler.getPickables().getFirst();
        assert(top.getVisibleSide() == Side.SIDEBACK);
        assert(visibleCards.get(0).getVisibleSide() == Side.SIDEFRONT);
        assert(visibleCards.get(1).getVisibleSide() == Side.SIDEFRONT);
        assertThrows(IndexOutOfBoundsException.class, ()-> handler.showFromTable(3));
        assertSame(handler.showFromTable(0), visibleCards.get(0));
        assertSame(handler.showFromTable(1), visibleCards.get(1));

        assertEquals(top, handler.getDeckTop());
        assertThrows(IndexOutOfBoundsException.class, () -> handler.drawFromTable(3));
        assertEquals(top, handler.getDeckTop());
        assertFalse(handler.isDeckEmpty());

        // Check draw from deck
        top = handler.drawFromDeck();
        assert(top.getVisibleSide() == null);
        assertSame(Side.SIDEBACK, handler.getDeckTop().getVisibleSide());
        assertEquals(visibleCards, handler.getVisibleCards());
        assertNotEquals(top, handler.getDeckTop());

        // Check draw from table
        top = handler.getDeckTop();
        CardResource expectedCard = handler.showFromTable(0);
        CardResource actualCard = handler.drawFromTable(0);
        assertEquals(expectedCard.getFront(), actualCard.getFront());
        assertEquals(expectedCard.getBack(), actualCard.getBack());
        assert(actualCard.getVisibleSide() == null);
        assertEquals(top.getFront(), handler.showFromTable(0).getFront());
    }

    @Test
    public void testDeckEmpty() throws InvalidCardCreationException, InvalidDrawCardException {
        DeckHandler<CardResource> handler = new DeckHandler<>(factory.createCardsResource());
        for (int i = 0; i < 19; i++) {
            handler.drawFromDeck();
            handler.drawFromTable(i%2);
        }

        assertTrue(handler.isDeckEmpty());
        CardResource expected = handler.getPickables().get(1);
        CardResource cardResource = handler.drawFromTable(0);
        assertSame(expected.getFront(), cardResource.getFront());
//        assertThrows(IndexOutOfBoundsException.class, () -> handler.drawFromTable(1));
        assertThrows(IndexOutOfBoundsException.class, () -> handler.drawFromTable(2));
        assertThrows(InvalidDrawCardException.class, () -> handler.drawFromTable(0));
        assertThrows(InvalidDrawCardException.class, handler::drawFromDeck);
    }

    @Test
    public void testPickCard() throws InvalidCardCreationException {
        DeckHandler<CardResource> handler = new DeckHandler<>(factory.createCardsResource());
        try {
            CardPlayableIF card;
            CardResource resource;
            for (int i = 0; i < 38; i++) {
                card = handler.getDeckTop();
                resource = handler.pickCard(card);
                assert(card == resource);
            }
            assertTrue(handler.isDeckEmpty());

            card = handler.showFromTable(0);
            resource = handler.pickCard(card);
            assert(card == resource);

            resource = handler.pickCard(card);
            assert(resource == null);


        } catch (InvalidDrawCardException e) {
            throw new RuntimeException(e);
        }


    }
}
