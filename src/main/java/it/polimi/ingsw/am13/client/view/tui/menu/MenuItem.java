package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

/**
 * Item belonging to a menu. It stores the command key with which it is possible to invoke the command, and its description.
 * It stores also the {@link NetworkHandler} thanks to which the item sends the command to the server
 * and the {@link PlayerLobby} it is associated to.
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
     * Handler of the network, which will be used to actually run the action associated to this menu item
     */
    protected NetworkHandler networkHandler;

    /**
     * Player associated to the view this menu item belongs to
     */
    protected PlayerLobby player;

    /**
     * Build a new menu item
     * @param commandKey Command key with which it is possible to invoke the command
     * @param description Description of the command
     * @param networkHandler Handler of the network, which will be used to actually run the action associated to this menu item
     */
    public MenuItem(String commandKey, String description, NetworkHandler networkHandler) {
        this.commandKey = commandKey;
        this.description = description;
        this.networkHandler = networkHandler;
        this.player = networkHandler.getPlayer();
    }

    /**
     * Executes the action this menu item represents
     * @param argsStr String of parameters for the command
     * @throws InvalidTUIArgumentsException If the arguments passad via command line are wrong, or anyway different from what expected
     */
    public abstract void executeCommand(String argsStr) throws InvalidTUIArgumentsException;

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
        //FIXME: format better
        return String.format("%s: %s", this.commandKey, this.description);
    }
}
