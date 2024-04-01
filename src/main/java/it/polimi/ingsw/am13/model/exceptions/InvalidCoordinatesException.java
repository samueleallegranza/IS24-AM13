package it.polimi.ingsw.am13.model.exceptions;

/**
 * Represents invalid game coordinates, that is coordinates whose sum is not a multiple of 2, or more generically
 * game coordinates used improperly during the game itself.
 */
public class InvalidCoordinatesException extends ModelException {
    public InvalidCoordinatesException() {
        super();
    }
}
