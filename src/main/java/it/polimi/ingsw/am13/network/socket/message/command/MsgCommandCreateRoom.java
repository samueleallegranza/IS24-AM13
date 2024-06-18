package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.model.player.Token;

import java.io.Serializable;

/**
 * Command message which is sent to create a room
 */
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
     * The room capacity.
     */
    private final int players;

    /**
     * Creates the message by setting the attributes to the parameters
     * @param chosenNickname the nickname of the client who wants to create a room
     * @param token the color token chosen by the player who wants to create a room
     * @param players the capacity (number of players) of the room
     */
    public MsgCommandCreateRoom(String chosenNickname, Token token, int players) {
        super();
        this.chosenNickname = chosenNickname;
        this.token = token;
        this.players = players;
    }

    /**
     *
     * @return the nickname the client wants to create the room with
     */
    public String getChosenNickname() {
        return chosenNickname;
    }

    /**
     *
     * @return the capacity of the room
     */
    public int getPlayers() {
        return players;
    }

    /**
     *
     * @return the color token of the player who wants to create the room
     */
    public Token getToken() {
        return token;
    }
}
