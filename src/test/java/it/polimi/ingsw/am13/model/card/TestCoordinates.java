package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.exceptions.InvalidCoordinatesException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TestCoordinates {

    @Test
    void testAdd() {
        Coordinates res;
        try {
            res = new Coordinates(0,2).add(new Coordinates(-1,3));
            assertEquals(-1, res.getPosX());
            assertEquals(5, res.getPosY());

            res = new Coordinates(0,2).add(-1,3);
            assertEquals(-1, res.getPosX());
            assertEquals(5, res.getPosY());
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testFetchNearCoordinates() {
        try {
            Set<Coordinates> nears = new Coordinates(0,0).fetchNearCoordinates();
            assertEquals(4, nears.size());
            assertTrue(nears.contains(new Coordinates(-1,1)));
            assertTrue(nears.contains(new Coordinates(1,1)));
            assertTrue(nears.contains(new Coordinates(-1,-1)));
            assertTrue(nears.contains(new Coordinates(1,-1)));
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testHasCoords() {
        try {
            Coordinates c = new Coordinates(1,-9);
            assertTrue(c.hasCoords(1,-9));
            assertFalse(c.hasCoords(0,-9));
            assertFalse(c.hasCoords(1,9));
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testEquals() {
        try {
            Coordinates c1 = new Coordinates(1,5);
            Coordinates c2 = new Coordinates(1,5);
            assertEquals(c1, c2);
            assertEquals(c2, c1);
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }
}