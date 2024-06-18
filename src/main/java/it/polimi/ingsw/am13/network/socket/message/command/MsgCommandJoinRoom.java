package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.model.player.Token;

import java.io.Serializable;

/**
 * Command message which is sent to join a room
 */
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

    /**
     * The id of the game that the client wants to join
     */
    private final int gameId;

    /**
     * Creates the message by setting the attributes to the parameters
     * @param chosenNickname the nickname the client wants to join the room with
     * @param token chosen by the player who wants to join the room
     * @param gameId of the game that the client wants to join
     */
    public MsgCommandJoinRoom(String chosenNickname, Token token, int gameId) {
        super();
        this.chosenNickname = chosenNickname;
        this.token = token;
        this.gameId = gameId;
    }

    /**
     *
     * @return the nickname the client wants to join the room with
     */
    public String getChosenNickname() {
        return chosenNickname;
    }

    /**
     *
     * @return the color token chosen by the player who wants to join the room
     */
    public Token getToken() {
        return token;
    }

    /**
     *
     * @return the id of the game that the client wants to join
     */
    public int getGameId() {
        return gameId;
    }
}
