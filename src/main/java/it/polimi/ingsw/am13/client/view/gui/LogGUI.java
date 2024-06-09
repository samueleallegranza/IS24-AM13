package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.view.tui.ViewTUIConstants;
import it.polimi.ingsw.am13.model.card.CardSidePlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.time.LocalTime;
import java.util.*;

public class LogGUI {

    private final Queue<String> logMessages;
    private final GameState gameState;

    public LogGUI(GameState gameState) {
        this.gameState = gameState;
        this.logMessages = new LinkedList<>();
    }

    public boolean hasOtherLogs() {
        return !logMessages.isEmpty();
    }

    public String popNextLog() {
        return logMessages.poll();
    }

    public void logPlayedStarter(PlayerLobby player) {
        this.addToLog(formatLogPlayedStarter(player));
    }

    public void logChosenPersonalObjective(PlayerLobby player) {
        this.addToLog(formatLogChosenPersonalObjective(player));
    }

    public void logPlayedCard(PlayerLobby player, Coordinates coord, boolean pointsIncr) {
        this.addToLog(formatLogPlayedCard(player, coord, pointsIncr));
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

    public void logUpdatePoints(Map<PlayerLobby, Integer> pointsDelta) {
        addToLog(formatLogUpdatePoints(pointsDelta));
    }

    public void logWinner() {
        addToLog(formatLogWinner());
    }

    public void logDisconnect(PlayerLobby player) {
        this.addToLog(formatLogDisconnect(player));
    }

    public void logReconnect(PlayerLobby player) {
        this.addToLog(formatLogReconnect(player));
    }

    private void addToLog(String log) {
        logMessages.add(log);
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

    private String formatLogPlayedCard(PlayerLobby player, Coordinates coord, boolean pointsIncr) {
        CardSidePlayableIF card = gameState.getPlayerState(player).getField().getCardSideAtCoord(coord);

        String resourceName = card.getColor().correspondingResource().name();
        resourceName = resourceName.substring(0,1).toUpperCase() +
                resourceName.substring(1).toLowerCase();

        return String.format(
                "[%s][%s] Played card %s at coordinates (%d, %d) --> %s %d points",
                this.currentTimeString(),
                player.getNickname(),
                resourceName,
                coord.getPosX(),
                coord.getPosY(),
                pointsIncr ? "reached" : "remained with",
                gameState.getPlayerState(player).getPoints()
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

    private String formatLogUpdatePoints(Map<PlayerLobby, Integer> pointsBefore) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s][%s] The turn-based phase has ended, the extra points from the objective cards are:",
                currentTimeString(),
                "Server"));
        for(PlayerLobby p : pointsBefore.keySet()) {
            int delta = gameState.getPlayerState(p).getPoints() - pointsBefore.get(p);
            sb.append(String.format("\n\t%s: %d points --> %s %d points",
                    p.getNickname(),
                    delta,
                    delta==0 ? "remained with" : "reached",
                    gameState.getPlayerState(p).getPoints()));
        }
        return sb.toString();
    }

    private String formatLogWinner() {
        return String.format("[%s][%s] The game ended, %s has won",
                currentTimeString(),
                "Server",
                gameState.getWinner().getNickname());
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
