package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.model.card.Resource;


public final class TUIUtils {
    public static String resourceToSymbol(Resource r) {
        switch (r) {
            case FUNGUS -> {return TUIConstants.FUNGUS_SYMBOL;}
            case ANIMAL -> {return TUIConstants.ANIMAL_SYMBOL;}
            case PLANT -> {return TUIConstants.PLANT_SYMBOL;}
            case INSECT -> {return TUIConstants.INSECT_SYMBOL;}
            case QUILL -> {return TUIConstants.QUILL_SYMBOL;}
            case INKWELL -> {return TUIConstants.INKWELL_SYMBOL;}
            case MANUSCRIPT -> {return TUIConstants.MANUSCRIPT_SYMBOL;}
            case NO_RESOURCE -> {return TUIConstants.ANGLE_NORESOURCE_SYMBOL;}

            default -> {return "?";}
        }
    }

    public static String resourceToName(Resource r) {
        switch (r) {
            case FUNGUS -> {return "Fungus";}
            case ANIMAL -> {return "Animal";}
            case PLANT -> {return "Plant";}
            case INSECT -> {return "Insect";}
            case QUILL -> {return "Quill";}
            case INKWELL -> {return "Inkwell";}
            case MANUSCRIPT -> {return "Manuscript";}
            case NO_RESOURCE -> {return "no resource";}

            default -> {return "unknown";}
        }
    }
}

