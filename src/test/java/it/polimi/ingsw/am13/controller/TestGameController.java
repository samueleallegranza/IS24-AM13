package it.polimi.ingsw.am13.controller;


import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayersNumberException;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import org.junit.jupiter.api.Test;

import java.util.*;

public class TestGameController {
    GameController gameController;
    List<CardPlayable> playableCards;
    //List<Player> players;
    static class GameListenerForTesting implements GameListener{
        private final PlayerLobby player;
        private Long ping;
        public GameListenerForTesting(PlayerLobby player) {
            this.player = player;
        }

        @Override
        public void updateStartGame(GameModelIF model) {
            /*for(Player player: players) {
                try {
                    player.initStarter(model.fetchStarter(player.getPlayerLobby()));
                } catch (VariableAlreadySetException e) {
                    throw new RuntimeException(e);
                } catch (InvalidPlayerException e) {
                    throw new RuntimeException(e);
                }
            }*/
        }
        @Override
        public void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter) {
        }
        @Override
        public void updateChosenPersonalObjective(PlayerLobby player) {
        }
        @Override
        public void updateNextTurn(PlayerLobby player) {
        }
        @Override
        public void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayable, Side side, Coordinates coord, int points, List<Coordinates> availableCoords) {
        }
        @Override
        public void updatePickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards) {
        }
        @Override
        public void updatePoints(Map<PlayerLobby, Integer> pointsMap) {
        }
        @Override
        public void updateWinner(PlayerLobby winner) {
        }
        @Override
        public void updateEndGame() {
        }
        @Override
        public void updatePlayerDisconnected(PlayerLobby player) {
        }
        @Override
        public void updatePlayerReconnected(PlayerLobby player) {
        }
        @Override
        public void updateGameModel(GameModelIF model, GameController controller) {
        }
        @Override
        public void updateFinalPhase() {
        }
        @Override
        public void updateInGame() {
        }
        @Override
        public PlayerLobby getPlayer() {
            return player;
        }
        @Override
        public void updateGameBegins(GameController controller) {
        }
        @Override
        public void updatePlayerJoinedRoom(PlayerLobby player) {
        }
        @Override
        public void updatePlayerLeftRoom(PlayerLobby player) {
        }
        @Override
        public Long getPing() {
            return null;
        }
        @Override
        public void updatePing() {
        }
    }
    @Test
    public void testConstruction(){
        ArrayList<PlayerLobby> players=new ArrayList<>(Arrays.asList(new PlayerLobby("Al", ColorToken.RED),new PlayerLobby("John",ColorToken.BLUE)));
        gameController=null;
        try{
            gameController=new GameController(0,new ListenerHandler((Arrays.asList(new GameListenerForTesting(players.get(0)),new GameListenerForTesting(players.get(1))))));
        } catch (InvalidPlayersNumberException e){
            throw new RuntimeException();
        }
    }

    @Test
    public void testPickCard(){
        testConstruction();
    }
}
