package it.polimi.ingsw.am13.model.exceptions;

import java.io.Serializable;

/**
 * Main exception class for the Model package
 */
public class ModelException extends Exception implements Serializable {

    public ModelException() {
        super();
    }

    /**
     * Constructor of the exception.
     * @param msg the message to be shown when the exception will be handled.
     */
    public ModelException(String msg) {
        super(msg);
    }

}
