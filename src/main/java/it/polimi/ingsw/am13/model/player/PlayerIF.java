package it.polimi.ingsw.am13.model.player;

public interface PlayerIF {

    /**
     * @return Player's nickname.
     */
    String getNickname();

    /**
     * @return Player's token (indicates their color).
     */
    Token getToken();
}
