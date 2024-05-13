package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.view.tui.menu.MenuItem;

import java.util.Scanner;

public class MenuInputReader extends Thread {
    ViewTUI view;
    private Scanner scanner;

    public MenuInputReader(ViewTUI view) {
        this.view = view;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        while (true) {
            String input = scanner.nextLine();

            synchronized (view) {
                // Get commandKey from input
                MenuItem menuItem = view.getCurrentMenu().get(input.substring(0, input.indexOf(" ")));
                if (menuItem != null) {
                    // Get args from input
                    menuItem.executeCommand(input.substring(input.indexOf(" ") + 1));
                } else {
                    System.out.println("Invalid command. Please try again.");
                }
            }
        }

    }
}
