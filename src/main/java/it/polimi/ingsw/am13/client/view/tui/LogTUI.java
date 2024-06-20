package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.model.card.CardSidePlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.time.LocalTime;
import java.util.*;

/**
 * Class that stores the logs for the TUI
 * To avoid using too much space in the terminal, it only stores the last LOG_MAXLINES log messages
 */
public class LogTUI {

    /**
     * Messages currently saved.
     * It's a deque with a fixed size (size stored in {@link ViewTUIConstants#LOG_MAXLINES}
     */
    private final Deque<String> logMessages;

    /**
     * State of the game
     */
    private final GameState gameState;

    /**
     * Creates a new instance of the handler of the log for TUI
     * @param gameState State of the game
     */
    public LogTUI(GameState gameState) {
        this.gameState = gameState;
        this.logMessages = new LinkedList<>();
    }

    /**
     * @return List of the last logs, sorted from the most recent to the most old
     */
    public List<String> getLogMessages() {
        return new ArrayList<>(this.logMessages);
    }

    /**
     * Adds to the log that the specified player played the starter card
     * @param player Player who played the starter card
     */
    public void logPlayedStarter(PlayerLobby player) {
        this.addToLog(String.format(
                "[%s][%s] Played starter card",
                this.currentTimeString(),
                player.getNickname()
        ));
    }

    /**
     * Adds to the log that the specified player chosen their personal objective card
     * @param player Player who chose the personal objective card
     */
    public void logChosenPersonalObjective(PlayerLobby player) {
        this.addToLog(String.format(
                "[%s][%s] Chosen personal objective",
                this.currentTimeString(),
                player.getNickname()
        ));
    }

    /**
     * Adds to the log that the specified player played the a card on the specified coordinates
     * @param player Player who played the card
     * @param coord Coordinates on which the card has been placed
     */
    public void logPlayedCard(PlayerLobby player, Coordinates coord) {
        CardSidePlayableIF card = gameState.getPlayerState(player).getField().getCardSideAtCoord(coord);
        String resourceName = card.getColor().correspondingResource().name();
        resourceName = resourceName.substring(0,1).toUpperCase() +
                resourceName.substring(1).toLowerCase();

        this.addToLog(String.format(
                "[%s][%s] Played card %s (%s) at coordinates (%d, %d)",
                this.currentTimeString(),
                player.getNickname(),
                resourceName,
                ViewTUIConstants.resourceToSymbol(card.getColor().correspondingResource()),
                coord.getPosX(),
                coord.getPosY()
        ));
    }

    /**
     * Adds to the log that the specified player picked a card
     * @param player Player who picked the card
     */
    public void logPickedCard(PlayerLobby player) {
        this.addToLog(String.format(
                "[%s][%s] Picked card from the table",
                this.currentTimeString(),
                player.getNickname()
        ));
    }

    /**
     * Adds to the log that the turn changed, and specifies the new current player
     */
    public void logNextTurn() {
        this.addToLog(String.format(
                "[%s][%s] Now its the turn of player %s",
                this.currentTimeString(),
                "Server",
                gameState.getCurrentPlayer().getNickname()
        ));
    }

    /**
     * Adds to the log that the final phase has been triggered
     */
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

        this.addToLog(String.format(
                "[%s][%s] Hurry up, %s has reached %d points! The next game round will be the last one",
                this.currentTimeString(),
                "Server",
                player.getNickname(),
                maxpoints
        ));
    }

    /**
     * Adds to the log that the specified player disconnected
     * @param player Player who disconnected
     */
    public void logDisconnect(PlayerLobby player) {
        this.addToLog(String.format(
                "[%s][%s] Left the game",
                this.currentTimeString(),
                player.getNickname()
        ));
    }

    /**
     * Adds to the log that the specified player reconnected
     * @param player Player who reconnected
     */
    public void logReconnect(PlayerLobby player) {
        this.addToLog(String.format(
                "[%s][%s] Re-joined the game",
                this.currentTimeString(),
                player.getNickname()
        ));
    }

    /**
     * Adds to the log a generic string
     * @param log String (already formatted) for the log
     */
    private void addToLog(String log) {
        // remove the oldest log from the queue if logs to make space for the new one
        if(logMessages.size() == ViewTUIConstants.LOG_MAXLINES) {
            logMessages.removeLast();
        }
        // add new log to the queue
        this.logMessages.addFirst(log);
    }

    /**
     * @return Formatted string for the current time the log is stored
     */
    private String currentTimeString() {
        LocalTime currentTime = LocalTime.now();
        return currentTime.format(ViewTUIConstants.DATETIME_FORMAT);
    }

}
