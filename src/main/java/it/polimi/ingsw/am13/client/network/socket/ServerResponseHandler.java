package it.polimi.ingsw.am13.client.network.socket;

import it.polimi.ingsw.am13.client.gamestate.GameStateHandler;
import it.polimi.ingsw.am13.client.network.PingThread;
import it.polimi.ingsw.am13.network.socket.message.response.*;

import java.io.*;
import java.net.Socket;

public class ServerResponseHandler extends Thread{
    private final Socket socket;
    private GameStateHandler gameStateHandler;
    private final PingThread pingThread;
    public ServerResponseHandler(Socket socket, PingThread pingThread){
        this.socket=socket;
        gameStateHandler=null;
        this.pingThread=pingThread;
    }

    public void run(){

            ObjectInputStream in;
            try {
                in = new ObjectInputStream(this.socket.getInputStream());
            } catch (IOException e){
                throw new RuntimeException();
            }
            while(true){
                MsgResponse msgResponse;
                try {
                    msgResponse=(MsgResponse) in.readObject();
                }
                 catch (ClassNotFoundException | IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    //TODO: le notifiche alla view (chiamate all'interfaccia view per modificarla) sono da fare qui, non le fa gameStateHandler.
                    // Oppure sì, si può cambiare la cosa

                    //Todo notifica la view di getRooms, di eventuali errori legati a gameState (rami else di tutti gli if sottostanti), idem per MsgResponseError
                    switch (msgResponse) {
                        case MsgResponseGetRooms msgResponseGetRooms ->{

                        }

                        case MsgResponsePlayerJoinedRooom msgResponsePlayerJoinedRooom -> {
                            //if(gameStateHandler!=null)
                        }
                        case MsgResponsePlayerLeftRoom msgResponsePlayerLeftRoom ->{
                            //if(gameStateHandler!=null)
                        }
                        case MsgResponsePlayerDisconnected msgResponsePlayerDisconnected -> {
                            if(gameStateHandler!=null)
                                gameStateHandler.updatePlayerDisconnected(msgResponsePlayerDisconnected.getPlayer());
                        }
                        case MsgResponsePlayerReconnected msgResponsePlayerReconnected -> {
                            if(gameStateHandler!=null)
                                gameStateHandler.updatePlayerReconnected(msgResponsePlayerReconnected.getPlayer());
                        }

                        case MsgResponseStartGame msgResponseStartGame -> {
                            if(gameStateHandler==null)
                                gameStateHandler=new GameStateHandler(msgResponseStartGame.getGameState());
                        }
                        case MsgResponsePlayedStarter msgResponsePlayedStarter -> {
//                            if(gameStateHandler!=null)
//                                gameStateHandler.updatePlayedStarter(msgResponsePlayedStarter.getPlayer(),msgResponsePlayedStarter.getStarter(), availableCoords);
                            //TODO ricava available coords da msg
                        }
                        case MsgResponseChosenPersonalObjective msgResponseChosenPersonalObjective ->{
//                            if(gameStateHandler!=null)
//                                gameStateHandler.updateChosenPersonalObjective(msgResponseChosenPersonalObjective.getPlayer(), chosenObj);
                            //TODO ricava chosen obj da msg
                        }
                        case MsgResponseInGame msgResponseInGame ->{
                            if(gameStateHandler!=null)
                                gameStateHandler.updateInGame();
                        }

                        case MsgResponsePlayedCard msgResponsePlayedCard ->{
                            if(gameStateHandler!=null)
                                gameStateHandler.updatePlayedCard(msgResponsePlayedCard.getPlayer(),msgResponsePlayedCard.getCardPlayer(),msgResponsePlayedCard.getCoordinates(),msgResponsePlayedCard.getPoints(),msgResponsePlayedCard.getAvailableCoordinates());
                        }
                        case MsgResponsePickedCard msgResponsePickedCard ->{
//                            if(gameStateHandler!=null)
//                                gameStateHandler.updatePickedCard(msgResponsePickedCard.getPlayer(),new ArrayList<>(msgResponsePickedCard.getUpdatedVisibleCards()), pickedCard);
                            //TODO ricava picked card da msg
                        }
                        case MsgResponseNextTurn msgResponseNextTurn ->{
                            if(gameStateHandler!=null)
                                gameStateHandler.updateNextTurn(msgResponseNextTurn.getPlayer());
                        }
                        case MsgResponseFinalPhase msgResponseFinalPhase ->{
                            if(gameStateHandler!=null)
                                gameStateHandler.updateFinalPhase();
                        }

                        case MsgResponsePointsCalculated msgResponsePointsCalculated ->{
                            if(gameStateHandler!=null)
                                gameStateHandler.updatePoints(msgResponsePointsCalculated.getPoints());
                        }
                        case MsgResponseWinner msgResponseWinner ->{
                            if(gameStateHandler!=null)
                                gameStateHandler.updateWinner(msgResponseWinner.getPlayer());
                        }

                        case MsgResponseEndGame msgResponseEndGame ->{
                           // if(gameStateHandler!=null)
                            socket.close();
                            pingThread.interrupt();
                            //TODO c'è da notificare la view
                        }

                        case MsgResponseUpdateGameState msgResponseUpdateGameState ->{

                        }
                        case MsgResponseError msgResponseError -> System.out.println("Error");
                        //TODO: bisogna far arrivare l'eccezione alla view

                        default -> throw new IllegalStateException("Unexpected value: " + msgResponse);
                    }
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
    }

}
