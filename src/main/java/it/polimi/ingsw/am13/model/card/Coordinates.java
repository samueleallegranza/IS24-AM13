package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.exceptions.InvalidCoordinatesException;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a 2-tuple of coordinates for a 2D point or vector that could be used in game.
 * For how game works, coordinates (x, y) are valid only if (x+y)%2=0 (their sum must be a multiple of 2).
 * The object, once instantiated, is immutable.
 */
public class Coordinates implements Serializable {

    /**
     * Coordinate x of point/vector (horizontal axis)
     */
    private final int posX;
    /**
     * Coordinate y of point/vector (vertical axis)
     */
    private final int posY;

    /**
     * Creates an immutable valid object Coordinates. The sum of parameters must be a multiple of 2
     * @param posX Coordinate x of point/vector (horizontal ax)
     * @param posY Coordinate y of point/vector (vertical axis)
     * @throws InvalidCoordinatesException If posX+posY is not a multiple of 2 (game coordinates are not valid)
     */
    public Coordinates(int posX, int posY) throws InvalidCoordinatesException {
        if((posX+posY)%2!=0)
            throw new InvalidCoordinatesException();
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Create new coordinates for origin, without throwing exceptions
     * @return Coordinates for (0,0)
     */
    static public Coordinates origin() {
        try {
            return new Coordinates(0,0);
        } catch (InvalidCoordinatesException e) {
            // Cant ever happen
            throw new RuntimeException(e);
        }
    }

    /**
     * @return Coordinate x of point/vector (horizontal axis)
     */
    public int getPosX() {
        return posX;
    }

    /**
     * @return Coordinate y of point/vector (vertical axis)
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Performs arithmetic sum of coordinates
     * @param other Other coordinates to be summed
     * @return New object Coordinates with result of sum
     */
    public Coordinates add(Coordinates other) {
        try {
            return new Coordinates(posX+other.posX, posY+other.posY);
        } catch (InvalidCoordinatesException e) {
            // It can never happen, as both Coordinates are already created, hence they are valid
            throw new RuntimeException(e);
        }
    }

    /**
     * Performs arithmetic sum of coordinates
     * @param posX Other x coordinate to be summed
     * @param posY Other y coordinate to be summed
     * @return New object Coordinates with result of sum
     * @throws InvalidCoordinatesException If, performing the sum, the new coordinates are not valid
     */
    public Coordinates add(int posX, int posY) throws InvalidCoordinatesException {
        return new Coordinates(this.posX+posX, this.posY+posY);
    }

    /**
     * Creates the ordered list of 4 coordinates that are near this coordinate in game field.
     * The 'near coordinates' are the ones where a new card could be placed with respect to a card in these coordinates.
     * @return List of the 4 near coordinates, in order 'upper left', 'upper right', 'lower right', 'lower left'
     */
    public List<Coordinates> fetchNearCoordinates() {
        try {
            return new ArrayList<>(Arrays.asList(this.add(-1,1), this.add(1,1),
                    this.add(1,-1), this.add(-1,-1)));
        } catch (InvalidCoordinatesException e) {
            // It can never happen
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks equality with indicated coordinates
     * @param posX x coordinate to be checked
     * @param posY y coordinate to be checked
     * @return <code>true</code> if these coordinates are equal to the respective coordinates passed as parameter
     */
    public boolean hasCoords(int posX, int posY) {
        return this.posX==posX && this.posY==posY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return posX == that.posX && posY == that.posY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(posX, posY);
    }

    @Override
    public String toString() {
        return "{coord (" + posX + ", " + posY + ")";
    }
}
