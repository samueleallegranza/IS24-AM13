package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.card.points.PointsPlayable;
import it.polimi.ingsw.am13.model.card.points.PointsResource;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CardFactoryTest {

    @Test
    void testReadingCardsResource() {
        CardFactory factory = new CardFactory();
        assertDoesNotThrow(
                () -> {
                    LinkedList<CardResource> deck = factory.createCardsResource();
                });
    }

    @Test
    void testReadingCardsGold() {
        CardFactory factory = new CardFactory();
        assertDoesNotThrow(
                () -> {
                    LinkedList<CardGold> deck = factory.createCardsGold();
                });
    }

    @Test
    void testReadingCardsStarter() {
        CardFactory factory = new CardFactory();
        assertDoesNotThrow(
                () -> {
                    LinkedList<CardStarter> deck = factory.createCardsStarter();
                });
    }

    @Test
    void testReadingCardsObjective() {
        CardFactory factory = new CardFactory();
        assertDoesNotThrow(
                () -> {
                    LinkedList<CardObjective> deck = factory.createCardsObjective();
                });
    }

    @Test
    void testFirstCardResource() throws InvalidCardCreationException {
        CardFactory factory = new CardFactory();
        LinkedList<CardResource> deck = factory.createCardsResource();

        List<Corner> cornersF = new ArrayList<>();
        cornersF.add(new Corner(Resource.FUNGUS));
        cornersF.add(new Corner(Resource.NO_RESOURCE));
        cornersF.add(new Corner());
        cornersF.add(new Corner(Resource.FUNGUS));

        CardResource firstCard = new CardResource("r000", Color.FUNGUS, cornersF, 0);

        CardResource actualFirstCard = deck.getFirst();
        assertEquals(firstCard, actualFirstCard);
    }
    @Test
    void testFirstCardGold() throws InvalidCardCreationException {
        CardFactory factory = new CardFactory();
        LinkedList<CardGold> deck = factory.createCardsGold();

        List<Corner> cornersF = new ArrayList<>();
        cornersF.add(new Corner());
        cornersF.add(new Corner(Resource.NO_RESOURCE));
        cornersF.add(new Corner(Resource.QUILL));
        cornersF.add(new Corner(Resource.NO_RESOURCE));
        Map<Resource, Integer> reqs = new HashMap<>();
        reqs.put(Resource.FUNGUS, 2);
        reqs.put(Resource.ANIMAL, 1);
        PointsPlayable points = new PointsResource(1, Resource.QUILL);

        CardGold firstCard = new CardGold("g000", Color.FUNGUS, reqs, cornersF, points);

        CardGold actualFirstCard = deck.getFirst();
        assertEquals(firstCard, actualFirstCard);
    }

    @Test
    void testFirstCardStarter() throws InvalidCardCreationException {
        CardFactory factory = new CardFactory();
        LinkedList<CardStarter> deck = factory.createCardsStarter();

        List<Corner> cornersF = new ArrayList<>();
        cornersF.add(new Corner(Resource.NO_RESOURCE));
        cornersF.add(new Corner(Resource.PLANT));
        cornersF.add(new Corner(Resource.NO_RESOURCE));
        cornersF.add(new Corner(Resource.INSECT));
        List<Resource> centerF = new ArrayList<>();
        centerF.add(Resource.INSECT);

        List<Corner> cornersB = new ArrayList<>();
        cornersB.add(new Corner(Resource.FUNGUS));
        cornersB.add(new Corner(Resource.PLANT));
        cornersB.add(new Corner(Resource.ANIMAL));
        cornersB.add(new Corner(Resource.INSECT));

        CardStarter firstCard = new CardStarter("s000", cornersF, centerF, cornersB);

        CardStarter actualFirstCard = deck.getFirst();
        assertEquals(firstCard, actualFirstCard);
    }

    @Test
    void testFirstCardObjective() throws InvalidCardCreationException {
        CardFactory factory = new CardFactory();
        LinkedList<CardObjective> deck = factory.createCardsObjective();
        Map<Resource, Integer> set = new HashMap<>();

        CardObjective card0 = new CardObjective("o000", 2, Color.FUNGUS, Color.FUNGUS, Color.FUNGUS, -1, -1);
        assertEquals(card0, deck.getFirst());

        CardObjective card4 = new CardObjective("o004", 3, Color.FUNGUS, Color.FUNGUS, Color.PLANT, 0, 1);
        assertEquals(card4, deck.get(4));

        set.put(Resource.FUNGUS, 3);
        CardObjective card8 = new CardObjective("o008", 2, set);
        assertEquals(card8, deck.get(8));

        set.clear();
        set.put(Resource.QUILL, 1);
        set.put(Resource.INKWELL, 1);
        set.put(Resource.MANUSCRIPT, 1);
        CardObjective card12 = new CardObjective("o012", 3, set);
        assertEquals(card12, deck.get(12));
    }
}