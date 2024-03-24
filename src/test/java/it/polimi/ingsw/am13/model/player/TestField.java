package it.polimi.ingsw.am13.model.player;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.card.points.PointsInstant;
import it.polimi.ingsw.am13.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayCardException;
import it.polimi.ingsw.am13.model.exceptions.RequirementsNotMetException;
import it.polimi.ingsw.am13.model.exceptions.VariableAlreadySetException;
import org.junit.jupiter.api.Test;

import java.util.*;

public class TestField {

    private final ArrayList<Resource> emptyarr = new ArrayList<>();
    private final HashMap<Resource, Integer> norequirements = new HashMap<>();

    @Test
    public void testGetCardAtCoord() {
        try {
            CardSidePlayable starter = new CardSidePlayable(
                    norequirements,
                    Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                    Arrays.asList(Resource.PLANT, Resource.PLANT),
                    new PointsInstant(0),
                    Color.NO_COLOR
            );
            CardStarter starter_card = new CardStarter("starter001", starter, starter);
            CardSidePlayable resource1 = new CardSidePlayable(
                    norequirements,
                    Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                    emptyarr,
                    new PointsInstant(0),
                    Color.PLANT
            );
            CardSidePlayable resource2 = new CardSidePlayable(
                    norequirements,
                    Arrays.asList(new Corner(Resource.FUNGUS), new Corner(), new Corner(Resource.FUNGUS), new Corner(Resource.FUNGUS)),
                    emptyarr,
                    new PointsInstant(1),
                    Color.FUNGUS
            );

            Field f = new Field();
            f.initStartCard(starter_card);
            f.playCardSide(starter, new Coordinates(0,0));
            f.playCardSide(resource1, new Coordinates(1, 1));
            f.playCardSide(resource2, new Coordinates(1, -1));

            assert(f.getCardAtCoord(new Coordinates(0,0)) == starter);
            assert(f.getCardAtCoord(new Coordinates(1,1)) == resource1);
            assert(f.getCardAtCoord(new Coordinates(1,-1)) == resource2);
            assert(f.getCardAtCoord(new Coordinates(-99,-99)) == null);
        } catch (VariableAlreadySetException | InvalidPlayCardException | InvalidCoordinatesException |
                 RequirementsNotMetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetRoot() {
        try {
            CardSidePlayable starter = new CardSidePlayable(
                    norequirements,
                    Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                    Arrays.asList(Resource.PLANT, Resource.PLANT),
                    new PointsInstant(0),
                    Color.NO_COLOR
            );
            CardStarter starter_card = new CardStarter("starter001", starter, starter);
            CardSidePlayable resource1 = new CardSidePlayable(
                    norequirements,
                    Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                    emptyarr,
                    new PointsInstant(0),
                    Color.PLANT
            );
            CardSidePlayable resource2 = new CardSidePlayable(
                    norequirements,
                    Arrays.asList(new Corner(Resource.FUNGUS), new Corner(), new Corner(Resource.FUNGUS), new Corner(Resource.FUNGUS)),
                    emptyarr,
                    new PointsInstant(1),
                    Color.FUNGUS
            );

            Field f = new Field();
            f.initStartCard(starter_card);
            f.playCardSide(starter, new Coordinates(0,0));
            f.playCardSide(resource1, new Coordinates(1, 1));
            f.playCardSide(resource2, new Coordinates(1, -1));

            assertSame(f.getRoot(), starter);
        } catch (VariableAlreadySetException | InvalidPlayCardException | RequirementsNotMetException |
                 InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetAvailableCoord() throws InvalidCoordinatesException, RequirementsNotMetException, InvalidPlayCardException, VariableAlreadySetException {
        CardSidePlayable starter = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.PLANT),
                new PointsInstant(0),
                Color.NO_COLOR
        );
        CardStarter starter_card = new CardStarter("starter001", starter, starter);
        CardSidePlayable resource1 = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                emptyarr,
                new PointsInstant(0),
                Color.PLANT
        );
        CardSidePlayable resource2 = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.FUNGUS), new Corner(), new Corner(Resource.FUNGUS), new Corner(Resource.FUNGUS)),
                emptyarr,
                new PointsInstant(1),
                Color.FUNGUS
        );

        Field f = new Field();
        f.initStartCard(starter_card);
        f.playCardSide(starter, new Coordinates(0,0));
        f.playCardSide(resource1, new Coordinates(1, 1));
        f.playCardSide(resource2, new Coordinates(1, -1));

        List<Coordinates> avbCoords = f.getAvailableCoord();

