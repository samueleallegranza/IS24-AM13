package it.polimi.ingsw.am13.client.view.tui;

import java.time.format.DateTimeFormatter;


// could be replaced with enum
public final class TUIConstants {
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

}
