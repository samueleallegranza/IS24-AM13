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
     * List of parameters needed, in form of text.
     * The list can be used to check how many parameters must be present for this command
     * (optional parameters are not supported)
     */
    private final List<String> parameterList;
    /**
     * Action corresponding to the command. The argument is the possible list of arguments for that command
     */
    private final Consumer<List<String>> action;

    public PromptCommand(String command, String description, Consumer<List<String>> action, String ... params) {
        this.command = "--" + command;
        this.description = description;
        this.action = action;
        this.parameterList = List.of(params);
    }

    /**
     * @return Command key from the command line (comprehensive of the '--')
     */
    public String getCommand() {
        return command;
    }

    /**
     * @return List of parameters needed, in form of text.
     * The list can be used to check how many parameters must be present for this command
     */
    public List<String> getParameterList() {
        return parameterList;
    }

    public void executeAction(List<String> args) throws IllegalArgumentException {
        if(args.size() != parameterList.size())
            throw new IllegalArgumentException();
        action.accept(args);
    }

    /**
     * @return A formatted string representing the command
     */
    public String generateHelpString() {
        StringBuilder sb = new StringBuilder(command);
        for(String param : parameterList)
            sb.append(" <").append(param).append(">");
        return String.format("%-35s: %s\n", sb, description);
    }
}