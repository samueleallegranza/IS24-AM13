package it.polimi.ingsw.am13.client.view;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.view.tui.ViewTUIConstants;
import it.polimi.ingsw.am13.model.card.CardSidePlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.time.LocalTime;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Vector;


public class Log {
    private final Deque<String> logMessages;
    private final GameState gameState;

    public Log(GameState gameState) {
        this.logMessages = new LinkedList<>();
        this.gameState = gameState;
    }

    public Vector<String> getLogMessages() {
        return new Vector<>(this.logMessages);
    }

    public void logPlayedStarter(PlayerLobby player) {
        String log = String.format(
                "[%s][%s] Played starter card",
                this.currentTimeString(),
                player.getNickname()
        );
        this.addToLog(log);
    }

    public void logChosenPersonalObjective(PlayerLobby player) {
        String log = String.format(
                "[%s][%s] Chosen personal objective",
                this.currentTimeString(),
                player.getNickname()
        );
        this.addToLog(log);
    }

    public void logPlayedCard(PlayerLobby player, Coordinates coord) {
        CardSidePlayableIF card = gameState.getPlayerState(player).getField().getCardSideAtCoord(coord);

        String resourceName = card.getColor().correspondingResource().name();
        resourceName = resourceName.substring(0,1).toUpperCase() +
                       resourceName.substring(1).toLowerCase();

        String log = String.format(
                "[%s][%s] Played card %s (%s) at coordinates (%d, %d)",
                this.currentTimeString(),
                player.getNickname(),
                resourceName,
                ViewTUIConstants.resourceToSymbol(card.getColor().correspondingResource()),
                coord.getPosX(),
                coord.getPosY()
        );
        this.addToLog(log);
    }

    public void logPickedCard(PlayerLobby player) {
        String log = String.format(
                "[%s][%s] Picked card from the table",
                this.currentTimeString(),
                player.getNickname()
        );

        this.addToLog(log);
    }

    public void logNextTurn() {
        String log = String.format(
                "[%s][%s] Now its the turn of player %s",
                this.currentTimeString(),
                "Server",
                gameState.getCurrentPlayer().getNickname()
        );
        this.addToLog(log);
    }

    public void logFinalPhase() {
        // find who triggered the final phase
        int maxpoints = -1;
        PlayerLobby player = gameState.getPlayers().getFirst();
        for (PlayerLobby pl: gameState.getPlayers()) {
            if(gameState.getPlayerState(pl).getPoints() > maxpoints) {
                maxpoints = gameState.getPlayerState(pl).getPoints();
                player = pl;
            }
        }

        String log = String.format(
                "[%s][%s] Hurry up, %s has reached %d points! The next game round will be the last one",
                this.currentTimeString(),
                "Server",
                player.getNickname(),
                maxpoints
        );

        this.addToLog(log);
    }

    public void logDisconnect(PlayerLobby player) {
        String log = String.format(
                "[%s][%s] Left the game",
                this.currentTimeString(),
                player.getNickname()
        );
        this.addToLog(log);
    }

    public void logReconnect(PlayerLobby player) {
        String log = String.format(
                "[%s][%s] Re-joined the game",
                this.currentTimeString(),
                player.getNickname()
        );
        this.addToLog(log);
    }

    private String currentTimeString() {
        LocalTime currentTime = LocalTime.now();
        return currentTime.format(ViewTUIConstants.DATETIME_FORMAT);
    }

    private void addToLog(String log) {
        // remove the oldest log from the queue if logs to make space for the new one
        if(logMessages.size() == ViewTUIConstants.LOG_MAXLINES) {
            logMessages.removeLast();
        }
        // add new log to the queue
        this.logMessages.addFirst(log);
    }

}
