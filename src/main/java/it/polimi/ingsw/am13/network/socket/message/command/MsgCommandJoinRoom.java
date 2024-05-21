package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.model.player.Token;

import java.io.Serializable;

public class MsgCommandJoinRoom extends MsgCommand implements Serializable {
    /**
     * The nickname the client wants to join the room with.
     * Specified here because we don't know if it is a valid one!
     */
    private final String chosenNickname;

    /**
     * The chosen token color.
     */
    private final Token token;

    private final int gameId;

    public MsgCommandJoinRoom(String chosenNickname, Token token, int gameId) {
        super();
        this.chosenNickname = chosenNickname;
        this.token = token;
        this.gameId = gameId;
    }

    public String getChosenNickname() {
        return chosenNickname;
    }

    public int getGameId() {
        return gameId;
    }

    public Token getToken() {
        return token;
    }
}
