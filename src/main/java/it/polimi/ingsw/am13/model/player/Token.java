package it.polimi.ingsw.am13.model.player;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents token present in game.
 * It could be a token representing the color chosen by the player, the token of that color in the scoreboard,
 * or the black token indicating the first player
 */
public class Token implements Serializable {

    /**
     * Color of the token
     */
    private final ColorToken color;

    /**
     * @param color Color of the token
     */
    public Token(ColorToken color) {
        this.color = color;
    }

    /**
     * @return Color of the token
     */
    public ColorToken getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return color == token.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}
