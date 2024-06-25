package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.view.tui.menu.*;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the TUI during the match
 */
public class ViewTUIMatch {

    //TODO: magari aggiungi un contatore per le risorse attualmente in campo

    private final ViewTUI view;
    private final GameState gameState;
    private final PlayerLobby thisPlayer;
    private PlayerLobby displayPlayer;
    private boolean flowCardPlaced;
    private LogTUI logs;

    /**
     * Flag indicating if the legend is currently being printed.
     * The default value is true
     */
    private boolean showLegend;
    /**
     * Item that must be shown to enable or disable the print of the legend
     */
    private MenuItem itemForLegend;

    public ViewTUIMatch(ViewTUI viewTUI, GameState gameState, PlayerLobby thisPlayer) {
        this.view = viewTUI;
        this.gameState = gameState;
        this.thisPlayer = thisPlayer;
        this.displayPlayer = null;
        this.flowCardPlaced = false;
        this.logs = null;
        this.setShowLegend(true);
    }

    /**
     * Setter for the flag indicating whether to print the legend
     * @param showLegend True if the legend must be printed, false otherwise
     */
    public void setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
        if(showLegend)
            itemForLegend = new MenuItemDisableLegend(this);
        else
            itemForLegend = new MenuItemEnableLegend(this);
    }

    public void setFlowCardPlaced(boolean flowCardPlaced) {
        this.flowCardPlaced = flowCardPlaced;
    }

    public void setDisplayPlayer(PlayerLobby displayPlayer) {
        this.displayPlayer = displayPlayer;
    }

    public void setDisplayPlayer(String nick) throws InvalidParameterException {
        this.displayPlayer = gameState.getPlayers().stream().filter(p -> p.getNickname().equals(nick)).
                findFirst().orElseThrow(InvalidParameterException::new);
    }

    public void setLogs(LogTUI logs) {
        this.logs = logs;
    }

    public void printMatch() {

        // clear screen
        ViewTUIConstants.clearScreen();

        // print player header
        System.out.println(sectionPlayers());

        if(showLegend)
            System.out.println(sectionLegend());

        // print player field
        System.out.println(sectionField());

        // print player cards (with proper censorship if opponent)
        System.out.println(sectionCards());

        // print logs
        System.out.println(sectionLogs());

        // print menu (different based on player's turn status)
        if(thisPlayer.equals(gameState.getCurrentPlayer()) && gameState.countConnected() > 1) {
            // it's this player's turn. Force game flow: (1) place, (2) pick
            if (!flowCardPlaced) {
                // player has to place a card first
                view.setCurrentMenu(new MenuTUI( "Play a card on the field",
                        new MenuItemPlayCard(this.gameState),
                        new MenuItemEnterChat(view),
                        itemForLegend
                ));
            } else {
                // player has to pick a new card from drawable ones
                view.setCurrentMenu(new MenuTUI( "Pick a card from the common field",
                        new MenuItemPickCard(this.gameState),
                        new MenuItemEnterChat(view),
                        itemForLegend
                ));
            }
        } else {
            // not this player's turn, can move around until its turn.
            String menuCommand = gameState.countConnected()>1 ? "Wait for your turn" :
                    "You remained alone, wait for at least one of the other players to reconnect";
            view.setCurrentMenu(new MenuTUI(menuCommand,
                    new MenuItemChangeField(this),
                    new MenuItemEnterChat(view),
                    itemForLegend
            ));
        }

        view.getCurrentMenu().printMenu();
    }

    // --------------------------------------------------------------------
    // ----------------------------- SECTIONS -----------------------------
    // --------------------------------------------------------------------

    private String sectionPlayers() {
        List<PlayerLobby> players = this.gameState.getPlayers();

        // turn symbol
        Character[] turnSymbol = {' ', ' ', ' ', ' '};
        for(int i=0; i<players.size(); i++) turnSymbol[i] = players.get(i).equals(this.gameState.getCurrentPlayer()) ? '▼' : ' ';

        // player nicknames
        String[] nickString = {"", "", "", ""};
        for(int i=0; i< players.size(); i++)
            nickString[i] = (this.thisPlayer.equals(players.get(i)) ? ("[" + players.get(i).getNickname() + "]") : (players.get(i).getNickname()));

        // player online/offline
        Character[] onlineSymbol = {' ', ' ', ' ', ' '};
        for(int i=0; i<players.size(); i++) onlineSymbol[i] = this.gameState.getPlayerState(players.get(i)).isConnected() ? '✓' : '⚠';

        // player points
        Integer[] points = {-1, -1, -1, -1};
        for(int i=0; i<players.size(); i++) points[i] = this.gameState.getPlayerState(players.get(i)).getPoints();
        String[] pointsStr = new String[4];
        for(int i=0; i<4; i++) pointsStr[i] = ((points[i] == -1) ? "  " :  points[i].toString());

        return String.format(
                """
                        Turn   │ %c       %c         │ %c       %c         │ %c       %c         │ %c       %c         │
                        Player │    %-14s │    %-14s │    %-14s │    %-14s │
                        Points │        %2s         │        %2s         │        %2s         │        %2s         │
                """,
                onlineSymbol[0], turnSymbol[0], onlineSymbol[1], turnSymbol[1], onlineSymbol[2], turnSymbol[2], onlineSymbol[3], turnSymbol[3],
                nickString[0], nickString[1], nickString[2], nickString[3],
                pointsStr[0], pointsStr[1], pointsStr[2], pointsStr[3]
        );
    }

    private String sectionLegend() {
        StringBuilder sb = new StringBuilder("Legend:");
        for(Resource r : Resource.values())
            if(r != Resource.NO_RESOURCE)
                sb.append(String.format("\n\tResource %s: %s", ViewTUIConstants.resourceToSymbol(r), r.name()));
        sb.append("\n\t" + ViewTUIConstants.ANGLE_NORESOURCE_SYMBOL + ": Existing corner with no resource");
        sb.append("\n\t" + ViewTUIConstants.ANGLE_NOTLINKABLE_SYMBOL + ": Non existing corner (card not placeable)");
        sb.append("\n\t" + ViewTUIConstants.POINTS_PATTERN_ANGLE + ": Objective card giving points for how many corners are covered");
        sb.append("\n\tAbbreviations for objective card giving points for patterns of colors: LT (Left), RT (Right), DN (Down)");
        sb.append("\n");
        return sb.toString();
    }

    private String sectionField() {
        return ViewTUIPrintUtils.genFieldString(gameState.getPlayerState(displayPlayer).getField());
    }

    private String sectionCards() {
        return thisPlayer.equals(displayPlayer) ? sectionCardsThisPlayer() : sectionCardsOpponentPlayer();
    }

    private String sectionLogs() {
        String logString = "";
        for(String log: this.logs.getLogMessages().reversed()) {
            String logLine = "";
            logLine = logLine.concat("║" + log);
            logLine = String.format("%-100s║\n", logLine);
            logString = logString.concat(logLine);
        }

        return String.format(
                """
                        ╔═════════════════════════════════════════════[▽ LOGS ▽]════════════════════════════════════════════╗
                        %s╚═══════════════════════════════════════════════════════════════════════════════════════════════════╝
                        """,
                logString
        );
    }

    // --------------------------------------------------------------------
    // ------------------------------ UTILS -------------------------------
    // --------------------------------------------------------------------

    private String sectionCardsThisPlayer() {

        // player hand displayed with front side
        List<ViewTUIPrintUtils.CardSideSymbolsBuilder> hand = new ArrayList<>();
        List<CardPlayableIF> handCards = this.gameState.getPlayerState(this.displayPlayer).getHandPlayable();
        for(int i=0 ; i<3 ; i++) {
            CardPlayableIF card = i<handCards.size() ? handCards.get(i) : null;
            hand.add(new ViewTUIPrintUtils.CardSideSymbolsBuilder(card, Side.SIDEFRONT));
        }

        // decks for resource cards and gold cards
        List<ViewTUIPrintUtils.CardSideSymbolsBuilder> deckRes = new ArrayList<>();
        List<ViewTUIPrintUtils.CardSideSymbolsBuilder> deckGold = new ArrayList<>();

        List<CardPlayableIF> pickables = this.gameState.getPickables();
        deckRes.add(new ViewTUIPrintUtils.CardSideSymbolsBuilder(pickables.get(0), Side.SIDEBACK)); // deck
        deckRes.add(new ViewTUIPrintUtils.CardSideSymbolsBuilder(pickables.get(1), Side.SIDEFRONT)); // option 1
        deckRes.add(new ViewTUIPrintUtils.CardSideSymbolsBuilder(pickables.get(2), Side.SIDEFRONT)); // option 2

        deckGold.add(new ViewTUIPrintUtils.CardSideSymbolsBuilder(pickables.get(3), Side.SIDEBACK)); // deck
        deckGold.add(new ViewTUIPrintUtils.CardSideSymbolsBuilder(pickables.get(4), Side.SIDEFRONT)); // option 1
        deckGold.add(new ViewTUIPrintUtils.CardSideSymbolsBuilder(pickables.get(5), Side.SIDEFRONT)); // option 2

        List<String> infoObj1 = ViewTUIPrintUtils.createInfoForObjective(gameState.getCommonObjectives().get(0));
        List<String> infoObj2 = ViewTUIPrintUtils.createInfoForObjective(gameState.getCommonObjectives().get(1));
        List<String> infoObj3 = ViewTUIPrintUtils.createInfoForObjective(gameState.getPlayerState(thisPlayer).getHandObjective());


        return String.format(
                       """
                        ╔═══════════════════════════[▽ DRAWABLE CARDS ▽]════════════════════════╦═════════[▽ HAND ▽]════════╗
                        ║           DECKS                 OPTION 1               OPTION 2       ║ ┌───┬───%c───%c───┬───┐     ║
                        ║   ┌───┬───%c───%c───┬───┐  ┌───┬───%c───%c───┬───┐  ┌───┬───%c───%c───┬───┐ ║ │ %c │    %3s    │ %c │     ║
                        ║ R │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │ ║ ├───┘    %3s    └───┤ (1) ║
                        ║ E ├───┘    %3s    └───┤  ├───┘    %3s    └───┤  ├───┘    %3s    └───┤ ║ ├───┐           ┌───┤     ║
                        ║ S ├───┐           ┌───┤  ├───┐           ┌───┤  ├───┐           ┌───┤ ║ │ %c │   %5s   │ %c │     ║
                        ║   │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │ ║ └───┴───────────┴───┘     ║
                        ║   └───┴───────────┴───┘  └───┴───────────┴───┘  └───┴───────────┴───┘ ║                           ║
                        ║   ┌───┬───%c───%c───┬───┐  ┌───┬───%c───%c───┬───┐  ┌───┬───%c───%c───┬───┐ ║ ┌───┬───%c───%c───┬───┐     ║
                        ║ G │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │ ║ │ %c │    %3s    │ %c │     ║
                        ║ O ├───┘    %3s    └───┤  ├───┘    %3s    └───┤  ├───┘    %3s    └───┤ ║ ├───┘    %3s    └───┤ (2) ║
                        ║ L ├───┐           ┌───┤  ├───┐           ┌───┤  ├───┐           ┌───┤ ║ ├───┐           ┌───┤     ║
                        ║ D │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │ ║ │ %c │   %5s   │ %c │     ║
                        ║   └───┴───────────┴───┘  └───┴───────────┴───┘  └───┴───────────┴───┘ ║ └───┴───────────┴───┘     ║
                        ╠════════════════════════════[▽ OBJECTIVES ▽]═══════════════════════════╣                           ║
                        ║   ┌───────COMMON──────┐  ┌───────COMMON──────┐  ┌──────PERSONAL─────┐ ║ ┌───┬───%c───%c───┬───┐     ║
                        ║   │%s│  │%s│  │%s│ ║ │ %c │    %3s    │ %c │     ║
                        ║   │%s│  │%s│  │%s│ ║ ├───┘    %3s    └───┤ (3) ║
                        ║   │%s│  │%s│  │%s│ ║ ├───┐           ┌───┤     ║
                        ║   │%s│  │%s│  │%s│ ║ │ %c │   %5s   │ %c │     ║
                        ║   └───────────────────┘  └───────────────────┘  └───────────────────┘ ║ └───┴───────────┴───┘     ║
                        ╚═══════════════════════════════════════════════════════════════════════╩═══════════════════════════╝""",
                hand.get(0).type, hand.get(0).side,
                deckRes.get(0).type, deckRes.get(0).side, deckRes.get(1).type, deckRes.get(1).side, deckRes.get(2).type, deckRes.get(2).side, hand.get(0).corners[0], hand.get(0).points, hand.get(0).corners[1],
                deckRes.get(0).corners[0], deckRes.get(0).points, deckRes.get(0).corners[1], deckRes.get(1).corners[0], deckRes.get(1).points, deckRes.get(1).corners[1], deckRes.get(2).corners[0], deckRes.get(2).points, deckRes.get(2).corners[1], hand.get(0).color,
                deckRes.get(0).color, deckRes.get(1).color, deckRes.get(2).color,
                hand.get(0).corners[3], hand.get(0).requirements, hand.get(0).corners[2],
                deckRes.get(0).corners[3], deckRes.get(0).requirements, deckRes.get(0).corners[2], deckRes.get(1).corners[3], deckRes.get(1).requirements, deckRes.get(1).corners[2], deckRes.get(2).corners[3], deckRes.get(2).requirements, deckRes.get(2).corners[2],

                deckGold.get(0).type, deckGold.get(0).side, deckGold.get(1).type, deckGold.get(1).side, deckGold.get(2).type, deckGold.get(2).side,  hand.get(1).type, hand.get(1).side,
                deckGold.get(0).corners[0], deckGold.get(0).points, deckGold.get(0).corners[1], deckGold.get(1).corners[0], deckGold.get(1).points, deckGold.get(1).corners[1], deckGold.get(2).corners[0], deckGold.get(2).points, deckGold.get(2).corners[1], hand.get(1).corners[0], hand.get(1).points, hand.get(1).corners[1],
                deckGold.get(0).color, deckGold.get(1).color, deckGold.get(2).color, hand.get(1).color,
                deckGold.get(0).corners[3], deckGold.get(0).requirements, deckGold.get(0).corners[2], deckGold.get(1).corners[3], deckGold.get(1).requirements, deckGold.get(1).corners[2], deckGold.get(2).corners[3], deckGold.get(2).requirements, deckGold.get(2).corners[2], hand.get(1).corners[3], hand.get(1).requirements, hand.get(1).corners[2],

                hand.get(2).type, hand.get(2).side,
                infoObj1.get(0), infoObj2.get(0), infoObj3.get(0),
                hand.get(2).corners[0], hand.get(2).points, hand.get(2).corners[1],
                infoObj1.get(1), infoObj2.get(1), infoObj3.get(1),
                hand.get(2).color,
                infoObj1.get(2), infoObj2.get(2), infoObj3.get(2),
                infoObj1.get(3), infoObj2.get(3), infoObj3.get(3),
                hand.get(2).corners[3], hand.get(2).requirements, hand.get(2).corners[2]
            );
    }

    private String sectionCardsOpponentPlayer() {

        // player hand displayed with back side
        List<ViewTUIPrintUtils.CardSideSymbolsBuilder> hand = new ArrayList<>();
        List<CardPlayableIF> handCards = this.gameState.getPlayerState(this.displayPlayer).getHandPlayable();
        for(int i=0 ; i<3 ; i++) {
            CardPlayableIF card = i<handCards.size() ? handCards.get(i) : null;
            hand.add(new ViewTUIPrintUtils.CardSideSymbolsBuilder(card, Side.SIDEBACK));
        }

        // decks for resource cards and gold cards
        List<ViewTUIPrintUtils.CardSideSymbolsBuilder> deckRes = new ArrayList<>();
        List<ViewTUIPrintUtils.CardSideSymbolsBuilder> deckGold = new ArrayList<>();

        List<CardPlayableIF> pickables = this.gameState.getPickables();
        deckRes.add(new ViewTUIPrintUtils.CardSideSymbolsBuilder(pickables.get(0), Side.SIDEBACK)); // deck
        deckRes.add(new ViewTUIPrintUtils.CardSideSymbolsBuilder(pickables.get(1), Side.SIDEFRONT)); // option 1
        deckRes.add(new ViewTUIPrintUtils.CardSideSymbolsBuilder(pickables.get(2), Side.SIDEFRONT)); // option 2

        deckGold.add(new ViewTUIPrintUtils.CardSideSymbolsBuilder(pickables.get(3), Side.SIDEBACK)); // deck
        deckGold.add(new ViewTUIPrintUtils.CardSideSymbolsBuilder(pickables.get(4), Side.SIDEFRONT)); // option 1
        deckGold.add(new ViewTUIPrintUtils.CardSideSymbolsBuilder(pickables.get(5), Side.SIDEFRONT)); // option 2

        List<String> infoObj1 = ViewTUIPrintUtils.createInfoForObjective(gameState.getCommonObjectives().get(0));
        List<String> infoObj2 = ViewTUIPrintUtils.createInfoForObjective(gameState.getCommonObjectives().get(1));

        return String.format(
                       """
                        ╔═══════════════════════════[▽ DRAWABLE CARDS ▽]════════════════════════╦═════════[▽ HAND ▽]════════╗
                        ║           DECKS                 OPTION 1               OPTION 2       ║ ┌───┬───%c───%c───┬───┐     ║
                        ║   ┌───┬───%c───%c───┬───┐  ┌───┬───%c───%c───┬───┐  ┌───┬───%c───%c───┬───┐ ║ │ %c │    %3s    │ %c │     ║
                        ║ R │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │ ║ ├───┘    %3s    └───┤ (1) ║
                        ║ E ├───┘    %3s    └───┤  ├───┘    %3s    └───┤  ├───┘    %3s    └───┤ ║ ├───┐           ┌───┤     ║
                        ║ S ├───┐           ┌───┤  ├───┐           ┌───┤  ├───┐           ┌───┤ ║ │ %c │   %5s   │ %c │     ║
                        ║   │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │ ║ └───┴───────────┴───┘     ║
                        ║   └───┴───────────┴───┘  └───┴───────────┴───┘  └───┴───────────┴───┘ ║                           ║
                        ║   ┌───┬───%c───%c───┬───┐  ┌───┬───%c───%c───┬───┐  ┌───┬───%c───%c───┬───┐ ║ ┌───┬───%c───%c───┬───┐     ║
                        ║ G │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │ ║ │ %c │    %3s    │ %c │     ║
                        ║ O ├───┘    %3s    └───┤  ├───┘    %3s    └───┤  ├───┘    %3s    └───┤ ║ ├───┘    %3s    └───┤ (2) ║
                        ║ L ├───┐           ┌───┤  ├───┐           ┌───┤  ├───┐           ┌───┤ ║ ├───┐           ┌───┤     ║
                        ║ D │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │ ║ │ %c │   %5s   │ %c │     ║
                        ║   └───┴───────────┴───┘  └───┴───────────┴───┘  └───┴───────────┴───┘ ║ └───┴───────────┴───┘     ║
                        ╠════════════════════════════[▽ OBJECTIVES ▽]═══════════════════════════╣                           ║
                        ║   ┌───────COMMON──────┐  ┌───────COMMON──────┐  ┌──────PERSONAL─────┐ ║ ┌───┬───%c───%c───┬───┐     ║
                        ║   │%s│  │%s│  │░░░░░░░░░░░░░░░░░░░│ ║ │ %c │    %3s    │ %c │     ║
                        ║   │%s│  │%s│  │░░░░░░░HIDDEN░░░░░░│ ║ ├───┘    %3s    └───┤ (3) ║
                        ║   │%s│  │%s│  │░░░░░░░░░░░░░░░░░░░│ ║ ├───┐           ┌───┤     ║
                        ║   │%s│  │%s│  │░░░░░░░░░░░░░░░░░░░│ ║ │ %c │   %5s   │ %c │     ║
                        ║   └───────────────────┘  └───────────────────┘  └───────────────────┘ ║ └───┴───────────┴───┘     ║
                        ╚═══════════════════════════════════════════════════════════════════════╩═══════════════════════════╝""",
                hand.get(0).type, hand.get(0).side,
                deckRes.get(0).type, deckRes.get(0).side, deckRes.get(1).type, deckRes.get(1).side, deckRes.get(2).type, deckRes.get(2).side, hand.get(0).corners[0], hand.get(0).points, hand.get(0).corners[1],
                deckRes.get(0).corners[0], deckRes.get(0).points, deckRes.get(0).corners[1], deckRes.get(1).corners[0], deckRes.get(1).points, deckRes.get(1).corners[1], deckRes.get(2).corners[0], deckRes.get(2).points, deckRes.get(2).corners[1], hand.get(0).color,
                deckRes.get(0).color, deckRes.get(1).color, deckRes.get(2).color,
                hand.get(0).corners[3], hand.get(0).requirements, hand.get(0).corners[2],
                deckRes.get(0).corners[3], deckRes.get(0).requirements, deckRes.get(0).corners[2], deckRes.get(1).corners[3], deckRes.get(1).requirements, deckRes.get(1).corners[2], deckRes.get(2).corners[3], deckRes.get(2).requirements, deckRes.get(2).corners[2],

                deckGold.get(0).type, deckGold.get(0).side, deckGold.get(1).type, deckGold.get(1).side, deckGold.get(2).type, deckGold.get(2).side,  hand.get(1).type, hand.get(1).side,
                deckGold.get(0).corners[0], deckGold.get(0).points, deckGold.get(0).corners[1], deckGold.get(1).corners[0], deckGold.get(1).points, deckGold.get(1).corners[1], deckGold.get(2).corners[0], deckGold.get(2).points, deckGold.get(2).corners[1], hand.get(1).corners[0], hand.get(1).points, hand.get(1).corners[1],
                deckGold.get(0).color, deckGold.get(1).color, deckGold.get(2).color, hand.get(1).color,
                deckGold.get(0).corners[3], deckGold.get(0).requirements, deckGold.get(0).corners[2], deckGold.get(1).corners[3], deckGold.get(1).requirements, deckGold.get(1).corners[2], deckGold.get(2).corners[3], deckGold.get(2).requirements, deckGold.get(2).corners[2], hand.get(1).corners[3], hand.get(1).requirements, hand.get(1).corners[2],

                hand.get(2).type, hand.get(2).side,
                infoObj1.get(0), infoObj2.get(0),
                hand.get(2).corners[0], hand.get(2).points, hand.get(2).corners[1],
                infoObj1.get(1), infoObj2.get(1),
                hand.get(2).color,
                infoObj1.get(2), infoObj2.get(2),
                infoObj1.get(3), infoObj2.get(3),
                hand.get(2).corners[3], hand.get(2).requirements, hand.get(2).corners[2]
        );
    }


}
