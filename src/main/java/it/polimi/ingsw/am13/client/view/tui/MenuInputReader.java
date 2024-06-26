package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.view.tui.menu.InvalidTUICommandException;
import it.polimi.ingsw.am13.client.view.tui.menu.MenuItem;

import java.util.Scanner;

/**
 * Class implementing the thread listening for command line inputs from the user, used for TUI view
 * Note that, being an object mediating between the server and the client, it has references to both the
 * {@link NetworkHandler} and the {@link ViewTUI}.<br>
 * Once started, it catches anything typed via command line after pressing enter.
 * It parses the first word of the typed line, interprets it as the command and uses the current menu exposed be the view
 * to try and execute the command, otherwise it throws an exception.
 * If the typed command is present in the menu, that {@link MenuItem} is executed, passing the rest of the parameters
 * typed after the command key to it.
 */
public class MenuInputReader extends Thread {

    /**
     * Scanner used to listen to the input from stdin
     */
    private final Scanner scanner;

    /**
     * Reference to the view of the client.
     * It should be used only to handle the input-parsing exceptions.
     */
    private final ViewTUI view;

    /**
     * Reference to the handler of the network, to send commands to the server
     */
    private final NetworkHandler networkHandler;

    /**
     * Builds a new listeners for the command line input from the user
     * @param view Reference of the view of the client
     * @param networkHandler Reference of the handler of the network
     */
    public MenuInputReader(ViewTUI view, NetworkHandler networkHandler) {
        this.view = view;
        this.networkHandler = networkHandler;
        this.scanner = new Scanner(System.in);
    }

    public NetworkHandler getNetworkHandler() {
        return networkHandler;
    }

    /**
     * Listen for command line inputs coming from the user, and execute them
     */
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            String input = scanner.nextLine();

            // check if thread has been interrupted. This happens when game is finished and we're waiting
            // for the player to close the application
            if(Thread.interrupted()) System.exit(0);

            synchronized (view) {
                // Get commandKey and commandArgs from input
                String commandKey, commandArgs;
                int endCommandKey = input.indexOf(" ");
                if(endCommandKey == -1) {
                    commandKey = input;
                    commandArgs = "";
                } else {
                    commandKey = input.substring(0, endCommandKey);
                    commandArgs = input.substring(endCommandKey + 1);
                }

                MenuItem menuItem = view.getCurrentMenu().getItem(commandKey);
                if (menuItem != null) {
                    // Get args from input (first space excluded)
                    try {
                        menuItem.executeCommand(commandArgs, networkHandler);
                    } catch (InvalidTUICommandException e) {
                        view.showException(e);
                    }
                } else {
                    view.showException(new InvalidTUICommandException("Invalid command. Please, try again"));
                }
            }
        }

    }
}
