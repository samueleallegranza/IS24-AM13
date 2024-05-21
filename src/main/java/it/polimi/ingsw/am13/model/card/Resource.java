package it.polimi.ingsw.am13.model.card;

import java.io.Serializable;

/**
 * Enum of all the possible resources, plus NO_RESOURCE for corners that are empty or non-visible
 */
public enum Resource implements Serializable {
    PLANT, ANIMAL, FUNGUS, INSECT, QUILL, INKWELL, MANUSCRIPT, NO_RESOURCE
}
