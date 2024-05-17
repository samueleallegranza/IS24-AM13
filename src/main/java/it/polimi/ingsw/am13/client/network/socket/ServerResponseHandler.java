package it.polimi.ingsw.am13.client.network.socket;

import it.polimi.ingsw.am13.client.gamestate.GameStateHandler;
import it.polimi.ingsw.am13.client.network.PingThread;
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
    private final Socket socket;
    private GameStateHandler gameStateHandler;
    private PingThread pingThread;
    private final NetworkHandlerSocket networkHandlerSocket;
    private final View view;
    public ServerResponseHandler(Socket socket, NetworkHandlerSocket networkHandlerSocket, View view){
        this.socket=socket;
        gameStateHandler=null;
        this.networkHandlerSocket=networkHandlerSocket;
        this.view = view;
    }

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
                    msgResponse=(MsgResponse) in.readObject();
                }
                 catch (ClassNotFoundException | IOException e) {
                    throw new RuntimeException(e);
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
                            if(gameStateHandler == null)
                                gameStateHandler = new GameStateHandler(msgResponseStartGame.getGameState());
                            pingThread = new PingThread(networkHandlerSocket);
                            view.showStartGame(gameStateHandler.getState());
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
                                gameStateHandler.updateFinalPhase();
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
                            pingThread.stopPing();
                            view.showEndGame();
                        }

                        case MsgResponsePing msgResponsePing -> {
                            // do nothing
                        }

                        case MsgResponseUpdateGameState msgResponseUpdateGameState ->{
                            gameStateHandler = new GameStateHandler(msgResponseUpdateGameState.getGameState());
                            pingThread = new PingThread(networkHandlerSocket);
                            view.showStartGameReconnected(msgResponseUpdateGameState.getGameState(), msgResponseUpdateGameState.getPlayer());
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
