package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.model.player.Token;

import java.io.Serializable;

public class MsgCommandReconnectGame extends MsgCommand implements Serializable {
    private final String nickname;
    private final Token token;

    public MsgCommandReconnectGame(String nickname, Token token) {
        this.nickname = nickname;
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public Token getToken() {
        return token;
    }
}
