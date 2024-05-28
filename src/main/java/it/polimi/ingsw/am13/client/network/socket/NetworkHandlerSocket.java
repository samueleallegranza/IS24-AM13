package it.polimi.ingsw.am13.client.network.socket;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.view.View;
import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.model.player.Token;
import it.polimi.ingsw.am13.network.socket.message.command.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkHandlerSocket implements NetworkHandler {
    //private final PrintWriter out;
    private final ObjectOutputStream out;
    private PlayerLobby latestPlayer;
    public NetworkHandlerSocket(Socket socket, View view) {
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new ServerResponseHandler(socket,this, view).start();
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

    /**
     * @return The current valid player this class is associated to. Null is no valid player is currently associated.
     */
    @Override
    public PlayerLobby getPlayer() {
        return latestPlayer;
    }

    @Override
    public synchronized void getRooms() {
        sendMessage(new MsgCommandGetRooms());
    }

    @Override
    public synchronized void createRoom(String chosenNickname, Token token, int players) {
        sendMessage(new MsgCommandCreateRoom(chosenNickname,token,players));
        latestPlayer = new PlayerLobby(chosenNickname,token);
    }

    @Override
    public synchronized void joinRoom(String chosenNickname, Token token, int gameId) {
        sendMessage(new MsgCommandJoinRoom(chosenNickname,token,gameId));
        latestPlayer = new PlayerLobby(chosenNickname,token);
    }

    @Override
    public synchronized void reconnect(String nickname, Token token) {
        sendMessage(new MsgCommandReconnectGame(nickname, token));
        latestPlayer = new PlayerLobby(nickname, token);
    }

    @Override
    public synchronized void leaveRoom() {
        sendMessage(new MsgCommandLeaveRoom(latestPlayer.getNickname()));
    }

    @Override
    public synchronized void playStarter(Side side) {
        sendMessage(new MsgCommandPlayStarter(side));
    }

    @Override
    public synchronized void choosePersonalObjective(CardObjectiveIF card) {
        sendMessage(new MsgCommandChoosePersonalObjective(card));
    }

    @Override
    public synchronized void playCard(CardPlayableIF card, Coordinates coords, Side side) {
        sendMessage(new MsgCommandPlayCard(card,coords,side));
    }

    @Override
    public synchronized void pickCard(CardPlayableIF card) {
        sendMessage(new MsgCommandPickCard(card));
    }

    @Override
    public synchronized void ping() {
        sendMessage(new MsgCommandPing());
    }
}
