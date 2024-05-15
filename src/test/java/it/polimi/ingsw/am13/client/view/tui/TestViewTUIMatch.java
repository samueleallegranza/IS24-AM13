package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.controller.ListenerHandler;
import it.polimi.ingsw.am13.controller.LobbyException;
import it.polimi.ingsw.am13.controller.Room;
import it.polimi.ingsw.am13.model.GameModel;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.card.points.PointsCorner;
import it.polimi.ingsw.am13.model.card.points.PointsInstant;
import it.polimi.ingsw.am13.model.card.points.PointsResource;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayersNumberException;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.Player;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.model.player.Token;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TestViewTUIMatch {
    private final HashMap<Resource, Integer> norequirements = new HashMap<>();
    @Test
    public void testStrCardStarters() throws InvalidCardCreationException {
        CardSidePlayable cardFront = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.INSECT), new Corner(Resource.FUNGUS), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.FUNGUS),
                new PointsInstant(0),
                Color.NO_COLOR
        );
        CardSidePlayable cardBack = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.PLANT),
                new PointsInstant(0),
                Color.NO_COLOR
        );


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

        System.out.print(String.format(
                "%s──[Starter F]──%s   %s──[Starter F]──%s\n"+
                "│               │   │               │\n"+
                "│     %s %s %s     │   │     %s %s %s     │\n"+
                "│               │   │               │\n"+
                "%s───────────────%s   %s───────────────%s\n",
                frontCorners.get(0), frontCorners.get(1), backCorners.get(0), backCorners.get(1),
                frontCenter[0], frontCenter[1], frontCenter[2], backCenter[0], backCenter[1], backCenter[2],
                frontCorners.get(3), frontCorners.get(2), backCorners.get(3), backCorners.get(2)
        ));

        System.out.println(String.format(
                "┌───┬───S───F───┬───┐     ┌───┬───S───B───┬───┐\n" +
                "│ %s │           │ %s │     │ %s │           │ %s │\n" +
                "├───┘   %s %s %s   └───┤     ├───┘   %s %s %s   └───┤\n" +
                "├───┐           ┌───┤     ├───┐           ┌───┤\n" +
                "│ %s │           │ %s │     │ %s │           │ %s │\n" +
                "└───┴───────────┴───┘     └───┴───────────┴───┘\n",
                frontCorners.get(0), frontCorners.get(1), backCorners.get(0), backCorners.get(1),
                frontCenter[0], frontCenter[1], frontCenter[2], backCenter[0], backCenter[1], backCenter[2],
                frontCorners.get(3), frontCorners.get(2), backCorners.get(3), backCorners.get(2)

        ));
    }

    @Test
    public void testPlayerSection() {

            System.out.println(String.format(
                    "Turn   │         %c         │         %c         │         %c         │         %c         │\n" +
                    "Player │ %c  %-14s │ %c  %-14s │ %c  %-14s │ %c  %-14s │\n"+
                    "Points │        %2s         │        %2s         │        %2s         │        %2s         │\n",
                    '▼', ' ', ' ', ' ',
                    '✓', "samu02ita",
                    '✓', "hermione",
                    '⚠', "draco",
                    ' ', " ",
                    "11", "1", "30", "  "
            ));
    }

    @Test
    public void testCardSection1() {
        System.out.println(String.format(
                        "╔═══════════════════════════[▽ DRAWABLE CARDS ▽]════════════════════════╦═══════[▽ HAND ▽]══════╗\n" +
                        "║           DECKS                 OPTION 1               OPTION 2       ║ ┌─────────.─────────┐ ║\n" +
                        "║   ┌─────────.─────────┐  ┌─────────.─────────┐  ┌─────────.─────────┐ ║ │                   │ ║\n" +
                        "║ R │                   │  │                   │  │                   │ ║ │                   │ ║\n" +
                        "║ E │                   │  │                   │  │                   │ ║ │                   │ ║\n" +
                        "║ S │                   │  │                   │  │                   │ ║ │                   │ ║\n" +
                        "║   │                   │  │                   │  │                   │ ║ └───────────────────┘ ║\n" +
                        "║   └───────────────────┘  └───────────────────┘  └───────────────────┘ ║                       ║\n" +
                        "║   ┌─────────.─────────┐  ┌─────────.─────────┐  ┌─────────.─────────┐ ║ ┌─────────.─────────┐ ║\n" +
                        "║ G │                   │  │                   │  │                   │ ║ │                   │ ║\n" +
                        "║ O │                   │  │                   │  │                   │ ║ │                   │ ║\n" +
                        "║ L │                   │  │                   │  │                   │ ║ │                   │ ║\n" +
                        "║ D │                   │  │                   │  │                   │ ║ │                   │ ║\n" +
                        "║   └───────────────────┘  └───────────────────┘  └───────────────────┘ ║ └───────────────────┘ ║\n" +
                        "╠════════════════════════════[▽ OBJECTIVES ▽]═══════════════════════════╣                       ║\n" +
                        "║   ┌─────────.─────────┐  ┌─────────.─────────┐  ┌─────────.─────────┐ ║ ┌─────────.─────────┐ ║\n" +
                        "║   │                   │  │                   │  │                   │ ║ │                   │ ║\n" +
                        "║   │                   │  │                   │  │                   │ ║ │                   │ ║\n" +
                        "║   │                   │  │                   │  │                   │ ║ │                   │ ║\n" +
                        "║   │                   │  │                   │  │                   │ ║ │                   │ ║\n" +
                        "║   └───────────────────┘  └───────────────────┘  └───────────────────┘ ║ └───────────────────┘ ║\n" +
                        "╚═══════════════════════════════════════════════════════════════════════╩═══════════════════════╝\n"
        ));
    }

    @Test
    public void testCardSection2() {
        System.out.println(String.format(
                "╔═══════════════════════════[▽ DRAWABLE CARDS ▽]════════════════════════╦═══════[▽ HAND ▽]══════╗\n" +
                "║           DECKS                 OPTION 1               OPTION 2       ║ ┌───┬───c───F───┬───┐ ║\n"+
                "║   ┌───┬───c───B───┬───┐  ┌───┬───c───F───┬───┐  ┌───┬───c───F───┬───┐ ║ │ c │    ccc    │ c │ ║\n"+
                "║ R │ c │    ccc    │ c │  │ c │    ccc    │ c │  │ c │    ccc    │ c │ ║ ├───┘    [c]    └───┤ ║\n"+
                "║ E ├───┘     c     └───┤  ├───┘    [c]    └───┤  ├───┘    [c]    └───┤ ║ ├───┐           ┌───┤ ║\n"+
                "║ S ├───┐           ┌───┤  ├───┐           ┌───┤  ├───┐           ┌───┤ ║ │ c │   ccccc   │ c │ ║\n"+
                "║   │ c │   ccccc   │ c │  │ c │   ccccc   │ c │  │ c │   ccccc   │ c │ ║ └───┴───────────┴───┘ ║\n"+
                "║   └───┴───────────┴───┘  └───┴───────────┴───┘  └───┴───────────┴───┘ ║                       ║\n"+
                "║   ┌───┬───c───B───┬───┐  ┌───┬───c───F───┬───┐  ┌───┬───c───F───┬───┐ ║ ┌───┬───c───F───┬───┐ ║\n"+
                "║ G │ c │    ccc    │ c │  │ c │    ccc    │ c │  │ c │    ccc    │ c │ ║ │ c │    ccc    │ c │ ║\n"+
                "║ O ├───┘     c     └───┤  ├───┘     c     └───┤  ├───┘     c     └───┤ ║ ├───┘     c     └───┤ ║\n"+
                "║ L ├───┐           ┌───┤  ├───┐           ┌───┤  ├───┐           ┌───┤ ║ ├───┐           ┌───┤ ║\n"+
                "║ D │ c │   ccccc   │ c │  │ c │   ccccc   │ c │  │ c │   ccccc   │ c │ ║ │ c │   ccccc   │ c │ ║\n"+
                "║   └───┴───────────┴───┘  └───┴───────────┴───┘  └───┴───────────┴───┘ ║ └───┴───────────┴───┘ ║\n"+
                "╠════════════════════════════[▽ OBJECTIVES ▽]═══════════════════════════╣                       ║\n"+
                "║   ┌─────────.─────────┐  ┌─────────.─────────┐  ┌─────────.─────────┐ ║ ┌───┬───c───F───┬───┐ ║\n"+
                "║   │                   │  │                   │  │                   │ ║ │ c │    ccc    │ c │ ║\n"+
                "║   │       FIX ME      │  │       FIX ME      │  │       FIX ME      │ ║ ├───┘     c     └───┤ ║\n"+
                "║   │                   │  │                   │  │                   │ ║ ├───┐           ┌───┤ ║\n"+
                "║   │                   │  │                   │  │                   │ ║ │ c │   ccccc   │ c │ ║\n"+
                "║   └───────────────────┘  └───────────────────┘  └───────────────────┘ ║ └───┴───────────┴───┘ ║\n"+
                "╚═══════════════════════════════════════════════════════════════════════╩═══════════════════════╝\n"
        ));
    }


    @Test
    public void testCardSection3() {
        System.out.println(
                "╔═══════════════════════════[▽ DRAWABLE CARDS ▽]════════════════════════╦═══════[▽ HAND ▽]══════╗\n" +
                "║           DECKS                 OPTION 1               OPTION 2       ║ ┌───┬───%c───F───┬───┐ ║\n"+
                "║   ┌───┬───%c───B───┬───┐  ┌───┬───%c───F───┬───┐  ┌───┬───%c───F───┬───┐ ║ │ %c │    %3s    │ %c │ ║\n"+
                "║ R │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │ ║ ├───┘    [%c]    └───┤ ║\n"+
                "║ E ├───┘     %c     └───┤  ├───┘    [%c]    └───┤  ├───┘    [%c]    └───┤ ║ ├───┐           ┌───┤ ║\n"+
                "║ S ├───┐           ┌───┤  ├───┐           ┌───┤  ├───┐           ┌───┤ ║ │ %c │   %5s   │ %c │ ║\n"+
                "║   │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │ ║ └───┴───────────┴───┘ ║\n"+
                "║   └───┴───────────┴───┘  └───┴───────────┴───┘  └───┴───────────┴───┘ ║                       ║\n"+
                "║   ┌───┬───%c───B───┬───┐  ┌───┬───%c───F───┬───┐  ┌───┬───%c───F───┬───┐ ║ ┌───┬───%c───F───┬───┐ ║\n"+
                "║ G │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │ ║ │ %c │    %3c    │ %c │ ║\n"+
                "║ O ├───┘     %c     └───┤  ├───┘    [%c]    └───┤  ├───┘    [%c]    └───┤ ║ ├───┘    [%c]    └───┤ ║\n"+
                "║ L ├───┐           ┌───┤  ├───┐           ┌───┤  ├───┐           ┌───┤ ║ ├───┐           ┌───┤ ║\n"+
                "║ D │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │ ║ │ %c │   %5s   │ %c │ ║\n"+
                "║   └───┴───────────┴───┘  └───┴───────────┴───┘  └───┴───────────┴───┘ ║ └───┴───────────┴───┘ ║\n"+
                "╠════════════════════════════[▽ OBJECTIVES ▽]═══════════════════════════╣                       ║\n"+
                "║   ┌─────────.─────────┐  ┌─────────.─────────┐  ┌─────────.─────────┐ ║ ┌───┬───%c───F───┬───┐ ║\n"+
                "║   │                   │  │                   │  │                   │ ║ │ %c │    %3s    │ %c │ ║\n"+
                "║   │       FIX ME      │  │       FIX ME      │  │       FIX ME      │ ║ ├───┘    [%c]    └───┤ ║\n"+
                "║   │                   │  │                   │  │                   │ ║ ├───┐           ┌───┤ ║\n"+
                "║   │                   │  │                   │  │                   │ ║ │ %c │   %5s   │ %c │ ║\n"+
                "║   └───────────────────┘  └───────────────────┘  └───────────────────┘ ║ └───┴───────────┴───┘ ║\n"+
                "╚═══════════════════════════════════════════════════════════════════════╩═══════════════════════╝\n"
        );
    }


    @Test
    public void testCardSectionOpponent() {
        System.out.println(
                "╔═══════════════════════════[▽ DRAWABLE CARDS ▽]════════════════════════╦═══════[▽ HAND ▽]══════╗\n" +
                "║           DECKS                 OPTION 1               OPTION 2       ║ ┌───┬───%c───B───┬───┐ ║\n"+
                "║   ┌───┬───%c───B───┬───┐  ┌───┬───%c───F───┬───┐  ┌───┬───%c───F───┬───┐ ║ │ %c │    %3s    │ %c │ ║\n"+
                "║ R │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │ ║ ├───┘     %c     └───┤ ║\n"+
                "║ E ├───┘     %c     └───┤  ├───┘    [%c]    └───┤  ├───┘    [%c]    └───┤ ║ ├───┐           ┌───┤ ║\n"+
                "║ S ├───┐           ┌───┤  ├───┐           ┌───┤  ├───┐           ┌───┤ ║ │ %c │   %5s   │ %c │ ║\n"+
                "║   │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │ ║ └───┴───────────┴───┘ ║\n"+
                "║   └───┴───────────┴───┘  └───┴───────────┴───┘  └───┴───────────┴───┘ ║                       ║\n"+
                "║   ┌───┬───%c───B───┬───┐  ┌───┬───%c───F───┬───┐  ┌───┬───%c───F───┬───┐ ║ ┌───┬───%c───B───┬───┐ ║\n"+
                "║ G │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │  │ %c │    %3s    │ %c │ ║ │ %c │    %3c    │ %c │ ║\n"+
                "║ O ├───┘     %c     └───┤  ├───┘    [%c]    └───┤  ├───┘    [%c]    └───┤ ║ ├───┘     %c     └───┤ ║\n"+
                "║ L ├───┐           ┌───┤  ├───┐           ┌───┤  ├───┐           ┌───┤ ║ ├───┐           ┌───┤ ║\n"+
                "║ D │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │  │ %c │   %5s   │ %c │ ║ │ %c │   %5s   │ %c │ ║\n"+
                "║   └───┴───────────┴───┘  └───┴───────────┴───┘  └───┴───────────┴───┘ ║ └───┴───────────┴───┘ ║\n"+
                "╠════════════════════════════[▽ OBJECTIVES ▽]═══════════════════════════╣                       ║\n"+
                "║   ┌─────────.─────────┐  ┌─────────.─────────┐  ┌─────────.─────────┐ ║ ┌───┬───%c───B───┬───┐ ║\n"+
                "║   │                   │  │                   │  │░░░░░░░░░░░░░░░░░░░│ ║ │ %c │    %3s    │ %c │ ║\n"+
                "║   │       FIX ME      │  │       FIX ME      │  │░░░░░░░HIDDEN░░░░░░│ ║ ├───┘     %c     └───┤ ║\n"+
                "║   │                   │  │                   │  │░░░░░░░░░░░░░░░░░░░│ ║ ├───┐           ┌───┤ ║\n"+
                "║   │                   │  │                   │  │░░░░░░░░░░░░░░░░░░░│ ║ │ %c │   %5s   │ %c │ ║\n"+
                "║   └───────────────────┘  └───────────────────┘  └───────────────────┘ ║ └───┴───────────┴───┘ ║\n"+
                "╚═══════════════════════════════════════════════════════════════════════╩═══════════════════════╝\n"
        );
    }


    private class CardSideSymbols {
        public Character type; // gold/resource -> G/R
        public Character side; // front/back -> F/B
        public Character[] corners; // -> {x,y,z,k}
        public String points; // points -> "2xK" / " 2 "
        public Character color; // resource color -> 'x'
        public String requirements; // requirements -> "  x  " / " xx  " / " xxx " / "xxxx " / "xxxxx"

        public CardSideSymbols(CardPlayableIF c, Side s) {
            CardSidePlayableIF cs = c.getSide(s);

            this.type = c instanceof CardGold ? 'G' : 'R';
            this.side = s.equals(Side.SIDEFRONT) ? 'F' : 'B';

            List<Resource> cornerRes = cs.getCornerResources();
            this.corners = new Character[4];
            for(int i=0; i<4; i++) this.corners[i] = ViewTUIConstants.resourceToSymbol(cornerRes.get(i)).charAt(0);

            if(cs.getPoints().isCornerTypePoints())
                this.points = String.format("%d×%c", cs.getPoints().getPointsMultiplier(), ViewTUIConstants.POINTS_PATTERN_ANGLE.charAt(0));
            else if(cs.getPoints().getPointsResource() != Resource.NO_RESOURCE) {
                this.points = String.format("%d×%c", cs.getPoints().getPointsMultiplier(), ViewTUIConstants.resourceToSymbol(cs.getPoints().getPointsResource()).charAt(0));
            } else {
                this.points = String.format(" %d ", cs.getPoints().getPointsMultiplier());
            }

            this.color = ViewTUIConstants.resourceToSymbol(cs.getColor().correspondingResource()).charAt(0);

            List<Character> requirementList = new ArrayList<>();
            for(Resource r: cs.getRequirements().keySet()) for(int i=0; i<cs.getRequirements().get(r); i++) requirementList.add(ViewTUIConstants.resourceToSymbol(r).charAt(0));
            switch (requirementList.size()){
                case 0: { this.requirements = "     "; break;}
                case 1: { this.requirements = String.format("  %c  ", requirementList.getFirst()); break;}
                case 2: { this.requirements = String.format(" %c%c  ", requirementList.get(0), requirementList.get(1)); break;}
                case 3: { this.requirements = String.format(" %c%c%c ", requirementList.get(0), requirementList.get(1), requirementList.get(2)); break;}
                case 4: { this.requirements = String.format("%c%c%c%c ", requirementList.get(0), requirementList.get(1), requirementList.get(2), requirementList.get(3)); break;}
                case 5: { this.requirements = requirementList.stream().map(String::valueOf).collect(Collectors.joining()); }
            }
        }
    }

    @Test void testCardSymbol() throws InvalidCardCreationException {
        HashMap<Resource, Integer> customreq = new HashMap<>();
        customreq.put(Resource.PLANT, 1);
        customreq.put(Resource.INSECT, 1);
        customreq.put(Resource.FUNGUS, 3);;

        CardSidePlayable cardFront = new CardSidePlayable(
                customreq,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.INSECT), new Corner(Resource.FUNGUS), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.FUNGUS),
                new PointsCorner(1),
                Color.INSECT
        );
        CardSidePlayable cardBack = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.PLANT),
                new PointsResource(5,Resource.ANIMAL),
                Color.NO_COLOR
        );
        CardPlayableIF card = new CardGold("testXXX", cardFront, cardBack);
        CardSideSymbols sym = new CardSideSymbols(card, Side.SIDEFRONT);
        System.out.println(sym.type);
        System.out.println(sym.side);
        System.out.println(Arrays.toString(sym.corners));
        System.out.println(">" + sym.points + "<");
        System.out.println(sym.color);
        System.out.println(">" + sym.requirements + "<");
    }
}
