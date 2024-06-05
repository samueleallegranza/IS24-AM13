package it.polimi.ingsw.am13.model.exceptions;

/**
 * Exception thrown when a setter of a variable that should only be set one time outside a constructor is called more than once
 */
public class VariableAlreadySetException extends ModelException{

    public VariableAlreadySetException() {
    }

}
