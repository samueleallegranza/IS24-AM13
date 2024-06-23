package it.polimi.ingsw.am13.client.network.socket;

import it.polimi.ingsw.am13.client.chat.Chat;
import it.polimi.ingsw.am13.client.chat.ChatMessage;
import it.polimi.ingsw.am13.client.gamestate.GameStateHandler;
import it.polimi.ingsw.am13.client.view.View;
import it.polimi.ingsw.am13.network.socket.message.response.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class keeps waiting for responses coming from the server.
 * Depending on the received message, it calls the corresponding GameStateHandler and View methods.
 */
public class ServerResponseHandler extends Thread{
    /**
     * The socket which is used to communicate with the server
     */
    private final Socket socket;
    /**
     * Handler of the representation of the game state
     */
    private GameStateHandler gameStateHandler;
    /**
     * The socket network handler that sends the messages from the client to the server
     */
    private final NetworkHandlerSocket networkHandlerSocket;
    /**
     * The user interface
     */
    private final View view;
    /**
     * Chat storing all the messages involving this player
     */
    private Chat chat;

    /**
     * Constructor of this class, that sets the socket, network handler and view
     * @param socket to communicate with the server
     * @param networkHandlerSocket that sends the messages from the client to the server
     * @param view of this client, where the updates received from the server are shown
     */
    public ServerResponseHandler(Socket socket, NetworkHandlerSocket networkHandlerSocket, View view){
        this.socket=socket;
        gameStateHandler=null;
        this.networkHandlerSocket=networkHandlerSocket;
        this.view = view;
        chat=null;
    }

