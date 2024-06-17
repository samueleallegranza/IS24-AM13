package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.ParametersClient;
import it.polimi.ingsw.am13.client.chat.Chat;
import it.polimi.ingsw.am13.client.chat.ChatMessage;
import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.view.View;
import it.polimi.ingsw.am13.client.view.tui.menu.*;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.GameStatus;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.model.player.Token;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a view for TUI.
 * It stores and handles the flow of the game (the "show" updates it expects to receive)
 */
public class ViewTUI implements View {

    //TODO finisci di documentare

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
    private LogTUI logs;

    /**
     * Thread which reads user input for menu selection
     */
    private MenuInputReader inputReader;

    /**
     * Chat. As gameState, it is handled outside of this class, and shouldn't be updated here
     */
    private Chat chat;

    /**
     * List of receivers defining the current shown chat room. Null is no chat room is currently shown
     */
    private List<PlayerLobby> currentChatRoom;

    /**
     * Builds a new TUI view, setting an empty menu and all state attributes to null (not yet set)
     */
    public ViewTUI() {
        this.currentMenu = new MenuTUI("");
        this.thisPlayer = null;
        this.gameState = null;
        this.viewTUIMatch = null;
        this.logs = null;
        this.inputReader = null;
        this.currentChatRoom = null;
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
        this.inputReader = new MenuInputReader(this, networkHandler);
        this.inputReader.start();
    }

    /**
     * Shows a startup screen
     * @param isSocket If the chosen connection is socket (vs RMI)
     * @param ip Ip of the client
     * @param port Port of the client
     */
    @Override
    public synchronized void showStartupScreen(boolean isSocket, String ip, int port) {
        ViewTUIPrintUtils.printStartup(isSocket, ip, port);
    }

    /**
     * Prints a generic exception and prints again the current menu
     * @param e Exception to be shown
     */
    @Override
    public synchronized void showException(Exception e) {
        if(e.getMessage() != null)
            System.out.println(e.getMessage());
        else
            System.out.println(e.getClass().getName());
        currentMenu.printMenu();
        //TODO basta così?
    }

    @Override
    public synchronized void showGenericLogMessage(String msg) {
        System.out.println(msg);
        //TODO basta così?
    }

    /**
     * Prints the list of all rooms (with game started or not started)
     * @param rooms List of rooms
     */
    @Override
    public synchronized void showRooms(List<RoomIF> rooms) {
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
                String[] pToShow = {"X","X","X","X"};
                for(int i=0 ; i<r.getnPlayersTarget() ; i++) {
                    if(i < r.getPlayersInGame().size()) {
                        PlayerLobby player = r.getPlayersInGame().get(i);
                        p[i] = player.getNickname();
                        if (!r.getPlayers().contains(player))     // This player is disconnected
                            pToShow[i] = p[i] + " (⚠)";
                        else
                            pToShow[i] = p[i];
                    } else
                        pToShow[i] = "-";
                }
                String formatted = String.format(
                        "║ %-7s │ %-7s │ %-15s │ %-15s │ %-15s │ %-15s │ %-3s ║\n",
                        r.getGameId()+1, r.isGameStarted() ? "started" : "waiting",
                        pToShow[0], pToShow[1], pToShow[2], pToShow[3], r.getPlayers().size() +"/"+ r.getnPlayersTarget()
                );
                for (int i = 0; i < r.getPlayers().size(); i++)
                    formatted = formatted.replace(p[i], ViewTUIConstants.colorNickname(r.getPlayersInGame().get(i)));
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

        if(ParametersClient.SKIP_ROOM) {
            NetworkHandler networkHandler = inputReader.getNetworkHandler();
            if(rooms.isEmpty())
                networkHandler.createRoom("Harry", new Token(ColorToken.RED), ParametersClient.DEBUG_NPLAYERS);
            else {
                RoomIF room = rooms.getFirst();
                switch (room.getPlayers().size()) {
                    case 1 -> networkHandler.joinRoom("Hermione", new Token(ColorToken.BLUE), room.getGameId());
                    case 2 -> networkHandler.joinRoom("Ron", new Token(ColorToken.GREEN), room.getGameId());
                    case 3 -> networkHandler.joinRoom("Voldemort", new Token(ColorToken.YELLOW), room.getGameId());
                    default -> throw new RuntimeException();
                }
            }
        }
    }

