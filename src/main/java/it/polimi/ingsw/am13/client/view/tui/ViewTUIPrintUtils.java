package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.model.card.*;

import java.util.List;

public class ViewTUIPrintUtils {

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

    public static String starterCards(CardSidePlayableIF cardFront, CardSidePlayableIF cardBack) {
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
                """
                        ┌───┬───S───F───┬───┐     ┌───┬───S───B───┬───┐
                        │ %s │           │ %s │     │ %s │           │ %s │
                        ├───┘   %s %s %s   └───┤     ├───┘   %s %s %s   └───┤
                        ├───┐           ┌───┤     ├───┐           ┌───┤
                        │ %s │           │ %s │     │ %s │           │ %s │
                        └───┴───────────┴───┘     └───┴───────────┴───┘
                        """,
                frontCorners.get(0), frontCorners.get(1), backCorners.get(0), backCorners.get(1),
                frontCenter[0], frontCenter[1], frontCenter[2], backCenter[0], backCenter[1], backCenter[2],
                frontCorners.get(3), frontCorners.get(2), backCorners.get(3), backCorners.get(2)
        );
    }

    public static String objectiveCards(CardObjectiveIF obj1, CardObjectiveIF obj2) {
        // FIXME: Dont have access to CardObjectiveIF informations!
        return String.format(
                """
                        ┌─────OBJECTIVE─────┐     ┌─────OBJECTIVE─────┐
                        │                   │     │                   │
                        │         %s        │     │         %s        │
                        │                   │     │                   │
                        │                   │     │                   │
                        └───────────────────┘     └───────────────────┘
                        """,
                obj1.getId(),
                obj2.getId()
        );
    }

}
