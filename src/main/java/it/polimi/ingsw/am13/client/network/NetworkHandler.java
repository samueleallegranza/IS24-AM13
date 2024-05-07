package it.polimi.ingsw.am13.client.network;

import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.Token;

public interface NetworkHandler {
    //TODO add exceptions
    //todo msgCommandReconnectGame
    public void getRooms();
    public void createRoom(String chosenNickname, Token token, int players);
    public void joinRoom(String chosenNickname, Token token, int gameId);
    public void reconnect(String nickname, Token token);
    public void leaveRoom(String nickname);
    public void playStarter(Side side);
    public void choosePersonalObjective(CardObjectiveIF card);
    public void playCard(CardPlayableIF card, Coordinates coords, Side side);
    public void pickCard(CardPlayableIF card);
    public void ping();

}
