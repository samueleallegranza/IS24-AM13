package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.view.View;
import it.polimi.ingsw.am13.client.view.tui.menu.*;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;

/**
 * Implementation of a view for TUI.
 * It stores and handles the flow of the game (the "show" updates it expects to receive)
 */
public class ViewTUI implements View {

    //TODO finisci di implementare (da showPlayedCard in giù)

    //TODO finisci di documentare

    // TODO non mi faceva giocare una carta con requisiti, anche se i requisiti erano soddisfatti.
    //  Indaga meglio...

    /**
     * Current menu, which defines the possible actions to perform
     */
    private MenuTUI currentMenu;

    /**
     * Player the view refers to. Null if it is not define yet
     */
    private PlayerLobby thisPlayer;

    /**
     * Game's state. Null if it is not defined yet
     */
    private GameState gameState;

    /**
     * Handler of the view for the turn-based phases. Null if it is not defined
     */
    private ViewTUIMatch viewTUIMatch;

    /**
     * Log object to store logs displayed in the Log Section of the Match visualization
     */
    private Log logs;

    /**
     * Builds a new TUI view, setting an empty menu and all state attributes to null (not yet set)
     */
    public ViewTUI() {
        this.currentMenu = new MenuTUI("");
        this.thisPlayer = null;
        this.gameState = null;
        this.viewTUIMatch = null;
        this.logs = null;
    }

    /**
     * Creates a new menu with the specified items, sets this as the new current menu and prints it
     * @param items Items to be included in the new current menu
     */
    private void changeAndPrintMenu(String request, MenuItem ... items) {
        this.currentMenu = new MenuTUI(request, items);
        this.currentMenu.printMenu();
    }

    /**
     * Starts the thread defined by {@link MenuInputReader} (listening for input from stdin)
     * @param networkHandler Handler of the network, with which the commands can be sent to the server
     */
    @Override
    public void setNetworkHandler(NetworkHandler networkHandler) {
        MenuInputReader inputReader = new MenuInputReader(this, networkHandler);
        inputReader.start();
    }

    /**
     * Shows a startup screen
     * @param isSocket If the chosen connection is socket (vs RMI)
     * @param ip Ip of the client
     * @param port Port of the client
     */
    @Override
    public void showStartupScreen(boolean isSocket, String ip, int port) {
        ViewTUIPrintUtils.printStartup(isSocket, ip, port);
    }

    /**
     * Prints a generic exception and prints again the current menu
     * @param e Exception to be shown
     */
    @Override
    public void showException(Exception e) {
        if(e.getMessage() != null)
            System.out.println(e.getMessage());
        else
            System.out.println(e.getClass().getName());
        currentMenu.printMenu();
        //TODO basta così?
    }

    @Override
    public void showGenericLogMessage(String msg) {
        System.out.println(msg);
        //TODO basta così?
    }

    /**
     * Prints the list of all rooms (with game started or not started)
     * @param rooms List of rooms
     */
    @Override
    public void showRooms(List<RoomIF> rooms) {
        if (rooms.isEmpty())
            System.out.println("There no rooms for you to join right now!");
        else{
            String table = String.format(
                    """
                            ╔══════════════════════════════════════[▽ AVAILABLE ROOMS ▽]══════════════════════════════════════╗
                            ║ %-7s │ %-7s │ %-15s │ %-15s │ %-15s │ %-15s │ %-3s ║
                            """,
                    "GAME ID", "STATUS", "Player1", "Player2", "Player3", "Player4", "#NP");
            System.out.print(table);
            for (RoomIF r : rooms) {
                String[] p = {"X","X","X","X"};
                for (int i = 0; i < r.getnPlayersTarget(); i++)
                    if (i < r.getPlayers().size())
                        p[i] = r.getPlayers().get(i).getNickname();
                    else
                        p[i]="-";
                String formatted = String.format(
                        "║ %-7s │ %-7s │ %-15s │ %-15s │ %-15s │ %-15s │ %-3s ║\n",
                        r.getGameId(), r.isGameStarted() ? "started" : "waiting",
                        p[0], p[1], p[2], p[3], r.getPlayers().size() +"/"+ r.getnPlayersTarget()
                );
                for (int i = 0; i < r.getPlayers().size(); i++)
                    formatted = formatted.replace(p[i], ViewTUIConstants.colorNickname(r.getPlayers().get(i)));
                System.out.print(formatted);
            }
            System.out.println(
                    "╚═════════════════════════════════════════════════════════════════════════════════════════════════╝");
        }

        changeAndPrintMenu("", new MenuItemUpdateRoomList(),
                new MenuItemCreateRoom(),
                new MenuItemJoinRoom(),
                new MenuItemReconnect()
        );
    }