    /**
     * Prints a player joining the room.
     * If the player for this was not set, it is assumed this is the ACK message from the server and handles it.
     * Otherwise, it shows the update
     * @param player Player who joined the room
     */
    @Override
    public synchronized void showPlayerJoinedRoom(PlayerLobby player) {
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
    public synchronized void showPlayerLeftRoom(PlayerLobby player) {
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
    public synchronized void showStartGame(GameState gameState, Chat chat) {
        // TODO cosa fare se gameState è già impostato? (sarebbe stato già chiamato startgame...)
        //  E' una situazione da gestire (tipo player left room)?
        this.gameState = gameState;
        this.chat = chat;

        // initialize log
        this.logs = new LogTUI(gameState);

        // print
        ViewTUIConstants.clearScreen();
        System.out.print("Game started.\n");
        changeAndPrintMenu(
                ViewTUIPrintUtils.starterCards(gameState.getPlayerState(thisPlayer).getStarterCard()) +
                "\nPlease choose the side of your starter card:",
                new MenuItemPlayStarter()
        );

        if(ParametersClient.SKIP_INIT){
            inputReader.getNetworkHandler().playStarter(Side.SIDEBACK);
        }
    }

    @Override
    public synchronized void showStartGameReconnected(GameState state, PlayerLobby thisPlayer, Chat chat) {
        this.thisPlayer = thisPlayer;
        this.gameState = state;
        this.chat = chat;

        // re-initialize log
        this.logs = new LogTUI(gameState);
        this.logs.logReconnect(thisPlayer);

        if(gameState.getGameStatus()==GameStatus.INIT || gameState.getGameStatus()==null) {
            for(String log: this.logs.getLogMessages()) System.out.println(log);
            System.out.println("You reconnected successfully, wait for the other players to complete the initialization phase");
        } else {
            // re-initialize view for match as we are in game
            this.viewTUIMatch = new ViewTUIMatch(this, this.gameState, this.thisPlayer);
            this.viewTUIMatch.setLogs(this.logs);
            viewTUIMatch.setDisplayPlayer(this.thisPlayer);
            viewTUIMatch.printMatch();
        }
    }

    /**
     * It shows a successful starter card played.
     * If the player is not the one associated to the view, it shows the update.
     * Otherwise, it shows the two possible objective card and sets the menu in order to choose between them.
     * @param player Player who played their starter card
     */
    @Override
    public synchronized void showPlayedStarter(PlayerLobby player) {
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

        if(ParametersClient.SKIP_INIT && thisPlayer.equals(player)){
            inputReader.getNetworkHandler().choosePersonalObjective(
                    gameState.getPlayerState(thisPlayer).getPossibleHandObjectives().getFirst());
        }
    }

    /**
     * It shows a successful objective card chosen.
     * If the player is the one associated to the view, is also updated the menu
     * @param player Player who chose their personal objective card
     */
    @Override
    public synchronized void showChosenPersonalObjective(PlayerLobby player) {
        this.logs.logChosenPersonalObjective(player);


        if(thisPlayer.equals(player)) {
            this.currentMenu = new MenuTUI("Wait for the other players to finish their initialization.");
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
    public synchronized void showInGame() {
        // TODO non mi dovrebbero arrivare + showInGame, ma se succedesse?
        viewTUIMatch = new ViewTUIMatch(this, gameState, thisPlayer);
        this.viewTUIMatch.setLogs(this.logs);
        viewTUIMatch.setDisplayPlayer(thisPlayer);
        viewTUIMatch.printMatch();
    }

    @Override
    public synchronized void showPlayedCard(PlayerLobby player, Coordinates coord) {
        this.logs.logPlayedCard(player, coord);
        viewTUIMatch.setFlowCardPlaced(true);
        if(currentChatRoom == null)
            viewTUIMatch.printMatch();
    }

    @Override
    public synchronized void showPickedCard(PlayerLobby player) {
        this.logs.logPickedCard(player);
        viewTUIMatch.setFlowCardPlaced(false);
        if(currentChatRoom == null)
            viewTUIMatch.printMatch();
    }

    @Override
    public synchronized void showNextTurn() {
        logs.logNextTurn();
        if(thisPlayer.equals(gameState.getCurrentPlayer()))
            viewTUIMatch.setDisplayPlayer(thisPlayer);
        if(currentChatRoom == null)     //TODO: magari si può forzare la comparsa del field in questo caso...
            viewTUIMatch.printMatch();
        else if(thisPlayer.equals(gameState.getCurrentPlayer()))
            printCurrentChat("Now it's your turn");
    }

    @Override
    public synchronized void showFinalPhase() {
        logs.logFinalPhase();
        if(currentChatRoom == null)
            viewTUIMatch.printMatch();
        else
            printCurrentChat("The final phase has arrived, hurry up!");
    }

    @Override
    public synchronized void showUpdatePoints() {
        this.inputReader.interrupt();
        this.viewTUIMatch = null; // prevents reprint of match visual when a player disconnects before us

        ViewTUIConstants.clearScreen();
        System.out.println("Final points:");
        for(PlayerLobby p : gameState.getPlayers()) {
            System.out.println("\t" + p.getNickname() + "\t  " +
                    gameState.getPlayerState(p).getPoints() + " points");
        }
        System.out.print("\n");
    }

    @Override
    public synchronized void showWinner() {
        System.out.println("⇒ " + gameState.getWinner() + " has won the match!");
        System.out.println("\n\n\n\nPress enter to quit the game\n");
    }

    @Override
    public synchronized void showEndGame() {
        System.out.println("The game has ended");
        // TODO aggiungi networkHandler che chiama getRooms
    }

    @Override
    public void showPlayerDisconnected(PlayerLobby player) {
        this.logs.logDisconnect(player);
        if(viewTUIMatch != null)
            viewTUIMatch.printMatch();
        else
            System.out.println("player " + player.getNickname() + " has disconnected");
            //TODO sistema con i log
    }

    @Override
    public synchronized void showPlayerReconnected(PlayerLobby player) {
        this.logs.logReconnect(player);
        if(viewTUIMatch != null)
            viewTUIMatch.printMatch();
        else
            System.out.println("player " + player.getNickname() + " has reconnected");
            //TODO sistema con i log
    }

    /**
     * Shows a chat message
     * @param sender    of the message
     * @param receivers of the message
     */
    @Override
    public void showChatMessage(PlayerLobby sender, List<PlayerLobby> receivers) {
        if(currentChatRoom != null)
            if (sender.equals(thisPlayer))
                if (receivers.equals(currentChatRoom))
                    printCurrentChat("");
            else if (receivers.contains(thisPlayer))
                if (receivers.size() == 1 && currentChatRoom.size() == 1 && currentChatRoom.getFirst().equals(sender))
                    printCurrentChat("");
                else if (receivers.size() > 1 && currentChatRoom.size() > 1)
                    printCurrentChat("");
    }

    /**
     * Prints all the messages for the currently active chat room (assuming a chatroom is active)
     * It can print a message before the chat room.
     * It prints always the menu, too
     * @param messageUp Message to show before the chat room (it appears at the top of the screen).
     *                  Use "" or null string to show nothing
     */
    private void printCurrentChat(String messageUp) {
        ViewTUIConstants.clearScreen();
        if(messageUp!=null && !messageUp.isEmpty())
            System.out.println(messageUp + "\n");
        System.out.printf("Chat room with %s:\n", currentChatRoom.size()>1 ? "all" : currentChatRoom.getFirst());
        for(ChatMessage msg : chat.getChatWith(currentChatRoom)) {
            System.out.println("\t" + msg.getSender() + " " + msg.getText());
        }
        currentMenu.printMenu();
    }

    /**
     * Enters the chatroom associated to the specified receivers' nicknames
     * @param receiverNicks Receivers' nickname of the players associated to the chatroom to enter
     * @throws InvalidParameterException If the nicknames don't correspond to any player, or are invalid for the chatroom
     */
    public void enterChatRoom(List<String> receiverNicks) throws InvalidParameterException {
        List<PlayerLobby> receivers = new ArrayList<>();
        for(String nick : receiverNicks) {
            PlayerLobby player = gameState.getPlayers().stream().filter(p -> p.getNickname().equals(nick))
                    .findFirst().orElseThrow(InvalidParameterException::new);
            receivers.add(player);
        }
        if(chat.getChatWith(receivers) == null)
            throw new InvalidParameterException();
        currentChatRoom = receivers;

        currentMenu = new MenuTUI("",
                new MenuItemLeaveChat(this),
                new MenuItemSendChatMessage(currentChatRoom)
        );
        showChatMessage(thisPlayer, currentChatRoom);       // I force the chatroom to appear immediately
    }

    /**
     * Leaves the chatroom currently active, and reprints the match
     */
    public void leaveChatRoom() {
        currentChatRoom = null;
        viewTUIMatch.printMatch();
    }

    public PlayerLobby getThisPlayer() {
        return thisPlayer;
    }

    public GameState getGameState() {
        return gameState;
    }

    public synchronized MenuTUI getCurrentMenu() {
        return currentMenu;
    }

    synchronized void setCurrentMenu(MenuTUI currentMenu) {
        this.currentMenu = currentMenu;
    }
}
