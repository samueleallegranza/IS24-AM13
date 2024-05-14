package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.view.tui.menu.MenuItem;
import it.polimi.ingsw.am13.client.view.tui.menu.MenuItemChooseObj;
import it.polimi.ingsw.am13.client.view.tui.menu.MenuItemPlayStarter;
import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardSidePlayableIF;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewTUIOpening implements ViewTuiMenuDisplayer {
    private GameState gameState;
    private PlayerLobby thisPlayer;

    private Map<String, MenuItem> menu;

    public ViewTUIOpening() {
        this.gameState = null;
        this.thisPlayer = null;
        this.menu = new HashMap<>();
    }

    @Override
    public Map<String, MenuItem> getMenu() {
        return this.menu;
    }

    public void printStarterSelection(GameState state, PlayerLobby player) {
        // set new game state
        this.gameState = state;
        this.thisPlayer = player;

        System.out.print("Game started.\n");
        // TODO: How to handle looping until correct ??
        System.out.print("Please choose the side of your starter card: \n");
        System.out.print(this.starterCards());

        // set menu for starter selection
        Map<String, MenuItem> newMenu = new HashMap<>();
        // menu options:
        MenuItem menuItemPlayStarter = new MenuItemPlayStarter();
        // insert options inside menu
        newMenu.put(menuItemPlayStarter.getCommandKey(), menuItemPlayStarter);
        // update local menu
        this.menu = newMenu;
        // display menu
        System.out.println("Menu:\n");
        for(String k: newMenu.keySet()) System.out.println(newMenu.get(k).toString());
    }

    public void printObjectiveSelection() {
        System.out.print("Please choose your personal objective card: \n");
        System.out.print(this.objectiveCards());

        // set menu for objective selection
        Map<String, MenuItem> newMenu = new HashMap<>();
        // menu options:
        MenuItem menuItemChooseObj = new MenuItemChooseObj(null, gameState); // FIXME: why do I have to pass a networkHandler?!>!|@!!?.?
        // insert options inside menu
        newMenu.put(menuItemChooseObj.getCommandKey(), menuItemChooseObj);
        // update local menu
        this.menu = newMenu;
        // display menu
        System.out.println("Menu:\n");
        for(String k: newMenu.keySet()) System.out.println(newMenu.get(k).toString());
    }



    // --------------------------------------------------------------------
    // ------------------------------ UTILS -------------------------------
    // --------------------------------------------------------------------

    public String starterCards() {
        // get cars
        CardSidePlayableIF cardFront = gameState.getPlayerState(thisPlayer).getStarterCard().getSide(Side.SIDEFRONT);
        CardSidePlayableIF cardBack = gameState.getPlayerState(thisPlayer).getStarterCard().getSide(Side.SIDEBACK);

        // create an array of resource symbols for corners
        List<String> frontCorners = cardFront.getCornerResources().stream().map(ViewTUIConstants::resourceToSymbol).toList();
        List<String> backCorners = cardBack.getCornerResources().stream().map(ViewTUIConstants::resourceToSymbol).toList();

        // create an array of resource symbols for center. Fill with spaces where needed.
        String[] frontCenter = {" ", " ", " "};
        List<String> frontCenterUnprocessed = cardFront.getCenterResources().stream().map(ViewTUIConstants::resourceToSymbol).toList();
        int copyLength = Math.min(3, frontCenterUnprocessed.size());
        for (int i = 0; i < copyLength; i++) frontCenter[i] = frontCenterUnprocessed.get(i);

        String[] backCenter = {" ", " ", " "};
        List<String> backCenterUnprocessed = cardBack.getCenterResources().stream().map(ViewTUIConstants::resourceToSymbol).toList();
        copyLength = Math.min(3, backCenterUnprocessed.size());
        for (int i = 0; i < copyLength; i++) backCenter[i] = backCenterUnprocessed.get(i);

        return String.format(
                "┌───┬───S───F───┬───┐     ┌───┬───S───B───┬───┐\n" +
                "│ %s │           │ %s │     │ %s │           │ %s │\n" +
                "├───┘   %s %s %s   └───┤     ├───┘   %s %s %s   └───┤\n" +
                "├───┐           ┌───┤     ├───┐           ┌───┤\n" +
                "│ %s │           │ %s │     │ %s │           │ %s │\n" +
                "└───┴───────────┴───┘     └───┴───────────┴───┘\n",
                frontCorners.get(0), frontCorners.get(1), backCorners.get(0), backCorners.get(1),
                frontCenter[0], frontCenter[1], frontCenter[2], backCenter[0], backCenter[1], backCenter[2],
                frontCorners.get(3), frontCorners.get(2), backCorners.get(3), backCorners.get(2)
        );
    }

    public String objectiveCards() {
        CardObjectiveIF obj1 = this.gameState.getPlayerState(this.thisPlayer).getPossibleHandObjectives().getFirst();
        CardObjectiveIF obj2 = this.gameState.getPlayerState(this.thisPlayer).getPossibleHandObjectives().getLast();

        // FIXME: Dont have access to CardObjectiveIF informations!
        return String.format(
                "┌─────OBJECTIVE─────┐     ┌─────OBJECTIVE─────┐\n" +
                "│                   │     │                   │\n" +
                "│         %s         │     │         %s         │\n" +
                "│                   │     │                   │\n" +
                "│                   │     │                   │\n" +
                "└───────────────────┘     └───────────────────┘\n",
                obj1.getId(),
                obj2.getId()
        );
    }

}