    /**
     * Prints a player joining the room.
     * If the player for this was not set, it is assumed this is the ACK message from the server and handles it.
     * Otherwise, it shows the update
     * @param player Player who joined the room
     */
    @Override
    public void showPlayerJoinedRoom(PlayerLobby player) {
        if(thisPlayer==null) {
            // I joined a room
            thisPlayer = player;
            System.out.println("You have joined the room");
            changeAndPrintMenu("", new MenuItemLeaveRoom());
        } else {
            // I already joined a room, someone else joined the same room
            System.out.println("Player " + player + " joined the room");

        }
    }

    /**
     * Prints a player leaving the room
     * If the player is the one associated to the view, it is assumed that this is the ACK message from the server and handles it.
     * Otherwise, it shows the update
     * @param player Player who left the room
     */
    @Override
    public void showPlayerLeftRoom(PlayerLobby player) {
        // TODO se lo chiamo quando il gioco è avviato, mi perdo l'informazione su thisPlayer e mando a quel paese il menu.
        //  E' una situazione da gestire?

        if(thisPlayer.equals(player)) {
            // I left the room
            System.out.println("You left the room");
            thisPlayer = null;
            changeAndPrintMenu("", new MenuItemUpdateRoomList(),
                    new MenuItemCreateRoom(),
                    new MenuItemJoinRoom());
        } else {
            // Someone who was in my same room left it
            System.out.println("Player " + player + " left the room");
        }
    }

    /**
     * Shows the game starting.
     * In particular prints the two sides of the starter card and sets the meny in order to play the starter
     * @param gameState Reference to the game's state which is kept up to date
     */
    @Override
    public void showStartGame(GameState gameState) {
        // TODO cosa fare se gameState è già impostato? (sarebbe stato già chiamato startgame...)
        //  E' una situazione da gestire (tipo player left room)?
        this.gameState = gameState;

        // initialize log
        this.logs = new Log(gameState);

        // print
        ViewTUIConstants.clearScreen();
        System.out.print("Game started.\n");
        changeAndPrintMenu(
                ViewTUIPrintUtils.starterCards(gameState.getPlayerState(thisPlayer).getStarterCard()) +
                "\nPlease choose the side of your starter card:",
                new MenuItemPlayStarter()
        );
    }

    @Override
    public void showStartGameReconnected(GameState state, PlayerLobby thisPlayer) {
        this.gameState = state;
        this.thisPlayer = thisPlayer;

        // re-initialize view
        this.viewTUIMatch = new ViewTUIMatch(this, this.gameState, this.thisPlayer);

        // re-initialize log
        this.logs = new Log(gameState);
        this.viewTUIMatch.setLogs(this.logs);
        this.logs.logReconnect(thisPlayer);

        viewTUIMatch.setDisplayPlayer(this.thisPlayer);
        viewTUIMatch.printMatch();
    }

