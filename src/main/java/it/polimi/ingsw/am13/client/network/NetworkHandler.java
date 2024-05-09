package it.polimi.ingsw.am13.client.network;

import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.Token;

import java.util.List;

public interface NetworkHandler {
    //TODO add exceptions
    //todo msgCommandReconnectGame
    List<RoomIF> getRooms();
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
