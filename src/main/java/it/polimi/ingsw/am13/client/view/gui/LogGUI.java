package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.view.tui.ViewTUIConstants;
import it.polimi.ingsw.am13.model.card.CardSidePlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.time.LocalTime;
import java.util.*;

/**
 * Class that stores a log of the game for the GUI
 */
public class LogGUI {

    /**
     * Messages currently saved.
     * It's a queue storing only the still not handled log messages
     */
    private final Queue<String> logMessages;

    /**
     * State of the game
     */
    private final GameState gameState;

    /**
     * Creates a new instance of the handler of the log for GUI
     * @param gameState State of the game
     */
    public LogGUI(GameState gameState) {
        this.gameState = gameState;
        this.logMessages = new LinkedList<>();
    }

    /**
     * Checks if there are other logs in the queue of messages (i.e. the queue is not empty)
     * @return True is there are other logs to handle, false otherwise
     */
    public boolean hasOtherLogs() {
        return !logMessages.isEmpty();
    }

    /**
     * Sets the most old log as handled, deleting it (i.e. pops it from the queue)
     * @return Deletes the currently handled log message
     */
    public String popNextLog() {
        return logMessages.poll();
    }


    /**
     * Adds to the log that the game has started
     */
    public void logStartGame() {
        addToLog("The game has started\n--> TO 30L... AND BEYOND!! <--");
    }

    /**
     * Adds to the log that the specified player played the starter card
     * @param player Player who played the starter card
     */
    public void logPlayedStarter(PlayerLobby player) {
        addToLog(player, "Played starter card");
    }

    /**
     * Adds to the log that the specified player chosen their personal objective card
     * @param player Player who chose the personal objective card
     */
    public void logChosenPersonalObjective(PlayerLobby player) {
        addToLog(player, "Chose personal objective");
    }

    /**
     * Adds to the log that the specified player played the a card on the specified coordinates
     * @param player Player who played the card
     * @param coord Coordinates on which the card has been placed
     */
    public void logPlayedCard(PlayerLobby player, Coordinates coord, boolean pointsIncr) {
        CardSidePlayableIF card = gameState.getPlayerState(player).getField().getCardSideAtCoord(coord);

        String resourceName = card.getColor().correspondingResource().name();
        resourceName = resourceName.substring(0,1).toUpperCase() +
                resourceName.substring(1).toLowerCase();

        this.addToLog(player,
                String.format("Played card %s at coordinates (%d, %d) --> %s %d points",
                        resourceName,
                        coord.getPosX(),
                        coord.getPosY(),
                        pointsIncr ? "reached" : "remained with",
                        gameState.getPlayerState(player).getPoints())
        );
    }

    /**
     * Adds to the log that the specified player picked a card
     * @param player Player who picked the card
     */
    public void logPickedCard(PlayerLobby player) {
        this.addToLog(player, "Picked card from the table");
    }

    /**
     * Adds to the log that the turn changed, and specifies the new current player
     */
    public void logNextTurn() {
        this.addToLog("Now its the turn of player " + gameState.getCurrentPlayer().getNickname());
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
                "Hurry up, %s has reached %d points! The next game round will be the last one",
                player.getNickname(),
                maxpoints
        ));
    }

    /**
     * Adds to the log that the points were updated
     * @param pointsDelta Difference between the points now updated and before the update
     */
    public void logUpdatePoints(Map<PlayerLobby, Integer> pointsDelta) {
        StringBuilder sb = new StringBuilder();
        sb.append("The turn-based phase has ended, the extra points from the objective cards are:");
        for(PlayerLobby p : pointsDelta.keySet()) {
            int delta = gameState.getPlayerState(p).getPoints() - pointsDelta.get(p);
            sb.append(String.format("\n\t%s: %d points --> %s %d points",
                    p.getNickname(),
                    delta,
                    delta==0 ? "remained with" : "reached",
                    gameState.getPlayerState(p).getPoints()));
        }
        addToLog(sb.toString());
    }

    /**
     * Adds to the log that the winner has been calculated
     */
    public void logWinner() {
        StringBuilder winnerStr;
        if(gameState.getWinner().size()==1)
            winnerStr = new StringBuilder(String.format("The game ended, %s has won",
                    gameState.getWinner().getFirst().getNickname()));
        else {
            winnerStr = new StringBuilder("The game ended, ");
            for (int i = 0; i <gameState.getWinner().size() ; i++) {
                winnerStr.append(gameState.getWinner().get(i).getNickname());
                if(i!=gameState.getWinner().size()-1)
                    winnerStr.append(", ");
            }
            winnerStr.append(" have won");
        }
        addToLog(winnerStr.toString());
    }

    /**
     * Adds to the log that the specified player disconnected
     * @param player Player who disconnected
     */
    public void logDisconnect(PlayerLobby player) {
        this.addToLog(player, "Left the game");
    }

    /**
     * Adds to the log that the specified player reconnected
     * @param player Player who reconnected
     */
    public void logReconnect(PlayerLobby player) {
        this.addToLog(player, "Re-joined the game");
    }

    /**
     * Adds to the log that a chat message and been received by the client
     * @param sender Player who sent the message
     * @param toAll True if the message is to all the players, false if it's for a specific player
     */
    public void logMessageReceived(PlayerLobby sender, boolean toAll) {
        addToLog(String.format("[%s][%s] Sent message to %s",
                this.currentTimeString(),
                sender.getNickname(),
                toAll ? "all" : "you"));
    }

    /**
     * Adds to the log a generic string, with the specified subject player
     * @param player Player subject of the log (who triggered the action logged)
     * @param log String (without the time string and the subject) for the log
     */
    private void addToLog(PlayerLobby player, String log) {
        logMessages.add(String.format("[%s][%s] %s",
                this.currentTimeString(),
                player.getNickname(), log));
    }
    /**
     * Adds to the log a generic string, with the server as subject of the log
     * @param log String (without the time string and the subject) for the log
     */
    private void addToLog(String log) {
        logMessages.add(String.format("[%s][Server] %s",
                this.currentTimeString(), log));
    }

    /**
     * @return Formatted string for the current time the log is stored
     */
    private String currentTimeString() {
        LocalTime currentTime = LocalTime.now();
        return currentTime.format(ViewTUIConstants.DATETIME_FORMAT);
    }
}
