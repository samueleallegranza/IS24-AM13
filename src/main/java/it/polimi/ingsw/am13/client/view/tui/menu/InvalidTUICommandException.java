package it.polimi.ingsw.am13.client.view.tui.menu;

/**
 * Exception representing invalid set of parameters associated to a TUI command
 */
public class InvalidTUICommandException extends Exception {
    /**
     * Constructor of the exception.
     * @param message the message to be shown when the exception will be handled.
     */
    public InvalidTUICommandException(String message) {
        super(message);
    }
}
