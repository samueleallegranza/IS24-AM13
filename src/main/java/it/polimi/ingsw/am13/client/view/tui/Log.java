package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.gamestate.GameState;

import java.util.LinkedList;
import java.util.Queue;

public class Log {
    private Queue<String> logMessages;
    private GameState gameState;
    private final int size;

    public Log(GameState gameState) {
        this.logMessages = new LinkedList<String>();
        this.gameState = gameState;
        this.size = TUIConstants.LOG_LINES;
    }

    private void addToLog() {

    }

    public Queue<String> getLogMessages() {
        return this.logMessages;
    }

    public void logPlayedStarter() {

    }

    public void logPlayedCard() {

    }

    public void logPickedCard() {

    }

    public void logFinalPhase() {

    }



    /*
        TODO:   For the first implementation we could just display the winner in the logs (?)
                This could be changed later with a better alternative...
     */
    public void logWinner() {}


    /*
        TODO:   The following methods are not essential for the logs.
     */
    public void logDisconnect() {

    }
    public void logReconnect() {

    }
}
