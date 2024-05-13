package it.polimi.ingsw.am13.client.view.tui.menu;

/**
 * Exception representing invalid set of parameters associated to a TUI command
 */
public class InvalidTUIArgumentsException extends Exception {

    public InvalidTUIArgumentsException(String message) {
        super(message);
    }
}
