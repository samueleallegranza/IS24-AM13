package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.model.card.CardSidePlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.time.LocalTime;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Class that stores the logs for the TUI
 * To avoid using too much space in the terminal, it only stores the last LOG_MAXLINES log messages
 */
public class LogTUI {
    private final Deque<String> logMessages;
    private final GameState gameState;

    public LogTUI(GameState gameState) {
        this.gameState = gameState;
        this.logMessages = new LinkedList<>();
    }

    public Vector<String> getLogMessages() {
        return new Vector<>(this.logMessages);
    }

    public void logPlayedStarter(PlayerLobby player) {
        this.addToLog(formatLogPlayedStarter(player));
    }

    public void logChosenPersonalObjective(PlayerLobby player) {
        this.addToLog(formatLogChosenPersonalObjective(player));
    }

    public void logPlayedCard(PlayerLobby player, Coordinates coord) {
        this.addToLog(formatLogPlayedCard(player, coord));
    }

    public void logPickedCard(PlayerLobby player) {
        this.addToLog(formatLogPickedCard(player));
    }

    public void logNextTurn() {
        this.addToLog(formatLogNextTurn());
    }

    public void logFinalPhase() {
        this.addToLog(formatLogFinalPhase());
    }

    public void logDisconnect(PlayerLobby player) {
        this.addToLog(formatLogDisconnect(player));
    }

    public void logReconnect(PlayerLobby player) {
        this.addToLog(formatLogReconnect(player));
    }

    private void addToLog(String log) {
        // remove the oldest log from the queue if logs to make space for the new one
        if(logMessages.size() == ViewTUIConstants.LOG_MAXLINES) {
            logMessages.removeLast();
        }
        // add new log to the queue
        this.logMessages.addFirst(log);
    }

    private String currentTimeString() {
        LocalTime currentTime = LocalTime.now();
        return currentTime.format(ViewTUIConstants.DATETIME_FORMAT);
    }

    private String formatLogPlayedStarter(PlayerLobby player) {
        return String.format(
                "[%s][%s] Played starter card",
                this.currentTimeString(),
                player.getNickname()
        );
    }

    private String formatLogChosenPersonalObjective(PlayerLobby player) {
        return String.format(
                "[%s][%s] Chosen personal objective",
                this.currentTimeString(),
                player.getNickname()
        );
    }

    private String formatLogPlayedCard(PlayerLobby player, Coordinates coord) {
        CardSidePlayableIF card = gameState.getPlayerState(player).getField().getCardSideAtCoord(coord);

        String resourceName = card.getColor().correspondingResource().name();
        resourceName = resourceName.substring(0,1).toUpperCase() +
                resourceName.substring(1).toLowerCase();

        return String.format(
                "[%s][%s] Played card %s (%s) at coordinates (%d, %d)",
                this.currentTimeString(),
                player.getNickname(),
                resourceName,
                ViewTUIConstants.resourceToSymbol(card.getColor().correspondingResource()),
                coord.getPosX(),
                coord.getPosY()
        );
    }

    private String formatLogPickedCard(PlayerLobby player) {
        return String.format(
                "[%s][%s] Picked card from the table",
                this.currentTimeString(),
                player.getNickname()
        );
    }

    private String formatLogNextTurn() {
        return String.format(
                "[%s][%s] Now its the turn of player %s",
                this.currentTimeString(),
                "Server",
                gameState.getCurrentPlayer().getNickname()
        );
    }

    private String formatLogFinalPhase() {
        // find who triggered the final phase
        int maxpoints = -1;
        PlayerLobby player = gameState.getPlayers().getFirst();
        for (PlayerLobby pl: gameState.getPlayers()) {
            if(gameState.getPlayerState(pl).getPoints() > maxpoints) {
                maxpoints = gameState.getPlayerState(pl).getPoints();
                player = pl;
            }
        }

        return String.format(
                "[%s][%s] Hurry up, %s has reached %d points! The next game round will be the last one",
                this.currentTimeString(),
                "Server",
                player.getNickname(),
                maxpoints
        );
    }

    private String formatLogDisconnect(PlayerLobby player) {
        return String.format(
                "[%s][%s] Left the game",
                this.currentTimeString(),
                player.getNickname()
        );
    }

    private String formatLogReconnect(PlayerLobby player) {
        return String.format(
                "[%s][%s] Re-joined the game",
                this.currentTimeString(),
                player.getNickname()
        );
    }

}
