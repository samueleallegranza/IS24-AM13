package it.polimi.ingsw.am13.model.card;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.am13.model.exceptions.InvalidCoordinatesException;

import java.util.List;

public class TestCoordinates {

    @Test
    public void testAdd() {
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
    public void testCreateInvalid() {
        assertThrows(InvalidCoordinatesException.class, () -> new Coordinates(1,2) );
    }

    @Test
    public void testFetchNearCoordinates() {
        try {
            List<Coordinates> nears = new Coordinates(0,0).fetchNearCoordinates();
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
    public void testHasCoords() {
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
    public void testEquals() {
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