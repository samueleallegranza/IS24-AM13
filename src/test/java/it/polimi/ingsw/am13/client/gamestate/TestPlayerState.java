package it.polimi.ingsw.am13.client.gamestate;

import it.polimi.ingsw.am13.LisForTest;
import it.polimi.ingsw.am13.controller.GameListener;
import it.polimi.ingsw.am13.controller.LobbyException;
import it.polimi.ingsw.am13.controller.Room;
import it.polimi.ingsw.am13.model.GameModel;
import it.polimi.ingsw.am13.model.GameStatus;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestPlayerState {

    @Test
    public void testCreation() throws LobbyException, InvalidPlayersNumberException, GameStatusException, InvalidPlayerException, InvalidPlayCardException, InvalidChoiceException, VariableAlreadySetException, InvalidCoordinatesException {
        List<PlayerLobby>players = List.of(
                new PlayerLobby("1", ColorToken.RED),
                new PlayerLobby("2", ColorToken.BLUE)
        );
        List<GameListener> playerListeners = new ArrayList<>();
        players.forEach(p -> playerListeners.add(new LisForTest(p)));
        Room room = new Room(2, playerListeners.getFirst(), 2);
        room.joinRoom(playerListeners.get(1));
        GameModel model = new GameModel(room);

        PlayerState p;
        for(PlayerIF player : model.fetchPlayers()) {
            p = new PlayerState(player);

            assertEquals(0, p.getPoints());
            assertTrue(p.getHandPlayable().isEmpty());
            assertNull(p.getStarterCard());
            assertTrue(p.getPossibleHandObjectives().isEmpty());
            assertNull(p.getHandObjective());
            assertEquals(0, p.getField().getPlacedCoords().size());
        }

        model.startGame(null);
        assertEquals(GameStatus.INIT, model.fetchGameStatus());
        for(PlayerIF player : model.fetchPlayers()) {
            p = new PlayerState(player);

            assertEquals(0, p.getPoints());
            assertEquals(3, p.getHandPlayable().size());
            assertTrue(p.getHandPlayable().containsAll(player.getHandCards()));
            assertEquals(player.getStarter(), p.getStarterCard());
            assertEquals(2, p.getPossibleHandObjectives().size());
            assertTrue(p.getPossibleHandObjectives().containsAll(player.getPossiblePersonalObjectives()));
            assertEquals(player.getPersonalObjective(), p.getHandObjective());
            assertEquals(0, p.getField().getPlacedCoords().size());
        }

        for(PlayerLobby pp : players) {
            model.playStarter(pp, Side.SIDEBACK);
            model.choosePersonalObjective(pp, model.fetchPersonalObjectives(pp).getFirst());
        }
        assertEquals(GameStatus.IN_GAME, model.fetchGameStatus());
        for(PlayerIF player : model.fetchPlayers()) {
            p = new PlayerState(player);

            assertEquals(0, p.getPoints());
            assertEquals(3, p.getHandPlayable().size());
            assertTrue(p.getHandPlayable().containsAll(player.getHandCards()));
            assertEquals(player.getStarter(), p.getStarterCard());
            assertEquals(2, p.getPossibleHandObjectives().size());
            assertTrue(p.getPossibleHandObjectives().containsAll(player.getPossiblePersonalObjectives()));
            assertEquals(player.getPersonalObjective(), p.getHandObjective());
            assertEquals(1, p.getField().getPlacedCoords().size());
            assertEquals(4, p.getField().getAvailableCoords().size());
            assertTrue(p.getField().getAvailableCoords().containsAll(List.of(
                    new Coordinates(1,1), new Coordinates(1,-1),
                    new Coordinates(-1,1), new Coordinates(-1,-1)
            )));
        }
    }

}