    /**
     * It shows a successful starter card played.
     * If the player is not the one associated to the view, it shows the update.
     * Otherwise, it shows the two possible objective card and sets the menu in order to choose between them.
     * @param player Player who played their starter card
     */
    @Override
    public void showPlayedStarter(PlayerLobby player) {
        this.logs.logPlayedStarter(player);

        if(thisPlayer.equals(player)) {
            CardObjectiveIF obj1 = this.gameState.getPlayerState(this.thisPlayer).getPossibleHandObjectives().getFirst();
            CardObjectiveIF obj2 = this.gameState.getPlayerState(this.thisPlayer).getPossibleHandObjectives().getLast();

            this.currentMenu = new MenuTUI(
                    ViewTUIPrintUtils.objectiveCards(obj1, obj2)+
                    "\nPlease choose your personal objective card:",
                    new MenuItemChooseObj(gameState)
            ) ;
        }

        ViewTUIConstants.clearScreen();
        for(String log: this.logs.getLogMessages()) System.out.println(log);
        currentMenu.printMenu();
    }

    /**
     * It shows a successful objective card chosen.
     * If the player is the one associated to the view, is also updated the menu
     * @param player Player who chose their personal objective card
     */
    @Override
    public void showChosenPersonalObjective(PlayerLobby player) {
        this.logs.logChosenPersonalObjective(player);

        if(thisPlayer.equals(player)) {
            this.currentMenu = new MenuTUI("Wait for the other player to finish their initialization.");
        }

        ViewTUIConstants.clearScreen();
        for(String log: this.logs.getLogMessages()) System.out.println(log);
        currentMenu.printMenu();
    }

    /**
     * It shows the game entering the turn-based phase
     * In particular sets the {@link ViewTUIMatch} and uses that to handle this phase.
     */
    @Override
    public void showInGame() {
        // TODO non mi dovrebbero arrivare + showInGame, ma se succedesse?
        viewTUIMatch = new ViewTUIMatch(this, gameState, thisPlayer);
        this.viewTUIMatch.setLogs(this.logs);

        viewTUIMatch.setDisplayPlayer(thisPlayer);
        viewTUIMatch.printMatch();
    }

    @Override
    public void showPlayedCard(PlayerLobby player, Coordinates coord) {
        this.logs.logPlayedCard(player, coord);
        viewTUIMatch.setFlowCardPlaced(true);
        viewTUIMatch.printMatch();
    }

    @Override
    public void showPickedCard(PlayerLobby player) {
        this.logs.logPickedCard(player);
        viewTUIMatch.setFlowCardPlaced(false);
        viewTUIMatch.printMatch();
    }

    @Override
    public void showNextTurn() {
        System.out.println("Now it the turn of " + gameState.getCurrentPlayer());
        if(thisPlayer.equals(gameState.getCurrentPlayer()))
            viewTUIMatch.setDisplayPlayer(thisPlayer);
        viewTUIMatch.printMatch();
    }

    @Override
    public void showFinalPhase() {
        this.logs.logFinalPhase();
        viewTUIMatch.printMatch();
    }

    @Override
    public void showUpdatePoints() {
        System.out.println("Final points:");
        for(PlayerLobby p : gameState.getPlayers()) {
            System.out.println("\t" + p.getNickname() + "\t- " +
                    gameState.getPlayerState(p).getPoints() + " points");
        }
    }

    @Override
    public void showWinner() {
        this.logs.logWinner();
        System.out.println(gameState.getWinner() + " has won!!!");
    }

    @Override
    public void showEndGame() {
        System.out.println("The game has ended");
        // TODO aggiungi networkHandler che chiama getRooms
    }

    @Override
    public void showPlayerDisconnected(PlayerLobby player) {
        this.logs.logDisconnect(player);
        viewTUIMatch.printMatch();
    }

    @Override
    public void showPlayerReconnected(PlayerLobby player) {
        this.logs.logReconnect(player);
        viewTUIMatch.printMatch();
    }

    public MenuTUI getCurrentMenu() {
        return currentMenu;
    }

    void setCurrentMenu(MenuTUI currentMenu) {
        this.currentMenu = currentMenu;
    }
}
