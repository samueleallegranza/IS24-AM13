package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.card.points.PointsObjective;
import it.polimi.ingsw.am13.model.card.points.PointsPattern;
import it.polimi.ingsw.am13.model.card.points.PointsSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewTUIPrintUtils {


    public static class CardSideSymbolsBuilder {
        public Character type; // gold/resource/starter -> G/R/S
        public Character side; // front/back -> F/B
        public Character[] corners; // -> {x,y,z,k}
        public String points; // points -> "2xK" / " 2 "
        public String color; // resource color -> " x " / "[x]"
        public String requirements; // requirements -> "  x  " / " xx  " / " xxx " / "xxxx " / "xxxxx"
        public String centerResourcesStarter; // "xyz"

        public CardSideSymbolsBuilder(CardPlayableIF c, Side s) {
            // if card is null, we mush show an empty one
            if(c == null) {
                type = '─';
                side = '─';
                corners = new Character[4];
                for(int i=0 ; i<4 ; i++)
                    corners[i] = ' ';
                points = "   ";
                color = "   ";
                requirements = "     ";
            } else {

                CardSidePlayableIF cs = c.getSide(s);

                switch (c) {
                    case CardGold ignored -> this.type = 'G';
                    case CardResource ignored -> this.type = 'R';
                    case CardStarter ignored -> {
                        this.type = 'S';

                        List<Resource> centerRes = cs.getCenterResources();
                        StringBuilder centerResTemp = new StringBuilder("   ");
                        for(int i=0; i<centerRes.size(); i++) centerResTemp.setCharAt(i, ViewTUIConstants.resourceToSymbol(centerRes.get(i)).charAt(0));
                        this.centerResourcesStarter = centerResTemp.toString();
                    }
                    default -> this.type = '?';
                }

                this.side = s.equals(Side.SIDEFRONT) ? 'F' : 'B';

                List<Resource> cornerRes = cs.getCornerResources();
                List<Corner> cornerList = cs.getCorners();

                this.corners = new Character[4];
                for (int i = 0; i < 4; i++) {
                    if(cornerList.get(i).isPlaceable())
                        this.corners[i] = ViewTUIConstants.resourceToSymbol(cornerRes.get(i)).charAt(0);
                    else
                        this.corners[i] = ViewTUIConstants.ANGLE_NOTLINKABLE_SYMBOL.charAt(0);
                }

                if (cs.getPoints() != null) {
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

                if(s.equals(Side.SIDEBACK)) {
                    this.color = " " + ViewTUIConstants.resourceToSymbol(cs.getColor().correspondingResource()).charAt(0) + " ";
                } else {
                    this.color = "[" + ViewTUIConstants.resourceToSymbol(cs.getColor().correspondingResource()).charAt(0) + "]";
                }

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


    public static void printStartup(boolean isSocket, String ip, int port) {
        String options = String.format(
                """
                        \t> %s Mode
                        \t> %s Connection type
                        \t> Server address: %s:%d
                        """,
                "TUI",
                isSocket ? "Socket" : "RMI",
                ip, port
        );

        System.out.print(
                "\n" +
                        "      ...                         ..                               \n" +
                        "   xH88\"`~ .x8X                 dF                                 \n" +
                        " :8888   .f\"8888Hf        u.   '88bu.                    uL   ..   \n" +
                        ":8888>  X8L  ^\"\"`   ...ue888b  '*88888bu        .u     .@88b  @88R \n" +
                        "X8888  X888h        888R Y888r   ^\"*8888N    ud8888.  '\"Y888k/\"*P  \n" +
                        "88888  !88888.      888R I888>  beWE \"888L :888'8888.    Y888L     \n" +
                        "88888   %88888      888R I888>  888E  888E d888 '88%\"     8888     \n" +
                        "88888 '> `8888>     888R I888>  888E  888E 8888.+\"        `888N    \n" +
                        "`8888L %  ?888   ! u8888cJ888   888E  888F 8888L       .u./\"888&   \n" +
                        " `8888  `-*\"\"   /   \"*888*P\"   .888N..888  '8888c. .+ d888\" Y888*\" \n" +
                        "   \"888.      :\"      'Y\"       `\"888*\"\"    \"88888%   ` \"Y   Y\"    \n" +
                        "     `\"\"***~\"`                     \"\"         \"YP'                 \n" +
                        "                                                                   \n" +
                        "Authors:  Samuele Allegranza, Matteo Arrigo,\n" +
                        "          Lorenzo Battini, Federico Bulloni\n\n" +
                        "Startup options:\n"+ options + "\n"
        );
    }

    public static String starterCards(CardPlayableIF cardStarter) {
        CardSideSymbolsBuilder cardFront = new CardSideSymbolsBuilder(cardStarter, Side.SIDEFRONT);
        CardSideSymbolsBuilder cardBack = new CardSideSymbolsBuilder(cardStarter, Side.SIDEBACK);

        return String.format(
                """
                        ┌───┬───%c───%c───┬───┐     ┌───┬───%c───%c───┬───┐
                        │ %c │           │ %c │     │ %c │           │ %c │
                        ├───┘    %s    └───┤     ├───┘    %s    └───┤
                        ├───┐           ┌───┤     ├───┐           ┌───┤
                        │ %c │           │ %c │     │ %c │           │ %c │
                        └───┴───────────┴───┘     └───┴───────────┴───┘
                """,
                cardFront.type, cardFront.side, cardBack.type, cardBack.side,
                cardFront.corners[0], cardFront.corners[1], cardBack.corners[0], cardBack.corners[1],
                cardFront.centerResourcesStarter, cardBack.centerResourcesStarter,
                cardFront.corners[3], cardFront.corners[2], cardBack.corners[3], cardBack.corners[2]


        );
    }

    public static List<String> createInfoForObjective(CardObjectiveIF obj) {
        PointsObjective points = obj.getPoints();
        List<String> infos = new ArrayList<>();
        switch (points) {
            case PointsSet p -> {
                infos.add("      " + p.getPointsMultiplier() + " - SET      ");
                Map<Resource, Integer> set = p.getSet();
                for(Resource r : set.keySet()) {
                    infos.add(String.format("      %s  (x%d)      ",
                            ViewTUIConstants.resourceToSymbol(r),
                            set.get(r)));
                }
                while(infos.size()<4)
                    infos.add("                   ");
            }
            case PointsPattern p -> {
                String pos1;
                if(p.getVec12().getPosX()==-1)
                    pos1 = "LT";
                else if(p.getVec12().getPosX()==0)
                    pos1 = "DN";
                else
                    pos1 = "RT";
                String pos2;
                if(p.getVec13().getPosX() - p.getVec12().getPosX() == -1)
                    pos2 = "LT";
                else if(p.getVec13().getPosX() - p.getVec12().getPosX() == 0)
                    pos2 = "DN";
                else
                    pos2 = "RT";

                infos.add("    " + p.getPointsMultiplier() + " - PATTERN    ");
                infos.add(String.format("      %6s       ", p.getColor1()));
                infos.add(String.format("    %6s - %s    ",
                        p.getColor2(), pos1 ));
                infos.add(String.format("    %6s - %s    ",
                        p.getColor3(), pos2 ));
                infos.add("                   ");
            }
            default -> throw new IllegalStateException("Unexpected value: " + points);
        }
        return infos;
    }

    public static String objectiveCards(CardObjectiveIF obj1, CardObjectiveIF obj2) {
        // FIXME: Dont have access to CardObjectiveIF informations!
        List<String> info1 = createInfoForObjective(obj1);
        List<String> info2 = createInfoForObjective(obj2);

        return String.format(
                """
                        ┌─────OBJECTIVE─────┐     ┌─────OBJECTIVE─────┐
                        │%s│     │%s│
                        │%s│     │%s│
                        │%s│     │%s│
                        │%s│     │%s│
                        └───────────────────┘     └───────────────────┘
                        """,
                info1.get(0), info2.get(0),
                info1.get(1), info2.get(1),
                info1.get(2), info2.get(2),
                info1.get(3), info2.get(3)
        );

    }

}
