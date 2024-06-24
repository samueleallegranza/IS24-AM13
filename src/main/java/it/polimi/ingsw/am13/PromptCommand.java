package it.polimi.ingsw.am13;

import java.util.List;
import java.util.function.Consumer;

/**
 * Representation of a command accepted from the command line
 */
public class PromptCommand {

    /**
     * Command key from the command line (comprehensive of the '--')
     */
    private final String command;
    /**
     * Short description of the command
     */
    private final String description;
    /**
     * Action corresponding to the command. The argument is the possible list of arguments for that command
     */
    private final Consumer<List<String>> action;

    public PromptCommand(String command, String description, Consumer<List<String>> action) {
        this.command = "--" + command;
        this.description = description;
        this.action = action;
    }

    /**
     * @return Command key from the command line (comprehensive of the '--')
     */
    public String getCommand() {
        return command;
    }

    /**
     * @return Short description of the command
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return Action corresponding to the command. The argument is the possible list of arguments for that command
     */
    public Consumer<List<String>> getAction() {
        return action;
    }
}