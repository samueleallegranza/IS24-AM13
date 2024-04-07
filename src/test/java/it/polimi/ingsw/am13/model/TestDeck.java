package it.polimi.ingsw.am13.model;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import it.polimi.ingsw.am13.model.exceptions.InvalidDrawCardException;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import static org.junit.jupiter.api.Assertions.*;

public class TestDeck {
    private final CardFactory factory = new CardFactory();
    @Test
    public void testDeckResource() throws InvalidCardCreationException, InvalidDrawCardException {
        LinkedList<CardResource> orderedList = factory.createCardsResource();
        Deck<CardResource> shuffledDeck = new Deck<>(orderedList);

        shuffledDeck.shuffle();
        assert(shuffledDeck.getSize() == 40);
        assertSame(shuffledDeck.getSize(), orderedList.size());

        CardResource cardTop = shuffledDeck.getTop();
        CardResource cardDrawn = shuffledDeck.draw();
        assertSame(cardTop, cardDrawn);
        for (int i = 0; i < 39; i++) {
            cardDrawn = shuffledDeck.draw();
        }
        assertThrows(InvalidDrawCardException.class, () -> {
           CardResource card = shuffledDeck.draw();
        });
        assertThrows(InvalidDrawCardException.class, () -> {
            CardResource card = shuffledDeck.getTop();
        });
        assertTrue(shuffledDeck.isEmpty());
    }

    @Test
    public void testDeckGold() throws InvalidCardCreationException, InvalidDrawCardException {
        LinkedList<CardGold> orderedList = factory.createCardsGold();
        Deck<CardGold> shuffledDeck = new Deck<>(orderedList);

        shuffledDeck.shuffle();
        assert(shuffledDeck.getSize() == 40);
        assertSame(shuffledDeck.getSize(), orderedList.size());

        CardGold cardTop = shuffledDeck.getTop();
        CardGold cardDrawn = shuffledDeck.draw();
        assertSame(cardTop, cardDrawn);
        for (int i = 0; i < 39; i++) {
            cardDrawn = shuffledDeck.draw();
        }
        assertThrows(InvalidDrawCardException.class, () -> {
            CardGold card = shuffledDeck.draw();
        });
        assertThrows(InvalidDrawCardException.class, () -> {
            CardGold card = shuffledDeck.getTop();
        });
        assertTrue(shuffledDeck.isEmpty());
    }

    @Test
    public void testDeckStarter() throws InvalidCardCreationException, InvalidDrawCardException {
        LinkedList<CardStarter> orderedList = factory.createCardsStarter();
        Deck<CardStarter> shuffledDeck = new Deck<>(orderedList);

        shuffledDeck.shuffle();
        assert(shuffledDeck.getSize() == 6);
        assertSame(shuffledDeck.getSize(), orderedList.size());

        CardStarter cardTop = shuffledDeck.getTop();
        CardStarter cardDrawn = shuffledDeck.draw();
        assertSame(cardTop, cardDrawn);
        for (int i = 0; i < 5; i++) {
            cardDrawn = shuffledDeck.draw();
        }
        assertThrows(InvalidDrawCardException.class, () -> {
            CardStarter card = shuffledDeck.draw();
        });
        assertThrows(InvalidDrawCardException.class, () -> {
            CardStarter card = shuffledDeck.getTop();
        });
        assertTrue(shuffledDeck.isEmpty());
    }

    @Test
    public void testDeckObjectives() throws InvalidCardCreationException, InvalidDrawCardException {
        LinkedList<CardObjective> orderedList = factory.createCardsObjective();
        Deck<CardObjective> shuffledDeck = new Deck<>(orderedList);

        shuffledDeck.shuffle();
        assert(shuffledDeck.getSize() == 16);
        assertSame(shuffledDeck.getSize(), orderedList.size());

        CardObjective cardTop = shuffledDeck.getTop();
        CardObjective cardDrawn = shuffledDeck.draw();
        assertSame(cardTop, cardDrawn);
        for (int i = 0; i < 15; i++) {
            cardDrawn = shuffledDeck.draw();
        }
        assertThrows(InvalidDrawCardException.class, () -> {
            CardObjective card = shuffledDeck.draw();
        });
        assertThrows(InvalidDrawCardException.class, () -> {
            CardObjective card = shuffledDeck.getTop();
        });
        assertTrue(shuffledDeck.isEmpty());
    }
}