//        List<Coordinates> correctCoords = new ArrayList<>();
//        correctCoords.add(new Coordinates(0, 2));
//        correctCoords.add(new Coordinates(2, 2));
//        correctCoords.add(new Coordinates(-1, 1));
//        correctCoords.add(new Coordinates(-1, -1));
//        correctCoords.add(new Coordinates(0, -2));
//        correctCoords.add(new Coordinates(2, 0));
//        correctCoords.add(new Coordinates(2, -2));

        assertEquals(6, avbCoords.size());
    }

    @Test
    public void testCountResources() {
        CardSidePlayable starter_clear = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE)),
                List.of(),
                new PointsInstant(0),
                Color.NO_COLOR
        );
        CardStarter starter_clear_card = new CardStarter("starter001", starter_clear, starter_clear);
        CardSidePlayable resource1 = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                emptyarr,
                new PointsInstant(0),
                Color.PLANT
        );


        try {
            Field field = new Field();
            field.initStartCard(starter_clear_card);
            field.playCardSide(starter_clear, new Coordinates(0,0));

            field.playCardSide(
                    new CardSidePlayable(
                        norequirements,
                        Arrays.asList(new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner()),
                            List.of(Resource.ANIMAL),
                        new PointsInstant(0),
                        Color.ANIMAL
                    ),
                    new Coordinates(1,1)
            );


            field.playCardSide(
                    new CardSidePlayable(
                            norequirements,
                            Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner()),
                            Arrays.asList(Resource.ANIMAL, Resource.PLANT),
                            null,
                            Color.ANIMAL
                    ),
                    new Coordinates(-1,-1)
            );

            Map<Resource, Integer> freqs = field.getResourcesInField();

            // check if initialization worked fine
            assertTrue(freqs.containsKey(Resource.PLANT));
            assertTrue(freqs.containsKey(Resource.ANIMAL));
            assertTrue(freqs.containsKey(Resource.FUNGUS));
            assertTrue(freqs.containsKey(Resource.INSECT));
            assertTrue(freqs.containsKey(Resource.QUILL));
            assertTrue(freqs.containsKey(Resource.INKWELL));
            assertTrue(freqs.containsKey(Resource.MANUSCRIPT));
            assertTrue(freqs.containsKey(Resource.NO_RESOURCE));

            assertEquals(7,freqs.get(Resource.ANIMAL));
            assertEquals(2,freqs.get(Resource.PLANT));

            // cover 'ur' corner of card in (1,1) => resources modified
            field.playCardSide(resource1, new Coordinates(2,2));
            freqs = field.getResourcesInField();
            assertEquals(6,freqs.get(Resource.ANIMAL));
            assertEquals(5,freqs.get(Resource.PLANT));

        } catch (RequirementsNotMetException | InvalidCoordinatesException | InvalidPlayCardException |
                 VariableAlreadySetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCountResources2() {
        try {
            Field field = new Field();
            CardSidePlayable startSide = new CardSidePlayable(
                    norequirements,
                    Arrays.asList(new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE)),
                    List.of(),
                    new PointsInstant(0),
                    Color.NO_COLOR
            );
            field.initStartCard(new CardStarter("s001f", startSide, startSide));
            field.playCardSide(startSide, new Coordinates(0,0)
            );
            assertEquals(0, field. getResourcesInField().get(Resource.PLANT));
            assertEquals(0, field.getResourcesInField().get(Resource.ANIMAL));

            field.playCardSide(
                    new CardSidePlayable(
                            new HashMap<>(),
                            Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE), new Corner(Resource.PLANT), new Corner(Resource.PLANT)),
                            List.of(Resource.PLANT),
                            new PointsInstant(0),
                            Color.NO_COLOR
                    ), new Coordinates(1,1)
            );
            assertEquals(4, field. getResourcesInField().get(Resource.PLANT));
            assertEquals(0, field.getResourcesInField().get(Resource.ANIMAL));

            field.playCardSide(
                    new CardSidePlayable(
                            new HashMap<>(),
                            Arrays.asList(new Corner(Resource.ANIMAL), new Corner(Resource.NO_RESOURCE), new Corner(Resource.ANIMAL), new Corner(Resource.NO_RESOURCE)),
                            List.of(Resource.ANIMAL),
                            new PointsInstant(0),
                            Color.NO_COLOR
                    ), new Coordinates(2,2)
            );
            assertEquals(4, field. getResourcesInField().get(Resource.PLANT));
            assertEquals(3, field.getResourcesInField().get(Resource.ANIMAL));

            field.playCardSide(
                    new CardSidePlayable(
                        new HashMap<>(),
                        Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE), new Corner(Resource.PLANT), new Corner(Resource.PLANT)),
                        List.of(Resource.PLANT),
                        new PointsInstant(0),
                        Color.NO_COLOR
                    ), new Coordinates(3,3)
            );
            assertEquals(8, field. getResourcesInField().get(Resource.PLANT));
            assertEquals(3, field.getResourcesInField().get(Resource.ANIMAL));

            field.playCardSide(
                    new CardSidePlayable(
                            new HashMap<>(),
                            Arrays.asList(new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE)),
                            List.of(Resource.NO_RESOURCE),
                            new PointsInstant(0),
                            Color.NO_COLOR
                    ), new Coordinates(3,1)
            );      // I should cover 1 animal
            assertEquals(8, field. getResourcesInField().get(Resource.PLANT));
            assertEquals(2, field.getResourcesInField().get(Resource.ANIMAL));
        } catch (InvalidPlayCardException | RequirementsNotMetException | InvalidCoordinatesException |
                 VariableAlreadySetException e) {
            throw new RuntimeException(e);
        }
    }

}
