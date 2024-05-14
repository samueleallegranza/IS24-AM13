package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.view.tui.menu.InvalidTUIArgumentsException;
import it.polimi.ingsw.am13.client.view.tui.menu.MenuItem;

import java.util.Scanner;

public class MenuInputReader extends Thread {
    ViewTUI view;
    private final Scanner scanner;

    private final NetworkHandler networkHandler;

    public MenuInputReader(ViewTUI view, NetworkHandler networkHandler) {
        this.view = view;
        this.networkHandler = networkHandler;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            String input = scanner.nextLine();

            //TODO assicurati di questa synchronized
            synchronized (view) {
                // Get commandKey from input
                int endCommandKey = input.indexOf(" ");
                if(endCommandKey == -1)
                    endCommandKey = input.length();
                MenuItem menuItem = view.getCurrentMenu().get(input.substring(0, endCommandKey));
                if (menuItem != null) {
                    // Get args from input (first space excluded)
                    try {
                        menuItem.executeCommand(input.substring(input.indexOf(" ") + 1), networkHandler);
                    } catch (InvalidTUIArgumentsException e) {
                        view.showException(e);
                        //TODO forse da gestire meglio
                    }
                } else {
                    System.out.println("Invalid command. Please try again.");
                }
            }
        }

    }
}
