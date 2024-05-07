package it.polimi.ingsw.am13.client.network.rmi;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.Token;
import it.polimi.ingsw.am13.network.rmi.GameControllerRMI;

public class NetworkHandlerRMI implements NetworkHandler {
    public void setController(GameControllerRMI controller) {

    }

    @Override
    public void getRooms() {

    }

    @Override
    public void createRoom(String chosenNickname, Token token, int players) {

    }

    @Override
    public void joinRoom(String chosenNickname, Token token, int gameId) {

    }

    @Override
    public void reconnect(String nickname, Token token) {

    }

    @Override
    public void leaveRoom(String nickname) {

    }

    @Override
    public void playStarter(Side side) {

    }

    @Override
    public void choosePersonalObjective(CardObjectiveIF card) {

    }

    @Override
    public void playCard(CardPlayableIF card, Coordinates coords, Side side) {

    }

    @Override
    public void pickCard(CardPlayableIF card) {

    }

    @Override
    public void ping() {

    }
}
