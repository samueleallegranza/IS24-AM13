package it.polimi.ingsw.am13.client.network;

import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.model.player.Token;

/**
 * This is the interface used by the client to send messages to the server
 */
public interface NetworkHandler {
    //TODO add exceptions

    /**
     * @return The current valid player this class is associated to. Null is no valid player is currently associated.
     */
    PlayerLobby getPlayer();
    void getRooms();
    void createRoom(String nickname, Token token, int nPlayers);
    void joinRoom(String nickname, Token token, int gameId);
    void reconnect(String nickname, Token token);
    void leaveRoom();
    void playStarter(Side side);
    void choosePersonalObjective(CardObjectiveIF card);
    void playCard(CardPlayableIF card, Coordinates coords, Side side);
    void pickCard(CardPlayableIF card);
    void ping();

}
