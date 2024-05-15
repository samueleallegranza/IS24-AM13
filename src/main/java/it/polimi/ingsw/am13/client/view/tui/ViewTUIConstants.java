package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.model.card.Resource;

import java.time.format.DateTimeFormatter;


// could be replaced with enum
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
    public static final String ANGLE_NORESOURCE_SYMBOL= "■";

    // An angle which is not linkable
    public static final String ANGLE_NOTLINKABLE_SYMBOL = "□";



    public static final String POINTS_PATTERN_ANGLE = "▖";


    // TODO: fix to map
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


}
