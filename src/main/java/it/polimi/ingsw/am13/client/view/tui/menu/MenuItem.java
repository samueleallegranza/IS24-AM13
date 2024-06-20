package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;

/**
 * Item belonging to a menu. It stores the command key with which it is possible to invoke the command, and its description.
 * The focal point of this abstraction if the method which actually executes the command it represents.
 */
public abstract class MenuItem {

    /**
     * Command key with which it is possible to invoke the command via the command line
     */
    private final String commandKey;

    /**
     * Description of the command.
     * It should contain the list of arguments for the command to work, too
     */
    private final String description;

    /**
     * Build a new menu item
     * @param commandKey Command key with which it is possible to invoke the command
     * @param description Description of the command
     */
    public MenuItem(String commandKey, String description) {
        this.commandKey = commandKey;
        this.description = description;
    }

    /**
     * Executes the action this menu item represents
     * @param argsStr String of parameters for the command
     * @param networkHandler Handler of the network thanks to which the item sends the command to the server
     * @throws InvalidTUICommandException If the arguments passed via command line are wrong, or anyway different from what expected
     */
    public abstract void executeCommand(String argsStr, NetworkHandler networkHandler) throws InvalidTUICommandException;

    /**
     * @return Command key with which it is possible to invoke the command via the command line
     */
    public String getCommandKey() {
        return commandKey;
    }

    /**
     * @return Description of the command.
     * It should contain the list of arguments for the command to work, too
     */
    public String getDescription() {
        return description;
    }


    @Override
    public String toString() {
        return String.format("%s: %s", getCommandKey(), getDescription());
    }
}
