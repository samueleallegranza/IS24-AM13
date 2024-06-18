package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.model.player.Token;

import java.io.Serializable;

/**
 * Command message to reconnect to an existing game, containing nickname and token of the player who wants to reconnect
 */
public class MsgCommandReconnectGame extends MsgCommand implements Serializable {
    /**
     * Nickname of the player who wants to reconnect
     */
    private final String nickname;
    /**
     * Token of the player who wants to reconnect
     */
    private final Token token;

    /**
     * Creates the message by setting the two attributes to the parameters
     * @param nickname of the player who wants to reconnect
     * @param token of the player who wants to reconnect
     */
    public MsgCommandReconnectGame(String nickname, Token token) {
        this.nickname = nickname;
        this.token = token;
    }

    /**
     *
     * @return the nickname of the player who wants to reconnect
     */
    public String getNickname() {
        return nickname;
    }

    /**
     *
     * @return the token of the player who wants to reconnect
     */
    public Token getToken() {
        return token;
    }
}
