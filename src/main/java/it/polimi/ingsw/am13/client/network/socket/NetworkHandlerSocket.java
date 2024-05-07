package it.polimi.ingsw.am13.client.network.socket;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.Token;
import it.polimi.ingsw.am13.network.socket.message.command.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkHandlerSocket implements NetworkHandler {
    //private final PrintWriter out;
    private final ObjectOutputStream out;

    public NetworkHandlerSocket(Socket socket) {
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new ServerResponseHandler(socket);
    }

    private void flushReset() throws IOException{
        out.flush();
        out.reset();
    }
    private void sendMessage(MsgCommand messageCommand){
        try {
            out.writeObject(messageCommand);
            flushReset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void getRooms() {
        sendMessage(new MsgCommandGetRooms());
    }

    @Override
    public void createRoom(String chosenNickname, Token token, int players) {
        sendMessage(new MsgCommandCreateRoom(chosenNickname,token,players));
    }

    @Override
    public void joinRoom(String chosenNickname, Token token, int gameId) {
        sendMessage(new MsgCommandJoinRoom(chosenNickname,token,gameId));
    }

    @Override
    public void reconnect(String nickname, Token token) {

    }

    @Override
    public void leaveRoom(String nickname) {
        sendMessage(new MsgCommandLeaveRoom(nickname));
    }

    @Override
    public void playStarter(Side side) {
        sendMessage(new MsgCommandPlayStarter(side));
    }

    @Override
    public void choosePersonalObjective(CardObjectiveIF card) {
        sendMessage(new MsgCommandChoosePersonalObjective(card));
    }

    @Override
    public void playCard(CardPlayableIF card, Coordinates coords, Side side) {
        sendMessage(new MsgCommandPlayCard(card,coords,side));
    }

    @Override
    public void pickCard(CardPlayableIF card) {
        sendMessage(new MsgCommandPickCard(card));
    }

    @Override
    public void ping() {
        sendMessage(new MsgCommandPing());
    }
}
