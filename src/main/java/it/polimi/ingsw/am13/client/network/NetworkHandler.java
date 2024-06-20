package it.polimi.ingsw.am13.client.network;

import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.model.player.Token;

import java.util.List;

/**
 * This is the interface used by the client to communicate with the server.
 * Aside from getPlayer, all the methods are processed on the server
 */
public interface NetworkHandler {

    /**
     * @return The current valid player associated to this NetworkHandler. Null is no valid player is currently associated.
     */
    PlayerLobby getPlayer();

    /**
     * Get the existing rooms
     */
    void getRooms();

    /**
     * Create a room with the passed parameters
     * @param nickname of the player who is creating the room
     * @param token of the player who is creating the room
     * @param nPlayers of the room that is being created
     */
    void createRoom(String nickname, Token token, int nPlayers);

    /**
     * Make the player join the specified room
     * @param nickname the nickname the client wants to join the room with
     * @param token chosen by the player who wants to join the room
     * @param gameId of the game that the client wants to join
     */
    void joinRoom(String nickname, Token token, int gameId);

    /**
     * Reconnect the passed player to an existing game he was previously part of
    * @param nickname of the player who wants to reconnect
     * @param token of the player who wants to reconnect
     */
    void reconnect(String nickname, Token token);

    /**
     * Make the player leave the room he is currently part of
     */
    void leaveRoom();

    /**
     * Play the starter card
     * @param side on which the starter card should be played
     */
    void playStarter(Side side);

    /**
     * Choose the personal objective card
     * @param card the chosen personal objective
     */
    void choosePersonalObjective(CardObjectiveIF card);

    /**
     * Play a card according to the parameters
     * @param card the player wants to play
     * @param coords where the card should be played
     * @param side on which the card should be played
     */
    void playCard(CardPlayableIF card, Coordinates coords, Side side);

    /**
     * Pick the passed card
     * @param card the player wants to pick
     */
    void pickCard(CardPlayableIF card);

    /**
     * Ping the server
     */
    void ping();

    /**
     * Send a chat message to the receivers (either a single player, or all the other players)
     * @param receivers of the chat message
     * @param text of the chat message
     */
    void sendChatMessage(List<PlayerLobby> receivers, String text);
}