    /**
     * Thread run method that waits for messages coming from the server, and updates the view and the game state
     * according to the received message
     */
    public void run(){

            ObjectInputStream in;
            try {
                in = new ObjectInputStream(this.socket.getInputStream());
            } catch (IOException e){
                throw new RuntimeException();
            }

            while(true) {
                MsgResponse msgResponse;
                try {
                    msgResponse = (MsgResponse) in.readObject();
                }
                 catch (ClassNotFoundException | IOException e) {
                    view.showException(e);
                    view.forceCloseApp();
                    return;
                }
                try {
                    switch (msgResponse) {
                        case MsgResponseGetRooms msgResponseGetRooms ->
                            view.showRooms(msgResponseGetRooms.getRooms());

                        case MsgResponsePlayerJoinedRooom msgResponsePlayerJoinedRooom ->
                            view.showPlayerJoinedRoom(msgResponsePlayerJoinedRooom.getPlayer());

                        case MsgResponsePlayerLeftRoom msgResponsePlayerLeftRoom ->
                            view.showPlayerLeftRoom(msgResponsePlayerLeftRoom.getPlayer());

                        case MsgResponsePlayerDisconnected msgResponsePlayerDisconnected -> {
                            if(gameStateHandler!=null) {
                                gameStateHandler.updatePlayerDisconnected(msgResponsePlayerDisconnected.getPlayer());
                                view.showPlayerDisconnected(msgResponsePlayerDisconnected.getPlayer());
                            }
                        }

                        case MsgResponsePlayerReconnected msgResponsePlayerReconnected -> {
                            if(gameStateHandler!=null) {
                                gameStateHandler.updatePlayerReconnected(msgResponsePlayerReconnected.getPlayer());
                                view.showPlayerReconnected(msgResponsePlayerReconnected.getPlayer());
                            }
                        }

                        case MsgResponseStartGame msgResponseStartGame -> {
                            if(gameStateHandler == null) {
                                gameStateHandler = new GameStateHandler(msgResponseStartGame.getGameState());
                                chat=new Chat(msgResponseStartGame.getGameState().getPlayers(),networkHandlerSocket.getPlayer());
                            }
                            networkHandlerSocket.startPing();
                            view.showStartGame(gameStateHandler.getState(),chat);
                        }

                        case MsgResponsePlayedStarter msgResponsePlayedStarter -> {
                            if(gameStateHandler!=null)
                                gameStateHandler.updatePlayedStarter(msgResponsePlayedStarter.getPlayer(),msgResponsePlayedStarter.getStarter(),
                                        msgResponsePlayedStarter.getAvailableCoords());
                            view.showPlayedStarter(msgResponsePlayedStarter.getPlayer());
                        }

                        case MsgResponseChosenPersonalObjective msgResponseChosenPersonalObjective ->{
                            if(gameStateHandler!=null)
                                gameStateHandler.updateChosenPersonalObjective(msgResponseChosenPersonalObjective.getPlayer(),
                                        msgResponseChosenPersonalObjective.getChosenObjective());
                            view.showChosenPersonalObjective(msgResponseChosenPersonalObjective.getPlayer());
                        }

                        case MsgResponseInGame msgResponseInGame ->{
                            if(gameStateHandler!=null)
                                gameStateHandler.updateInGame();
                            view.showInGame();
                        }

                        case MsgResponsePlayedCard msgResponsePlayedCard ->{
                            if(gameStateHandler!=null)
                                gameStateHandler.updatePlayedCard(msgResponsePlayedCard.getPlayer(),msgResponsePlayedCard.getCardPlayer(),msgResponsePlayedCard.getCoordinates(),msgResponsePlayedCard.getPoints(),msgResponsePlayedCard.getAvailableCoordinates());
                            view.showPlayedCard(msgResponsePlayedCard.getPlayer(), msgResponsePlayedCard.getCoordinates());
                        }

                        case MsgResponsePickedCard msgResponsePickedCard -> {
                            if(gameStateHandler!=null)
                                gameStateHandler.updatePickedCard(msgResponsePickedCard.getPlayer(),new ArrayList<>(msgResponsePickedCard.getUpdatedVisibleCards()),
                                        msgResponsePickedCard.getPickedCard());
                            view.showPickedCard(msgResponsePickedCard.getPlayer());
                        }

                        case MsgResponseNextTurn msgResponseNextTurn ->{
                            if(gameStateHandler!=null)
                                gameStateHandler.updateNextTurn(msgResponseNextTurn.getPlayer());
                            view.showNextTurn();
                        }

                        case MsgResponseFinalPhase msgResponseFinalPhase ->{
                            if(gameStateHandler!=null)
                                gameStateHandler.updateFinalPhase(msgResponseFinalPhase.getTurnsToEnd());
                            view.showFinalPhase();
                        }

                        case MsgResponsePointsCalculated msgResponsePointsCalculated ->{
                            if(gameStateHandler!=null)
                                gameStateHandler.updatePoints(msgResponsePointsCalculated.getPoints());
                            view.showUpdatePoints();
                        }

                        case MsgResponseWinner msgResponseWinner ->{
                            if(gameStateHandler!=null)
                                gameStateHandler.updateWinner(msgResponseWinner.getPlayer());
                            view.showWinner();
                        }

                        case MsgResponseEndGame msgResponseEndGame ->{
                           // if(gameStateHandler!=null)
                            socket.close();
                            networkHandlerSocket.stopPing();
                            view.showEndGame();
                        }

                        case MsgResponsePing msgResponsePing -> {
                            // do nothing
                        }

                        case MsgResponseUpdateGameState msgResponseUpdateGameState -> {
                            gameStateHandler = new GameStateHandler(msgResponseUpdateGameState.getGameState());
                            chat = new Chat(msgResponseUpdateGameState.getGameState().getPlayers(),networkHandlerSocket.getPlayer());
                            networkHandlerSocket.startPing();
                            view.showStartGameReconnected(msgResponseUpdateGameState.getGameState(), msgResponseUpdateGameState.getPlayer(),chat);
                        }

                        case MsgResponseChat msgResponseChat -> {

                            chat.addMessage(msgResponseChat.getReceivers(),new ChatMessage(msgResponseChat.getSender(), msgResponseChat.getText()));
                            view.showChatMessage(msgResponseChat.getSender(),msgResponseChat.getReceivers());
                        }

                        case MsgResponseError msgResponseError ->
                            view.showException(msgResponseError.getException());

                        default -> throw new IllegalStateException("Unexpected value: " + msgResponse);
                    }
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
    }

}
