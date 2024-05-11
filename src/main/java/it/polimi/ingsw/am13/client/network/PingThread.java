package it.polimi.ingsw.am13.client.network;

import it.polimi.ingsw.am13.controller.GameListener;
import it.polimi.ingsw.am13.controller.Lobby;
import it.polimi.ingsw.am13.controller.LobbyException;
import it.polimi.ingsw.am13.model.exceptions.ConnectionException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayerException;

import java.util.List;

public class PingThread extends Thread{
    private static final Long sleepTime=500L;
    private final NetworkHandler networkHandler;

    public PingThread(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
        new Thread(this).start();
    }

    public void run(){
        while(!Thread.interrupted()) {
            networkHandler.ping();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
