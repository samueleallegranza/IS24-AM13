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


    public void logStartGame() {
        addToLog("The game has started\n--> TO 30L... AND BEYOND!! <--");
    }

    public void logPlayedStarter(PlayerLobby player) {
        addToLog(player, "Played starter card");
    }

    public void logChosenPersonalObjective(PlayerLobby player) {
        addToLog(player, "Chose personal objective");
    }

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

    public void logPickedCard(PlayerLobby player) {
        this.addToLog(player, "Picked card from the table");
    }

    public void logNextTurn() {
        this.addToLog("Now its the turn of player " + gameState.getCurrentPlayer().getNickname());
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

        this.addToLog(String.format(
                "Hurry up, %s has reached %d points! The next game round will be the last one",
                player.getNickname(),
                maxpoints
        ));
    }

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

    public void logWinner() {
        String winnerStr;
        if(gameState.getWinner().size()==1)
            winnerStr=String.format("The game ended, %s has won",
                    gameState.getWinner().getFirst().getNickname());
        else {
            winnerStr = "The game ended, ";
            for (int i = 0; i <gameState.getWinner().size() ; i++) {
                winnerStr += gameState.getWinner().get(i).getNickname();
                if(i!=gameState.getWinner().size()-1)
                    winnerStr += ", ";
            }
            winnerStr+=" have won";
        }
        addToLog(winnerStr);
    }

    public void logDisconnect(PlayerLobby player) {
        this.addToLog(player, "Left the game");
    }

    public void logReconnect(PlayerLobby player) {
        this.addToLog(player, "Re-joined the game");
    }

    public void logMessageReceived(PlayerLobby sender, boolean toAll) {
        addToLog(String.format("[%s][%s] Sent message to %s",
                this.currentTimeString(),
                sender.getNickname(),
                toAll ? "all" : "you"));
    }

    private void addToLog(PlayerLobby player, String log) {
        logMessages.add(String.format("[%s][%s] %s",
                this.currentTimeString(),
                player.getNickname(), log));
    }
    private void addToLog(String log) {
        logMessages.add(String.format("[%s][Server] %s",
                this.currentTimeString(), log));
    }

    private String currentTimeString() {
        LocalTime currentTime = LocalTime.now();
        return currentTime.format(ViewTUIConstants.DATETIME_FORMAT);
    }
}
