package it.polimi.ingsw.am13.model.player;

/**
 * Represents token present in game.
 * It could be a token representing the color chosen by the player, the token of that color in the scoreboard,
 * or the black token indicating the first player
 */
public class Token {

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

}
