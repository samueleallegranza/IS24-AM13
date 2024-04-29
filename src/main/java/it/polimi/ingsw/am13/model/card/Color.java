package it.polimi.ingsw.am13.model.card;

import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;

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
     * @return Resource corresponding to this color
     * @throws InvalidCardCreationException If the color is NO_COLOR (there's trivially no resource associated to no color)
     */
    public Resource correspondingResource() throws InvalidCardCreationException {
        if(this == NO_COLOR)
            throw new InvalidCardCreationException("You're trying to create fetch a resource from coloro NO_COLOR");
        return Resource.valueOf(this.name());
    }
}