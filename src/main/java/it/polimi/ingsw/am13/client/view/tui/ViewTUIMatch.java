package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.view.tui.menu.*;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViewTUIMatch {

    private final ViewTUI view;
    private final GameState gameState;
    private final PlayerLobby thisPlayer;
    private PlayerLobby displayPlayer;
    private boolean flowCardPlaced;

    public ViewTUIMatch(ViewTUI viewTUI, GameState gameState, PlayerLobby thisPlayer) {
        this.view = viewTUI;
        this.gameState = gameState;
        this.thisPlayer = thisPlayer;
        this.displayPlayer = null;
        this.flowCardPlaced = false;
    }

    public void setFlowCardPlaced(boolean flowCardPlaced) {
        this.flowCardPlaced = flowCardPlaced;
    }

    public void setDisplayPlayer(PlayerLobby displayPlayer) {
        this.displayPlayer = displayPlayer;
    }

    public void printMatch() {
//        this.displayPlayer = player;

        // print player header
        System.out.println(sectionPlayers());

        // print player field
        // FIXME: Something fuc*s up here...
        // System.out.println(sectionField());

        // print player cards (with proper censorship if opponent)
        System.out.println(sectionCards());

        // print logs


        // print menu (different based on player's turn status)
        if(this.gameState.getCurrentPlayer().equals(this.thisPlayer)) {
            // it's this player's turn. Force game flow: (1) place, (2) pick
            if (!flowCardPlaced) {
                // player has to place a card first
                view.setCurrentMenu(new MenuTUI(
                        new MenuItemPlayCard(this.gameState)
                ));

//                this.flowCardPlaced = true; // FIXME: could be broken with disconnections :/
            } else {
                // player has to pick a new card from drawable ones
                view.setCurrentMenu(new MenuTUI(
                        new MenuItemPickCard(this.gameState)
                ));

//                this.flowCardPlaced = false; // FIXME: could be broken with disconnections :/
            }
        } else {
            // not this player's turn, can move around until its turn.
            view.setCurrentMenu(new MenuTUI());    // FIXME: implement player movement in other fields!
        }

        view.getCurrentMenu().printMenu();
    }

    // --------------------------------------------------------------------
    // ----------------------------- SECTIONS -----------------------------
    // --------------------------------------------------------------------

    public String sectionPlayers() {
        List<PlayerLobby> players = this.gameState.getPlayers();

        // turn symbol
        Character[] turnSymbol = {' ', ' ', ' ', ' '};
        for(int i=0; i<players.size(); i++) turnSymbol[i] = players.get(i).equals(this.gameState.getCurrentPlayer()) ? '▼' : ' ';

        // player nicknames
        String[] nickString = {"", "", "", ""};
        for(int i=0; i< players.size(); i++) nickString[i] = players.get(i).getNickname();

        // player online/offline
        Character[] onlineSymbol = {' ', ' ', ' ', ' '};
        for(int i=0; i<players.size(); i++) turnSymbol[i] = this.gameState.getPlayerState(players.get(i)).isConnected() ? '✓' : '⚠';

        // player points
        Integer[] points = {-1, -1, -1, -1};
        for(int i=0; i<players.size(); i++) points[i] = this.gameState.getPlayerState(players.get(i)).getPoints();
        String[] pointsStr = new String[4];
        for(int i=0; i<4; i++) pointsStr[i] = ((points[i] == -1) ? "  " :  points[i].toString());

        return String.format(
                """
                        Turn   │         %c         │         %c         │         %c         │         %c         │
                        Player │ %c  %-14s │ %c  %-14s │ %c  %-14s │ %c  %-14s │
                        Points │        %2s         │        %2s         │        %2s         │        %2s         │
                        """,
                turnSymbol[0], turnSymbol[1], turnSymbol[2], turnSymbol[3],
                onlineSymbol[0], nickString[0],
                onlineSymbol[1], nickString[1],
                onlineSymbol[2], nickString[2],
                onlineSymbol[3], nickString[3],
                pointsStr[0], pointsStr[1], pointsStr[2], pointsStr[3]
        );
    }

    private String sectionField() {
        return genFieldString(this.displayPlayer);
    }

    private String sectionCards() {
        if(this.thisPlayer == this.displayPlayer)
            return sectionCardsThisPlayer();
        else return sectionCardsOpponentPlayer();
    }

    // --------------------------------------------------------------------
    // ------------------------------ UTILS -------------------------------
    // --------------------------------------------------------------------

    private class CardSideSymbols {
        public Character type; // gold/resource -> G/R
        public Character side; // front/back -> F/B
        public Character[] corners; // -> {x,y,z,k}
        public String points; // points -> "2xK" / " 2 "
        public Character color; // resource color -> 'x'
        public String requirements; // requirements -> "  x  " / " xx  " / " xxx " / "xxxx " / "xxxxx"

        public CardSideSymbols(CardPlayableIF c, Side s) {
            if(c == null) {
                type = '-';
                side = '-';
                corners = new Character[4];
                for(int i=0 ; i<4 ; i++)
                    corners[i] = ' ';
                points = "   ";
                color = ' ';
                requirements = "     ";
            } else {

                CardSidePlayableIF cs = c.getSide(s);

                this.type = c instanceof CardGold ? 'G' : 'R';
                this.side = s.equals(Side.SIDEFRONT) ? 'F' : 'B';

                List<Resource> cornerRes = cs.getCornerResources();
                this.corners = new Character[4];
                for (int i = 0; i < 4; i++)
                    this.corners[i] = ViewTUIConstants.resourceToSymbol(cornerRes.get(i)).charAt(0);

                if (cs.getPoints() != null) { //TODO controlla se va gestito in qualche altro modo
                    if (cs.getPoints().isCornerTypePoints())
                        this.points = String.format("%dx%c", cs.getPoints().getPointsMultiplier(), ViewTUIConstants.POINTS_PATTERN_ANGLE.charAt(0));
                    else if (cs.getPoints().getPointsResource() != Resource.NO_RESOURCE) {
                        this.points = String.format("%dx%c", cs.getPoints().getPointsMultiplier(), ViewTUIConstants.resourceToSymbol(cs.getPoints().getPointsResource()).charAt(0));
                    } else {
                        this.points = String.format(" %d ", cs.getPoints().getPointsMultiplier());
                    }
                } else {
                    this.points = "   "; // 0 points
                }

                this.color = ViewTUIConstants.resourceToSymbol(cs.getColor().correspondingResource()).charAt(0);

                List<Character> requirementList = new ArrayList<>();
                for (Resource r : cs.getRequirements().keySet())
                    for (int i = 0; i < cs.getRequirements().get(r); i++)
                        requirementList.add(ViewTUIConstants.resourceToSymbol(r).charAt(0));
                switch (requirementList.size()) {
                    case 0: {
                        this.requirements = "     ";
                        break;
                    }
                    case 1: {
                        this.requirements = String.format("  %c  ", requirementList.getFirst());
                        break;
                    }
                    case 2: {
                        this.requirements = String.format(" %c%c  ", requirementList.get(0), requirementList.get(1));
                        break;
                    }
                    case 3: {
                        this.requirements = String.format(" %c%c%c ", requirementList.get(0), requirementList.get(1), requirementList.get(2));
                        break;
                    }
                    case 4: {
                        this.requirements = String.format("%c%c%c%c ", requirementList.get(0), requirementList.get(1), requirementList.get(2), requirementList.get(3));
                        break;
                    }
                    case 5: {
                        this.requirements = requirementList.stream().map(String::valueOf).collect(Collectors.joining());
                    }
                }
            }
        }
    }

    private String sectionCardsThisPlayer() {
        // player hand displayed with front sides
        List<CardSideSymbols> hand = new ArrayList<>();
        List<CardPlayableIF> handCards = this.gameState.getPlayerState(this.displayPlayer).getHandPlayable();
        for(int i=0 ; i<3 ; i++) {
//        for(CardPlayableIF card : this.gameState.getPlayerState(this.displayPlayer).getHandPlayable()) {
            CardPlayableIF card = i<handCards.size() ? handCards.get(i) : null;
            hand.add(new CardSideSymbols(card, Side.SIDEFRONT));
        }
        //hand.add(new CardSideSymbols(this.gameState.getPlayerState(this.displayPlayer).getHandObjective(),Side.SIDEFRONT));
        // decks for resource cards and gold cards
        List<CardSideSymbols> deckRes = new ArrayList<>();
        List<CardSideSymbols> deckGold = new ArrayList<>();

        List<CardPlayableIF> pickables = this.gameState.getPickables();
        deckRes.add(new CardSideSymbols(pickables.get(0), Side.SIDEBACK)); // deck
        deckRes.add(new CardSideSymbols(pickables.get(1), Side.SIDEFRONT)); // option 1
        deckRes.add(new CardSideSymbols(pickables.get(2), Side.SIDEFRONT)); // option 2

        deckGold.add(new CardSideSymbols(pickables.get(3), Side.SIDEBACK)); // deck
        deckGold.add(new CardSideSymbols(pickables.get(4), Side.SIDEFRONT)); // option 1
        deckGold.add(new CardSideSymbols(pickables.get(5), Side.SIDEFRONT)); // option 2

        List<String> infoObj1 = ViewTUIPrintUtils.createInfoForObjective(gameState.getCommonObjectives().get(0));
        List<String> infoObj2 = ViewTUIPrintUtils.createInfoForObjective(gameState.getCommonObjectives().get(1));
        List<String> infoObj3 = ViewTUIPrintUtils.createInfoForObjective(gameState.getPlayerState(thisPlayer).getHandObjective());


        return String.format(
                """
                        ╔═══════════════════════════[▽ DRAWABLE CARDS ▽]════════════════════════╦═══════[▽ HAND ▽]══════╗
                        ║           DECKS                 OPTION 1               OPTION 2       ║ ┌───┬───%c───F───┬───┐ ║
                        ║   ┌───┬───%c───B───┬───┐  ┌───┬───%c───F───┬───┐  ┌───┬───%c───F───┬───┐ ║ │ %c │    %3s    │ %c │ ║
                        ║ R │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │ ║ ├───┘    [%c]    └───┤ ║
                        ║ E ├───┘     %c     └───┤  ├───┘    [%c]    └───┤  ├───┘    [%c]    └───┤ ║ ├───┐           ┌───┤ ║
                        ║ S ├───┐           ┌───┤  ├───┐           ┌───┤  ├───┐           ┌───┤ ║ │ %c │   %5s   │ %c │ ║
                        ║   │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │ ║ └───┴───────────┴───┘ ║
                        ║   └───┴───────────┴───┘  └───┴───────────┴───┘  └───┴───────────┴───┘ ║                       ║
                        ║   ┌───┬───%c───B───┬───┐  ┌───┬───%c───F───┬───┐  ┌───┬───%c───F───┬───┐ ║ ┌───┬───%c───F───┬───┐ ║
                        ║ G │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │ ║ │ %c │    %3s    │ %c │ ║
                        ║ O ├───┘     %c     └───┤  ├───┘    [%c]    └───┤  ├───┘    [%c]    └───┤ ║ ├───┘    [%c]    └───┤ ║
                        ║ L ├───┐           ┌───┤  ├───┐           ┌───┤  ├───┐           ┌───┤ ║ ├───┐           ┌───┤ ║
                        ║ D │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │ ║ │ %c │   %5s   │ %c │ ║
                        ║   └───┴───────────┴───┘  └───┴───────────┴───┘  └───┴───────────┴───┘ ║ └───┴───────────┴───┘ ║
                        ╠════════════════════════════[▽ OBJECTIVES ▽]═══════════════════════════╣                       ║
                        ║   ┌─────────.─────────┐  ┌─────────.─────────┐  ┌─────────.─────────┐ ║ ┌───┬───%c───F───┬───┐ ║
                        ║   │%s│  │%s│  │%s│ ║ │ %c │    %3s    │ %c │ ║
                        ║   │%s│  │%s│  │%s│ ║ ├───┘    [%c]    └───┤ ║
                        ║   │%s│  │%s│  │%s│ ║ ├───┐           ┌───┤ ║
                        ║   │%s│  │%s│  │%s│ ║ │ %c │   %5s   │ %c │ ║
                        ║   └───────────────────┘  └───────────────────┘  └───────────────────┘ ║ └───┴───────────┴───┘ ║
                        ╚═══════════════════════════════════════════════════════════════════════╩═══════════════════════╝
                        """,
                hand.get(0).type,
                deckRes.get(0).type, deckRes.get(1).type, deckRes.get(2).type, hand.get(0).corners[0], hand.get(0).points, hand.get(0).corners[1],
                deckRes.get(0).corners[0], deckRes.get(0).points, deckRes.get(0).corners[1], deckRes.get(1).corners[0], deckRes.get(1).points, deckRes.get(1).corners[1], deckRes.get(2).corners[0], deckRes.get(2).points, deckRes.get(2).corners[1], hand.get(0).color,
                deckRes.get(0).color, deckRes.get(1).color, deckRes.get(2).color,
                hand.get(0).corners[3], hand.get(0).points, hand.get(0).corners[2],
                deckRes.get(0).corners[3], deckRes.get(0).requirements, deckRes.get(0).corners[2], deckRes.get(1).corners[3], deckRes.get(1).requirements, deckRes.get(1).corners[2], deckRes.get(2).corners[3], deckRes.get(2).requirements, deckRes.get(2).corners[2],

                deckGold.get(0).type, deckGold.get(1).type, deckGold.get(2).type, hand.get(1).type,
                deckGold.get(0).corners[0], deckGold.get(0).points, deckGold.get(0).corners[1], deckGold.get(1).corners[0], deckGold.get(1).points, deckGold.get(1).corners[1], deckGold.get(2).corners[0], deckGold.get(2).points, deckGold.get(2).corners[1], hand.get(1).corners[0], hand.get(1).points, hand.get(1).corners[1],
                deckGold.get(0).color, deckGold.get(1).color, deckGold.get(2).color, hand.get(1).color,
                deckGold.get(0).corners[3], deckGold.get(0).requirements, deckGold.get(0).corners[2], deckGold.get(1).corners[3], deckGold.get(1).requirements, deckGold.get(1).corners[2], deckGold.get(2).corners[3], deckGold.get(2).requirements, deckGold.get(2).corners[2], hand.get(1).corners[3], hand.get(1).requirements, hand.get(1).corners[2],

                hand.get(2).type,       //TODO si potrebbe non mostrare se non c'è la carta
                infoObj1.get(0), infoObj2.get(0), infoObj3.get(0),
                hand.get(2).corners[0], hand.get(2).points, hand.get(2).corners[1],
                infoObj1.get(1), infoObj2.get(1), infoObj3.get(1),
                hand.get(2).color,      //TODO si potrebbe generare le parentesi o meno se color è ' '
                infoObj1.get(2), infoObj2.get(2), infoObj3.get(2),
                infoObj1.get(3), infoObj2.get(3), infoObj3.get(3),
                hand.get(2).corners[3], hand.get(2).requirements, hand.get(2).corners[2]
            );
    }

    private String sectionCardsOpponentPlayer() {
        // player hand displayed with front sides
        List<CardSideSymbols> hand = new ArrayList<>();
        List<CardPlayableIF> handCards = this.gameState.getPlayerState(this.displayPlayer).getHandPlayable();
        for(int i=0 ; i<3 ; i++) {
//        for(CardPlayableIF card : this.gameState.getPlayerState(this.displayPlayer).getHandPlayable()) {
            CardPlayableIF card = i<handCards.size() ? handCards.get(i) : null;
            hand.add(new CardSideSymbols(card, Side.SIDEBACK));
        }

        // decks for resource cards and gold cards
        List<CardSideSymbols> deckRes = new ArrayList<>();
        List<CardSideSymbols> deckGold = new ArrayList<>();

        List<CardPlayableIF> pickables = this.gameState.getPickables();
        deckRes.add(new CardSideSymbols(pickables.get(0), Side.SIDEBACK)); // deck
        deckRes.add(new CardSideSymbols(pickables.get(1), Side.SIDEFRONT)); // option 1
        deckRes.add(new CardSideSymbols(pickables.get(2), Side.SIDEFRONT)); // option 2

        deckGold.add(new CardSideSymbols(pickables.get(3), Side.SIDEBACK)); // deck
        deckGold.add(new CardSideSymbols(pickables.get(4), Side.SIDEFRONT)); // option 1
        deckGold.add(new CardSideSymbols(pickables.get(5), Side.SIDEFRONT)); // option 2

        List<String> infoObj1 = ViewTUIPrintUtils.createInfoForObjective(gameState.getCommonObjectives().get(0));
        List<String> infoObj2 = ViewTUIPrintUtils.createInfoForObjective(gameState.getCommonObjectives().get(1));

        return String.format(
                """
                        ╔═══════════════════════════[▽ DRAWABLE CARDS ▽]════════════════════════╦═══════[▽ HAND ▽]══════╗
                        ║           DECKS                 OPTION 1               OPTION 2       ║ ┌───┬───%c───B───┬───┐ ║
                        ║   ┌───┬───%c───B───┬───┐  ┌───┬───%c───F───┬───┐  ┌───┬───%c───F───┬───┐ ║ │ %c │    %3s    │ %c │ ║
                        ║ R │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │ ║ ├───┘     %c     └───┤ ║
                        ║ E ├───┘     %c     └───┤  ├───┘    [%c]    └───┤  ├───┘    [%c]    └───┤ ║ ├───┐           ┌───┤ ║
                        ║ S ├───┐           ┌───┤  ├───┐           ┌───┤  ├───┐           ┌───┤ ║ │ %c │   %5s   │ %c │ ║
                        ║   │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │ ║ └───┴───────────┴───┘ ║
                        ║   └───┴───────────┴───┘  └───┴───────────┴───┘  └───┴───────────┴───┘ ║                       ║
                        ║   ┌───┬───%c───B───┬───┐  ┌───┬───%c───F───┬───┐  ┌───┬───%c───F───┬───┐ ║ ┌───┬───%c───B───┬───┐ ║
                        ║ G │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │ ║ │ %c │    %3s    │ %c │ ║
                        ║ O ├───┘     %c     └───┤  ├───┘    [%c]    └───┤  ├───┘    [%c]    └───┤ ║ ├───┘     %c     └───┤ ║
                        ║ L ├───┐           ┌───┤  ├───┐           ┌───┤  ├───┐           ┌───┤ ║ ├───┐           ┌───┤ ║
                        ║ D │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │ ║ │ %c │   %5s   │ %c │ ║
                        ║   └───┴───────────┴───┘  └───┴───────────┴───┘  └───┴───────────┴───┘ ║ └───┴───────────┴───┘ ║
                        ╠════════════════════════════[▽ OBJECTIVES ▽]═══════════════════════════╣                       ║
                        ║   ┌─────────.─────────┐  ┌─────────.─────────┐  ┌─────────.─────────┐ ║ ┌───┬───%c───B───┬───┐ ║
                        ║   │%s│  │%s│  │░░░░░░░░░░░░░░░░░░░│ ║ │ %c │    %3s    │ %c │ ║
                        ║   │%s│  │%s│  │░░░░░░░HIDDEN░░░░░░│ ║ ├───┘     %c     └───┤ ║
                        ║   │%s│  │%s│  │░░░░░░░░░░░░░░░░░░░│ ║ ├───┐           ┌───┤ ║
                        ║   │%s│  │%s│  │░░░░░░░░░░░░░░░░░░░│ ║ │ %c │   %5s   │ %c │ ║
                        ║   └───────────────────┘  └───────────────────┘  └───────────────────┘ ║ └───┴───────────┴───┘ ║
                        ╚═══════════════════════════════════════════════════════════════════════╩═══════════════════════╝
                        """,
                hand.get(0).type,
                deckRes.get(0).type, deckRes.get(1).type, deckRes.get(2).type, hand.get(0).corners[0], hand.get(0).points, hand.get(0).corners[1],
                deckRes.get(0).corners[0], deckRes.get(0).points, deckRes.get(0).corners[1], deckRes.get(1).corners[0], deckRes.get(1).points, deckRes.get(1).corners[1], deckRes.get(2).corners[0], deckRes.get(2).points, deckRes.get(2).corners[1], hand.get(0).color,
                deckRes.get(0).color, deckRes.get(1).color, deckRes.get(2).color,
                hand.get(0).corners[3], hand.get(0).points, hand.get(0).corners[2],
                deckRes.get(0).corners[3], deckRes.get(0).requirements, deckRes.get(0).corners[2], deckRes.get(1).corners[3], deckRes.get(1).requirements, deckRes.get(1).corners[2], deckRes.get(2).corners[3], deckRes.get(2).requirements, deckRes.get(2).corners[2],

                deckGold.get(0).type, deckGold.get(1).type, deckGold.get(2).type, hand.get(1).type,
                deckGold.get(0).corners[0], deckGold.get(0).points, deckGold.get(0).corners[1], deckGold.get(1).corners[0], deckGold.get(1).points, deckGold.get(1).corners[1], deckGold.get(2).corners[0], deckGold.get(2).points, deckGold.get(2).corners[1], hand.get(1).corners[0], hand.get(1).points, hand.get(1).corners[1],
                deckGold.get(0).color, deckGold.get(1).color, deckGold.get(2).color, hand.get(1).color,
                deckGold.get(0).corners[3], deckGold.get(0).requirements, deckGold.get(0).corners[2], deckGold.get(1).corners[3], deckGold.get(1).requirements, deckGold.get(1).corners[2], deckGold.get(2).corners[3], deckGold.get(2).requirements, deckGold.get(2).corners[2], hand.get(1).corners[3], hand.get(1).requirements, hand.get(1).corners[2],

                hand.get(2).type,
                infoObj1.get(0), infoObj2.get(0),
                hand.get(2).corners[0], hand.get(2).points, hand.get(2).corners[1],
                infoObj1.get(1), infoObj2.get(1),
                hand.get(2).color,
                infoObj1.get(2), infoObj2.get(2),
                infoObj1.get(3), infoObj2.get(3),
                hand.get(2).corners[3], hand.get(2).requirements, hand.get(2).corners[2]
        );
    }

    private List<List<Character>> cardToStr(CardSidePlayableIF cardSidePlayableIF){
        List<List<Character>> strCard=new ArrayList<>(3);
        List<Resource> cornerResources=cardSidePlayableIF.getCornerResources();
        List<Resource> centerResources=cardSidePlayableIF.getCenterResources();
        for (int i = 0; i < 3; i++) {
            strCard.add(i,new ArrayList<>(3));
        }
        strCard.get(0).add(0,ViewTUIConstants.resourceToSymbol(cornerResources.get(0)).charAt(0));
        strCard.get(0).add(1,'─');
        strCard.get(0).add(2,ViewTUIConstants.resourceToSymbol(cornerResources.get(1)).charAt(0));
        strCard.get(1).add(0,'│');
        strCard.get(1).add(1,' ');
        strCard.get(1).add(2,'│');
        strCard.get(2).add(0,ViewTUIConstants.resourceToSymbol(cornerResources.get(3)).charAt(0));
        strCard.get(2).add(1,'─');
        strCard.get(2).add(2,ViewTUIConstants.resourceToSymbol(cornerResources.get(2)).charAt(0));
        int startInd;
        if(centerResources.size()<3)
            startInd=1;
        else
            startInd=0;
        for (int i = 0; i < centerResources.size(); i++)
            strCard.get(1).set(startInd+i,ViewTUIConstants.resourceToSymbol(centerResources.get(i)).charAt(0));
        return strCard;
    }

    private List<List<Character>> availableStr(int index){
        List<List<Character>> strPos=new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            strPos.add(i,new ArrayList<>(3));
        }
        strPos.get(0).add(0,'┌');
        strPos.get(0).add(1,'─');
        strPos.get(0).add(2,'┐');
        strPos.get(1).add(0,(char)('0'+index/100));
        index-=index/100;
        strPos.get(1).add(1,(char)('0'+index/10));
        index-=index/10;
        strPos.get(1).add(2,(char)('0'+index));
        strPos.get(2).add(0,'└');
        strPos.get(2).add(1,'─');
        strPos.get(2).add(2,'┘');
        return strPos;
    }

    //TODO da eccezione, da sistemare
    private String genFieldString(PlayerLobby playerLobby){
        StringBuilder strField= new StringBuilder();
        try {
            CardSidePlayableIF starterCard=gameState.getPlayerState(playerLobby).getField().getCardSideAtCoord(new Coordinates(0,0));
            if(starterCard!=null) {
                Coordinates origin=new Coordinates(0, 0);
                int minX=0,maxX=0,minY=0,maxY=0;
                for(Coordinates coord : gameState.getPlayerState(playerLobby).getField().getPlacedCoords()){
                    if(coord.getPosX()<minX)
                        minX= coord.getPosX();
                    else if(coord.getPosX()>maxX)
                        maxX= coord.getPosX();
                    if(coord.getPosY()<minY)
                        minY= coord.getPosY();
                    else if(coord.getPosY()>maxY)
                        maxY= coord.getPosY();
                }
                int dimX=2*(maxX-minX+3)+1,dimY=2*(maxY-minY+3)+1;
                List<List<Character>> fieldMatrix=new ArrayList<>(dimY);
                List<Character> emptyLine= new ArrayList<>(dimX);
                for (int i = 0; i < dimX; i++) {
                    emptyLine.add(i,' ');
                }
                for (int i = 0; i < dimY; i++) {
                    fieldMatrix.add(i, emptyLine);
                }
                for(Coordinates coord : gameState.getPlayerState(playerLobby).getField().getPlacedCoords()){
                    CardSidePlayableIF cardSidePlayableIF=gameState.getPlayerState(playerLobby).getField().getCardSideAtCoord(coord);
                    List<List<Character>> strCard=cardToStr(cardSidePlayableIF);
                    int curY=2*(coord.getPosY()-minY+1),curX=2*(coord.getPosX()-minX+1);
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if(fieldMatrix.get(curY+j).get(curX+i)==' ')
                                fieldMatrix.get(curY+j).set(curX+i,strCard.get(j).get(i));
                        }
                    }
                    if(!cardSidePlayableIF.getCoveredCorners().get(0))
                        fieldMatrix.get(curY).set(curX,strCard.get(0).get(0));
                    if(!cardSidePlayableIF.getCoveredCorners().get(1))
                        fieldMatrix.get(curY).set(curX+2,strCard.get(0).get(2));
                    if(!cardSidePlayableIF.getCoveredCorners().get(2))
                        fieldMatrix.get(curY+2).set(curX+2,strCard.get(2).get(2));
                    if(!cardSidePlayableIF.getCoveredCorners().get(3))
                        fieldMatrix.get(curY+2).set(curX,strCard.get(2).get(0));
                }
                for(int i=0; i<gameState.getPlayerState(playerLobby).getField().getAvailableCoords().size();i++){
                    List<List<Character>> strCard=availableStr(i);
                    Coordinates coord=gameState.getPlayerState(playerLobby).getField().getAvailableCoords().get(i);
                    int curY=2*(coord.getPosY()-minY+1),curX=2*(coord.getPosX()-minX+1);
                    //System.out.println(coord.getPosX()+" "+coord.getPosY()+" "+curX+" "+curY);
                    for (int j = 0; j < 3; j++) {
                        for (int k = 0; k < 3; k++) {
                            if(fieldMatrix.get(curY+k).get(curX+j)==' ')
                                fieldMatrix.get(curY+k).set(curX+j,strCard.get(k).get(j));
                        }
                    }
                }
                for (int i = 0; i < dimY; i++) {
                    for (int j = 0; j < dimX; j++) {
                        strField.append(fieldMatrix.get(i).get(j));
                    }
                    strField.append('\n');
                }
            }
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
        return strField.toString();
    }
}
