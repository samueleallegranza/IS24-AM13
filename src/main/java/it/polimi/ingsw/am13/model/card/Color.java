package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;

import java.io.Serializable;
import java.security.InvalidParameterException;

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
     * @return Resource corresponding to this color
     */
    public Resource correspondingResource() {
        if(this == NO_COLOR)
            throw new InvalidParameterException("You're trying to fetch a resource from color NO_COLOR");
        return Resource.valueOf(this.name());
    }
}