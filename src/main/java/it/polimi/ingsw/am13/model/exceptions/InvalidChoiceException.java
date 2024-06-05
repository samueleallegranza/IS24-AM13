package it.polimi.ingsw.am13.model.exceptions;

/**
 * Thrown after an attempt to set a variable to a value that does not belong to the set of allowed choices
 */
public class InvalidChoiceException extends ModelException{
    public InvalidChoiceException() {
        super();
    }

}
