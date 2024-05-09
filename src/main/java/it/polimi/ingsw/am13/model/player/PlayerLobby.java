package it.polimi.ingsw.am13.model.player;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable class representing a player not yet in game.
 * It stores only the information about nickname and token chosen.
 * 2 objects of this class are equals iff they have same nickname and color of token
 */
public final class PlayerLobby implements Serializable {

    /**
     * Player's nickname.
     */
    private final String nickname;

    /**
     * Player's token (indicates their color).
     */
    private final Token token;

    /**
     * @param nickname Nickname of the player
     * @param token Token chosen by the player
     */
    public PlayerLobby(String nickname, Token token) {
        this.nickname = nickname;
        this.token = token;
    }

    /**
     * @param nickname Nickname of the player
     * @param colorToken Color of the token chosen by the player
     */
    public PlayerLobby(String nickname, ColorToken colorToken) {
        this.nickname = nickname;
        this.token = new Token(colorToken);
    }

    /**
     * @return Player's nickname.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return Player's token (indicates their color).
     */
    public Token getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerLobby that = (PlayerLobby) o;
//        return Objects.equals(nickname, that.nickname);
        return Objects.equals(nickname, that.nickname) && Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, token);
    }
}
