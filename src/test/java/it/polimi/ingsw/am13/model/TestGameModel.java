package it.polimi.ingsw.am13.model;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameModel {

    private Set<PlayerLobby> players;
    private GameModel game;

    @Test
    public void testCreation() throws InvalidPlayersNumberException, InvalidPlayerException {
        assertThrows(InvalidParameterException.class, ()->new GameModel(1,
                Arrays.asList("1", "2"),
                List.of(ColorToken.RED)));
        assertThrows(InvalidPlayersNumberException.class, ()->new GameModel(1,
                List.of("1"),
                List.of(ColorToken.RED)));
        assertThrows(InvalidPlayersNumberException.class, ()->new GameModel(1,
                Arrays.asList("1", "2", "3"),
                Arrays.asList(ColorToken.RED, ColorToken.BLUE, ColorToken.RED)));
        assertThrows(InvalidPlayersNumberException.class, ()->new GameModel(1,
                Arrays.asList("1", "2"),
                Arrays.asList(ColorToken.RED, ColorToken.BLACK)));

        players = Set.of(
                new PlayerLobby("1", ColorToken.RED),
                new PlayerLobby("2", ColorToken.BLUE)
        );
        game = new GameModel(0, players);

        //Test of 5 turn-async non-player-specific methods
        assertNull(game.fetchGameStatus());
        assertNull(game.fetchCurrentPlayer());
        assertEquals(players, game.fetchPlayers());

        List<? extends CardPlayableIF> pickables = game.fetchPickables();
        assertEquals(pickables.size(), 6);
        assertEquals(pickables.get(0).getVisibleSide(), Side.SIDEBACK);
        assertEquals(pickables.get(1).getVisibleSide(), Side.SIDEFRONT);
        assertEquals(pickables.get(2).getVisibleSide(), Side.SIDEFRONT);
        assertEquals(pickables.get(3).getVisibleSide(), Side.SIDEBACK);
        assertEquals(pickables.get(4).getVisibleSide(), Side.SIDEFRONT);
        assertEquals(pickables.get(5).getVisibleSide(), Side.SIDEFRONT);
        assertInstanceOf(CardResource.class, pickables.get(0));
        assertInstanceOf(CardResource.class, pickables.get(1));
        assertInstanceOf(CardResource.class, pickables.get(2));
        assertInstanceOf(CardGold.class, pickables.get(3));
        assertInstanceOf(CardGold.class, pickables.get(4));
        assertInstanceOf(CardGold.class, pickables.get(5));

        List<? extends CardObjectiveIF> commongObj = game.fetchCommonObjectives();
        assertEquals(commongObj.size(), 2);
        assertEquals(commongObj.get(0).getVisibleSide(), Side.SIDEFRONT);
        assertEquals(commongObj.get(1).getVisibleSide(), Side.SIDEFRONT);
        assertInstanceOf(CardObjective.class, commongObj.get(0));
        assertInstanceOf(CardObjective.class, commongObj.get(1));

        //Test of 4 turn-async player-specific methods
        assertThrows(InvalidPlayerException.class, () -> game.fetchStarter(new PlayerLobby("3", ColorToken.BLUE)));
        assertThrows(InvalidPlayerException.class, () -> game.fetchHandPlayable(new PlayerLobby("3", ColorToken.BLUE)));
        assertThrows(InvalidPlayerException.class, () -> game.fetchHandObjective(new PlayerLobby("3", ColorToken.BLUE)));

        for(PlayerLobby player : players) {
            assertNull(game.fetchStarter(player));
            assertTrue(game.fetchHandPlayable(player).isEmpty());
            assertNull(game.fetchHandObjective(player));
            assertTrue(game.fetchAvailableCoord(player).isEmpty());
        }

        assertNull(game.fetchGameStatus());
    }

    @Test
    public void testStartGame() throws InvalidPlayerException, InvalidPlayersNumberException, GameStatusException, InvalidChoiceException, VariableAlreadySetException, InvalidPlayCardException {
        testCreation();
        game.startGame();
        assertThrows(GameStatusException.class, ()->game.nextTurn());

        //Test of 3 possibly changed turn-async non-player-specific methods
        assertEquals(GameStatus.INIT, game.fetchGameStatus());
        assertNull(game.fetchCurrentPlayer());

        List<? extends CardPlayableIF> pickables = game.fetchPickables();
        assertEquals(pickables.size(), 6);
        assertEquals(Side.SIDEBACK, pickables.get(0).getVisibleSide());
        assertEquals(Side.SIDEFRONT, pickables.get(1).getVisibleSide());
        assertEquals(Side.SIDEFRONT, pickables.get(2).getVisibleSide());
        assertEquals(Side.SIDEBACK, pickables.get(3).getVisibleSide());
        assertEquals(Side.SIDEFRONT, pickables.get(4).getVisibleSide());
        assertEquals(Side.SIDEFRONT, pickables.get(5).getVisibleSide());
        assertInstanceOf(CardResource.class, pickables.get(0));
        assertInstanceOf(CardResource.class, pickables.get(1));
        assertInstanceOf(CardResource.class, pickables.get(2));
        assertInstanceOf(CardGold.class, pickables.get(3));
        assertInstanceOf(CardGold.class, pickables.get(4));
        assertInstanceOf(CardGold.class, pickables.get(5));

        //Test of 4 turn-async player-specific methods
        for(PlayerLobby player : players) {
            assertInstanceOf(CardStarter.class, game.fetchStarter(player));
            assertNull(game.fetchStarter(player).getVisibleSide()); // Card starter has not been player yet

            assertEquals(3, game.fetchHandPlayable(player).size());
            assertEquals(2, game.fetchHandPlayable(player).stream().
                    filter(c -> c instanceof CardResource).toList().size());
            assertEquals(1, game.fetchHandPlayable(player).stream().
                    filter(c -> c instanceof CardGold).toList().size());
            for(CardPlayableIF card : game.fetchHandPlayable(player))
                assertNull(card.getVisibleSide());

            assertNull(game.fetchHandObjective(player));

            List<Coordinates> coords = game.fetchAvailableCoord(player);
            assertEquals(0, coords.size());
        }

        // Now we cannot call again startGame
        assertThrows(GameStatusException.class, game::startGame);
        // I assume that internal state has not changed...

        // Test of 3 methods callable in INIT phase
        boolean first = true;
        for(PlayerLobby p : players) {
            game.playStarter(p, Side.SIDEBACK);
            assertEquals(Side.SIDEBACK, game.fetchStarter(p).getVisibleSide());
            assertThrows(GameStatusException.class, ()->game.nextTurn());
            assertThrows(InvalidPlayCardException.class, ()->game.playStarter(p, Side.SIDEFRONT));
            List<Coordinates> coords = game.fetchAvailableCoord(p);
            assertEquals(4, coords.size());
            assertTrue(coords.containsAll(Coordinates.createOrigin().fetchNearCoordinates()));

            List<? extends CardObjectiveIF> possibleObj = game.fetchPersonalObjectives(p);
            assertEquals(2, possibleObj.size());
            assertInstanceOf(CardObjective.class, possibleObj.get(0));
            assertInstanceOf(CardObjective.class, possibleObj.get(1));
            assertNull(possibleObj.get(0).getVisibleSide());
            assertNull(possibleObj.get(1).getVisibleSide());
            assertNull(game.fetchHandObjective(p));

            assertThrows(InvalidChoiceException.class,
                    ()->game.choosePersonalObjective(p, game.fetchCommonObjectives().getFirst()));
            game.choosePersonalObjective(p, possibleObj.getFirst());
            if (first) {
                assertThrows(VariableAlreadySetException.class,         // I assume that after throwing exception, state doesn't change
                        ()->game.choosePersonalObjective(p, possibleObj.getFirst()));
                assertThrows(VariableAlreadySetException.class, ()->game.choosePersonalObjective(p, possibleObj.get(1)));
            }
            assertInstanceOf(CardObjective.class, game.fetchHandObjective(p));
            first = false;
        }

        // Now every player played their starter and chose their objective card, hence I shoud be in IN_GAME
        assertEquals(GameStatus.IN_GAME, game.fetchGameStatus());
        assertThrows(GameStatusException.class, ()->game.fetchPersonalObjectives(players.iterator().next()));
    }

    @Test
    public void testTurnOne() throws InvalidPlayerException, InvalidChoiceException, InvalidPlayersNumberException, VariableAlreadySetException, GameStatusException, RequirementsNotMetException, InvalidPlayCardException, InvalidDrawCardException, InvalidCoordinatesException {
        testStartGame();
        // Now I'm in the beginning of IN_GAME
        assertThrows(GameStatusException.class, ()->game.addoObjectivePoints());
        assertThrows(GameStatusException.class, ()->game.nextTurn());
        assertThrows(GameStatusException.class, ()->game.pickCard(game.fetchPickables().getFirst()));
        PlayerLobby currPlayer = game.fetchCurrentPlayer();
        CardObjectiveIF personalObjective = game.fetchHandObjective(currPlayer);

        // Test for RequirementsNotMet: now requirements of the only cardGold in hand are surely not met
        CardPlayableIF c1 = game.fetchHandPlayable(currPlayer).stream()
                .filter(c -> c instanceof CardGold).toList().getFirst();
        Coordinates coord = game.fetchAvailableCoord(currPlayer).getFirst();
        assertThrows(RequirementsNotMetException.class, ()->game.playCard(c1, Side.SIDEFRONT, coord));

        // Test for player not having specified card
        CardPlayableIF c2 = game.fetchPickables().getFirst();
        assertThrows(InvalidPlayCardException.class, ()->game.playCard(c2, Side.SIDEFRONT, coord));

        //Now a valid play
        CardPlayableIF c3 = game.fetchHandPlayable(currPlayer).stream()
                .filter(c -> c instanceof CardResource).toList().getFirst();
        game.playCard(c3, Side.SIDEBACK, new Coordinates(1,1));

        assertEquals(GameStatus.IN_GAME, game.fetchGameStatus());
        assertEquals(currPlayer, game.fetchCurrentPlayer());
        assertEquals(personalObjective, game.fetchHandObjective(currPlayer));
        assertEquals(2, game.fetchHandPlayable(currPlayer).size());
        assertFalse(game.fetchHandPlayable(currPlayer).contains(c3));   // I assume the other 2 are not changed
        assertEquals(Side.SIDEBACK, c3.getVisibleSide());
        assertEquals(6, game.fetchAvailableCoord(currPlayer).size());
        assertFalse(game.fetchAvailableCoord(currPlayer).contains(new Coordinates(1, 1)));
        assertTrue(game.fetchAvailableCoord(currPlayer).contains(new Coordinates(0,2)));
        assertTrue(game.fetchAvailableCoord(currPlayer).contains(new Coordinates(2,2)));
        assertTrue(game.fetchAvailableCoord(currPlayer).contains(new Coordinates(2,0)));       // I assume the other ones are not changed

        Coordinates coord2 = game.fetchAvailableCoord(currPlayer).getFirst();
        assertThrows(GameStatusException.class, ()->game.playCard(c1, Side.SIDEBACK, coord2));
        assertThrows(GameStatusException.class, ()->game.nextTurn());

        assertThrows(InvalidDrawCardException.class, ()->game.pickCard(c3));
        CardPlayableIF c4 = game.fetchPickables().get(4);
        game.pickCard(c4);

        // Now i picked the first gold card of the 2 visible ones
        List<? extends CardPlayableIF> pickables = game.fetchPickables();
        assertEquals(pickables.size(), 6);
        assertEquals(Side.SIDEBACK, pickables.get(0).getVisibleSide());
        assertEquals(Side.SIDEFRONT, pickables.get(1).getVisibleSide());
        assertEquals(Side.SIDEFRONT, pickables.get(2).getVisibleSide());
        assertEquals(Side.SIDEBACK, pickables.get(3).getVisibleSide());
        assertEquals(Side.SIDEFRONT, pickables.get(4).getVisibleSide());
        assertEquals(Side.SIDEFRONT, pickables.get(5).getVisibleSide());
        assertInstanceOf(CardResource.class, pickables.get(0));
        assertInstanceOf(CardResource.class, pickables.get(1));
        assertInstanceOf(CardResource.class, pickables.get(2));
        assertInstanceOf(CardGold.class, pickables.get(3));
        assertInstanceOf(CardGold.class, pickables.get(4));
        assertInstanceOf(CardGold.class, pickables.get(5));

        assertEquals(GameStatus.IN_GAME, game.fetchGameStatus());
        assertEquals(currPlayer, game.fetchCurrentPlayer());
        assertNull(c4.getVisibleSide());
        assertEquals(3, game.fetchHandPlayable(currPlayer).size());
        assertTrue(game.fetchHandPlayable(currPlayer).contains(c4));

        assertThrows(GameStatusException.class, ()->game.playCard(c4, Side.SIDEBACK, new Coordinates(2,2)));
        assertThrows(GameStatusException.class, ()->game.pickCard(game.fetchPickables().getFirst()));

        game.nextTurn();
        for(PlayerLobby p : players)
            if(p != currPlayer)
                currPlayer = p;
        assertEquals(currPlayer, game.fetchCurrentPlayer());
        assertEquals(GameStatus.IN_GAME, game.fetchGameStatus());
        assertThrows(GameStatusException.class, ()->game.addoObjectivePoints());
        // Other checks should not need...
    }


}