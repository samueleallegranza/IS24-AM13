package it.polimi.ingsw.am13.model.exceptions;

import java.io.Serializable;

/**
 * Main exception class for the Model package
 */
public class ModelException extends Exception implements Serializable {

    public ModelException() {
        super();
    }

    public ModelException(String msg) {
        super(msg);
    }

}
