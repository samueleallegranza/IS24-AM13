package it.polimi.ingsw.am13.model.card;


import java.io.Serializable;

/**
 * Represents color of a card side.
 * The color is named as the corresponding resource.
 * Hence:
 * <ul>
 *     <li>Plant = Green</li>
 *     <li>Animal = Blue</li>
 *     <li>Fungus = Red</li>
 *     <li>Insect = Purple</li>
 * </ul>
 * NO_COLOR represent a side with no color (as for starter cards).
 */
public enum Color implements Serializable {
    PLANT, ANIMAL, FUNGUS, INSECT, NO_COLOR;

    /**
     * Gets the resource associated to the color of a card.
     * @return Resource corresponding to this color (NO_RESOURCE for NO_COLOR)
     */
    public Resource correspondingResource() {
        return this!=NO_COLOR ? Resource.valueOf(this.name()) : Resource.NO_RESOURCE;
    }
}