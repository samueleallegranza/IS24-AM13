package it.polimi.ingsw.am13.client.network.socket;

import it.polimi.ingsw.am13.ParametersClient;
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
import java.util.List;

/**
 * This is the socket implementation of {@link NetworkHandler}.
 * For each method, it creates the corresponding {@link MsgCommand} and then sends it to the server.
 */
public class NetworkHandlerSocket implements NetworkHandler {
    /**
     * The output stream in which the messages are written
      */
    private final ObjectOutputStream out;

    /**
     * It is the player corresponding to this instance of the class (the latest one, in the sense that is updated if
     * a player leaves a room and then creates, joins or reconnects to another)
     */
    private PlayerLobby latestPlayer;

    /**
     * Thread sending periodically pings to server
     */
    private Thread pingThread;

    /**
     * Initialize the output stream and start the server response handler
     * @param socket the socket which is used to communicate with the server
     * @param view the user interface
     */
    public NetworkHandlerSocket(Socket socket, View view) {
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new ServerResponseHandler(socket,this, view).start();
    }

    /**
     * Flush and reset the network output stream
     * @throws IOException if an IO exception has occured
     */
    private void flushReset() throws IOException{
        out.flush();
        out.reset();
    }

    /**
     * It sends the message to the server, by writing it to out, and then flushing and resetting out.
     * @param messageCommand the message that has 0to be sent to the server
     */
    private void sendMessage(MsgCommand messageCommand){
        try {
            out.writeObject(messageCommand);
            flushReset();
        } catch (IOException e) {
            // The server could have crashed...
        }
    }

    /**
     * @return The current valid player this class is associated to. Null if no valid player is currently associated.
     */
    @Override
    public PlayerLobby getPlayer() {
        return latestPlayer;
    }

    /**
     * Get the existing rooms
     */
    @Override
    public synchronized void getRooms() {
        sendMessage(new MsgCommandGetRooms());
    }

    /**
     * Create a room with the passed parameters
     * @param chosenNickname of the player who is creating the room
     * @param token of the player who is creating the room
     * @param players of the room that is being created
     */
    @Override
    public synchronized void createRoom(String chosenNickname, Token token, int players) {
        sendMessage(new MsgCommandCreateRoom(chosenNickname,token,players));
        latestPlayer = new PlayerLobby(chosenNickname,token);
    }

    /**
     * Make the player join the specified room
     * @param chosenNickname the nickname the client wants to join the room with
     * @param token chosen by the player who wants to join the room
     * @param gameId of the game that the client wants to join
     */
    @Override
    public synchronized void joinRoom(String chosenNickname, Token token, int gameId) {
        sendMessage(new MsgCommandJoinRoom(chosenNickname,token,gameId));
        latestPlayer = new PlayerLobby(chosenNickname,token);
    }

    /**
     * Reconnect the passed player to an existing game he was previously part of
     * @param nickname of the player who wants to reconnect
     * @param token of the player who wants to reconnect
     */
    @Override
    public synchronized void reconnect(String nickname, Token token) {
        sendMessage(new MsgCommandReconnectGame(nickname, token));
        latestPlayer = new PlayerLobby(nickname, token);
    }

    /**
     * Make the player leave the room he is currently part of
     */
    @Override
    public synchronized void leaveRoom() {
        sendMessage(new MsgCommandLeaveRoom());
    }

    /**
     * Play the starter card
     * @param side on which the starter card should be played
     */
    @Override
    public synchronized void playStarter(Side side) {
        sendMessage(new MsgCommandPlayStarter(side));
    }

    /**
     * Choose the personal objective card
     * @param card the chosen personal objective
     */
    @Override
    public synchronized void choosePersonalObjective(CardObjectiveIF card) {
        sendMessage(new MsgCommandChoosePersonalObjective(card));
    }

    /**
     * Play a card according to the parameters
     * @param card the player wants to play
     * @param coords where the card should be played
     * @param side on which the card should be played
     */
    @Override
    public synchronized void playCard(CardPlayableIF card, Coordinates coords, Side side) {
        sendMessage(new MsgCommandPlayCard(card,coords,side));
    }

    /**
     * Pick the passed card
     * @param card the player wants to pick
     */
    @Override
    public synchronized void pickCard(CardPlayableIF card) {
        sendMessage(new MsgCommandPickCard(card));
    }

    /**
     * Ping the server
     */
    private synchronized void ping() {
        sendMessage(new MsgCommandPing());
    }

    /**
     * Starts the thread sending pings to server
     */
    @Override
    public void startPing() {
        pingThread = new Thread(() -> {
            while(!Thread.interrupted()) {
                ping();
                try {
                    Thread.sleep(ParametersClient.sleepTime);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        pingThread.start();
    }

    /**
     * Stops the thread sending pings to server
     */
    @Override
    public void stopPing() {
        pingThread.interrupt();
    }

    /**
     * Send a chat message to the receivers (either a single player, or all the players)
     *
     * @param receivers of the chat message
     * @param text      of the chat message
     */
    @Override
    public void sendChatMessage(List<PlayerLobby> receivers, String text) {
        sendMessage(new MsgCommandChat(receivers,text));
    }
}
