package it.polimi.ingsw.am13.client.view.tui.menu;

/**
 * Exception representing invalid set of parameters associated to a TUI command
 */
public class InvalidTUICommandException extends Exception {

    public InvalidTUICommandException(String message) {
        super(message);
    }
}
