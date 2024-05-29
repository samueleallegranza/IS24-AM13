package it.polimi.ingsw.am13.model.player;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.card.points.PointsInstant;
import it.polimi.ingsw.am13.model.exceptions.*;
import org.junit.jupiter.api.Test;

import java.util.*;

public class TestField {

    private final ArrayList<Resource> emptyarr = new ArrayList<>();
    private final HashMap<Resource, Integer> norequirements = new HashMap<>();

    @Test
    public void testGetCardAtCoord() throws InvalidCardCreationException {
        try {
            CardSidePlayable starter = new CardSidePlayable(
                    norequirements,
                    Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                    Arrays.asList(Resource.PLANT, Resource.PLANT),
                    new PointsInstant(0),
                    Color.NO_COLOR,
                    "",
                    Side.SIDEFRONT
            );
            CardSidePlayable resource1 = new CardSidePlayable(
                    norequirements,
                    Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                    emptyarr,
                    new PointsInstant(0),
                    Color.PLANT,
                    "",
                    Side.SIDEFRONT
            );
            CardSidePlayable resource2 = new CardSidePlayable(
                    norequirements,
                    Arrays.asList(new Corner(Resource.FUNGUS), new Corner(), new Corner(Resource.FUNGUS), new Corner(Resource.FUNGUS)),
                    emptyarr,
                    new PointsInstant(1),
                    Color.FUNGUS,
                    "",
                    Side.SIDEFRONT
            );

            Field f = new Field();
            f.playCardSide(starter, new Coordinates(0,0));
            f.playCardSide(resource1, new Coordinates(1, 1));
            f.playCardSide(resource2, new Coordinates(1, -1));

            assert(f.getCardSideAtCoord(new Coordinates(0,0)) == starter);
            assert(f.getCardSideAtCoord(new Coordinates(1,1)) == resource1);
            assert(f.getCardSideAtCoord(new Coordinates(1,-1)) == resource2);
            assert(f.getCardSideAtCoord(new Coordinates(-99,-99)) == null);
        } catch (InvalidPlayCardException | InvalidCoordinatesException |
                 RequirementsNotMetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetRoot() throws InvalidCardCreationException {
        try {
            CardSidePlayable starter = new CardSidePlayable(
                    norequirements,
                    Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                    Arrays.asList(Resource.PLANT, Resource.PLANT),
                    new PointsInstant(0),
                    Color.NO_COLOR,
                    "",
                    Side.SIDEFRONT
            );
            CardSidePlayable resource1 = new CardSidePlayable(
                    norequirements,
                    Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                    emptyarr,
                    new PointsInstant(0),
                    Color.PLANT,
                    "",
                    Side.SIDEFRONT
            );
            CardSidePlayable resource2 = new CardSidePlayable(
                    norequirements,
                    Arrays.asList(new Corner(Resource.FUNGUS), new Corner(), new Corner(Resource.FUNGUS), new Corner(Resource.FUNGUS)),
                    emptyarr,
                    new PointsInstant(1),
                    Color.FUNGUS,
                    "",
                    Side.SIDEFRONT
            );

            Field f = new Field();
            f.playCardSide(starter, new Coordinates(0,0));
            f.playCardSide(resource1, new Coordinates(1, 1));
            f.playCardSide(resource2, new Coordinates(1, -1));

            assertSame(f.getRoot(), starter);
        } catch (InvalidPlayCardException | RequirementsNotMetException |
                 InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetAvailableCoord() throws InvalidCoordinatesException, RequirementsNotMetException, InvalidPlayCardException, InvalidCardCreationException {
        CardSidePlayable starter = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.PLANT),
                new PointsInstant(0),
                Color.NO_COLOR,
                "",
                Side.SIDEFRONT
        );
        CardSidePlayable resource1 = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                emptyarr,
                new PointsInstant(0),
                Color.PLANT,
                "",
                Side.SIDEFRONT
        );
        CardSidePlayable resource2 = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.FUNGUS), new Corner(), new Corner(Resource.FUNGUS), new Corner(Resource.FUNGUS)),
                emptyarr,
                new PointsInstant(1),
                Color.FUNGUS,
                "",
                Side.SIDEFRONT
        );

        Field f = new Field();
        f.playCardSide(starter, new Coordinates(0,0));
        f.playCardSide(resource1, new Coordinates(1, 1));
        f.playCardSide(resource2, new Coordinates(1, -1));

        List<Coordinates> avbCoords = f.getAvailableCoords();

        assertEquals(6, avbCoords.size());
    }

    @Test
    public void testCountResources() throws InvalidCardCreationException {
        CardSidePlayable starter_clear = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE)),
                List.of(),
                new PointsInstant(0),
                Color.NO_COLOR,
                "",
                Side.SIDEFRONT
        );
        CardSidePlayable resource1 = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                emptyarr,
                new PointsInstant(0),
                Color.PLANT,
                "",
                Side.SIDEFRONT
        );


        try {
            Field field = new Field();
            field.playCardSide(starter_clear, new Coordinates(0,0));

            field.playCardSide(
                    new CardSidePlayable(
                        norequirements,
                        Arrays.asList(new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner()),
                            List.of(Resource.ANIMAL),
                        new PointsInstant(0),
                        Color.ANIMAL,
                            "",
                            Side.SIDEFRONT
                    ),
                    new Coordinates(1,1)
            );


            field.playCardSide(
                    new CardSidePlayable(
                            norequirements,
                            Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner()),
                            Arrays.asList(Resource.ANIMAL, Resource.PLANT),
                            null,
                            Color.ANIMAL,
                            "",
                            Side.SIDEFRONT
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

        } catch (RequirementsNotMetException | InvalidCoordinatesException | InvalidPlayCardException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCountResources2() throws InvalidCardCreationException {
        try {
            Field field = new Field();
            CardSidePlayable startSide = new CardSidePlayable(
                    norequirements,
                    Arrays.asList(new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE)),
                    List.of(),
                    new PointsInstant(0),
                    Color.NO_COLOR,
                    "",
                    Side.SIDEFRONT
            );
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
                            Color.NO_COLOR,
                            "",
                            Side.SIDEFRONT
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
                            Color.NO_COLOR,
                            "",
                            Side.SIDEFRONT
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
                        Color.NO_COLOR,
                            "",
                            Side.SIDEFRONT
                    ), new Coordinates(3,3)
            );
            assertEquals(8, field. getResourcesInField().get(Resource.PLANT));
            assertEquals(3, field.getResourcesInField().get(Resource.ANIMAL));

            field.playCardSide(
                    new CardSidePlayable(
                            new HashMap<>(),
                            Arrays.asList(new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE)),
                            new ArrayList<>(),
                            new PointsInstant(0),
                            Color.NO_COLOR,
                            "",
                            Side.SIDEFRONT
                    ), new Coordinates(3,1)
            );      // I should cover 1 animal
            assertEquals(8, field. getResourcesInField().get(Resource.PLANT));
            assertEquals(2, field.getResourcesInField().get(Resource.ANIMAL));
        } catch (InvalidPlayCardException | RequirementsNotMetException | InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }

}
