package it.polimi.ingsw.am13.model.player;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.card.points.PointsInstant;
import it.polimi.ingsw.am13.model.card.points.PointsResource;
import it.polimi.ingsw.am13.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayCardException;
import it.polimi.ingsw.am13.model.exceptions.RequirementsNotMetException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestField {

    ArrayList<Resource> emptyarr = new ArrayList<Resource>();
    HashMap<Resource, Integer> norequirements = new HashMap<Resource, Integer>();

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

    @Test
    public void testGetCardAtCoord() throws InvalidCoordinatesException, RequirementsNotMetException, InvalidPlayCardException {
        Field f = new Field(starter_card);
        f.playCardSide(starter, new Coordinates(0,0));
        f.playCardSide(resource1, new Coordinates(1, 1));
        f.playCardSide(resource2, new Coordinates(1, -1));

        assert(f.getCardAtCoord(new Coordinates(0,0)) == starter);
        assert(f.getCardAtCoord(new Coordinates(1,1)) == resource1);
        assert(f.getCardAtCoord(new Coordinates(1,-1)) == resource2);
        assert(f.getCardAtCoord(new Coordinates(-99,-99)) == null);
    }

    @Test
    public void testGetRoot() throws InvalidCoordinatesException, RequirementsNotMetException, InvalidPlayCardException {
        Field f = new Field(starter_card);
        f.playCardSide(starter, new Coordinates(0,0));
        f.playCardSide(resource1, new Coordinates(1, 1));
        f.playCardSide(resource2, new Coordinates(1, -1));

        assert(f.getRoot() == starter);
    }

    @Test
    void testGetAvailableCoord() throws InvalidCoordinatesException, RequirementsNotMetException, InvalidPlayCardException {
        Field f = new Field(starter_card);
        f.playCardSide(starter, new Coordinates(0,0));
        f.playCardSide(resource1, new Coordinates(1, 1));
        f.playCardSide(resource2, new Coordinates(1, -1));

        ArrayList<Coordinates> avbCoords = f.getAvailableCoord();

//        ArrayList<Coordinates> correctCoords = new ArrayList<>();
//        correctCoords.add(new Coordinates(0, 2));
//        correctCoords.add(new Coordinates(2, 2));
//        correctCoords.add(new Coordinates(-1, 1));
//        correctCoords.add(new Coordinates(-1, -1));
//        correctCoords.add(new Coordinates(0, -2));
//        correctCoords.add(new Coordinates(2, 0));
//        correctCoords.add(new Coordinates(2, -2));

        assert(avbCoords.size() == 6);
    }

    @Test
    public void testCountResources() {
        CardSidePlayable starter_clear = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(),
                new PointsInstant(0),
                Color.NO_COLOR
        );
        CardStarter starter_clear_card = new CardStarter("starter001", starter_clear, starter_clear);


        try {
            Field field = new Field(starter_clear_card);
            field.playCardSide(starter_clear, new Coordinates(0,0));

            field.playCardSide(
                    new CardSidePlayable(
                        norequirements,
                        Arrays.asList(new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner(Resource.ANIMAL), new Corner()),
                        Arrays.asList(Resource.ANIMAL),
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

            Map<Resource, Integer> freqs = field.countResources();

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
            freqs = field.countResources();
            assertEquals(6,freqs.get(Resource.ANIMAL));
            assertEquals(5,freqs.get(Resource.PLANT));

        } catch (RequirementsNotMetException | InvalidCoordinatesException | InvalidPlayCardException e) {
            throw new RuntimeException(e);
        }
    }

}
