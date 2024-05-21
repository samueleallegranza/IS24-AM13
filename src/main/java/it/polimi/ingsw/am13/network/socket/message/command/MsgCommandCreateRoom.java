package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.model.player.Token;

import java.io.Serializable;

public class MsgCommandCreateRoom extends MsgCommand implements Serializable {
    /**
     * The nickname the client wants to create the room with.
     * Specified here because we don't know if it is a valid one!
     */
    private final String chosenNickname;

    /**
     * The chosen token color. Can't be invalid.
     */
    private final Token token;

    /**
     * The chosen room capacity.
     */
    private final int players;


    public MsgCommandCreateRoom(String chosenNickname, Token token, int players) {
        super();
        this.chosenNickname = chosenNickname;
        this.token = token;
        this.players = players;
    }

    public String getChosenNickname() {
        return chosenNickname;
    }

    public int getPlayers() {
        return players;
    }

    public Token getToken() {
        return token;
    }
}
