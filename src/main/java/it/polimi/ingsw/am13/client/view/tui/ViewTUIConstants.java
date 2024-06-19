package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.model.card.Resource;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


// could be replaced with enum

/**
 * Class containing various constants used by view TUI
 */
public final class ViewTUIConstants {
    public static final int LOG_MAXLINES = 6;
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

//    PLANT, ANIMAL, FUNGUS, INSECT, QUILL, INKWELL, MANUSCRIPT, NO_RESOURCE

    public static final String FUNGUS_SYMBOL = "*";
    public static final String ANIMAL_SYMBOL = "+";
    public static final String PLANT_SYMBOL = "^";
    public static final String INSECT_SYMBOL = "~";
    public static final String QUILL_SYMBOL = "\"";
    public static final String INKWELL_SYMBOL = "|";
    public static final String MANUSCRIPT_SYMBOL = "@";

    // An angle which is linkable but has no resource in it
    public static final String ANGLE_NORESOURCE_SYMBOL= "□";

    // An angle which is not linkable
    public static final String ANGLE_NOTLINKABLE_SYMBOL = "■";

    public static final String POINTS_PATTERN_ANGLE = "▖";

    // CONSOLE COLORING STUFF
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    // TODO: fix to map (use char instead of a string?)
    /**
     * Returns the symbol associated to a resource
     * @param r a resource
     * @return the symbol associated to the resource
     */
    public static String resourceToSymbol(Resource r) {
        switch (r) {
            case FUNGUS -> {return ViewTUIConstants.FUNGUS_SYMBOL;}
            case ANIMAL -> {return ViewTUIConstants.ANIMAL_SYMBOL;}
            case PLANT -> {return ViewTUIConstants.PLANT_SYMBOL;}
            case INSECT -> {return ViewTUIConstants.INSECT_SYMBOL;}
            case QUILL -> {return ViewTUIConstants.QUILL_SYMBOL;}
            case INKWELL -> {return ViewTUIConstants.INKWELL_SYMBOL;}
            case MANUSCRIPT -> {return ViewTUIConstants.MANUSCRIPT_SYMBOL;}
            case NO_RESOURCE -> {return ViewTUIConstants.ANGLE_NORESOURCE_SYMBOL;}

            default -> {return "?";}
        }
    }

    /**
     * Returns the colored version of this player's nickname, based on the color of the token
     * @param player the player
     * @return the colored version of the player's nickname
     */
    public static String colorNickname(PlayerLobby player) {
        if (player == null) return "-";
        return switch (player.getToken().getColor()) {
            case RED -> ANSI_RED + player.getNickname() + ANSI_RESET;
            case GREEN -> ANSI_GREEN + player.getNickname() + ANSI_RESET;
            case YELLOW -> ANSI_YELLOW + player.getNickname() + ANSI_RESET;
            case BLUE -> ANSI_BLUE + player.getNickname() + ANSI_RESET;
        };
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

}
