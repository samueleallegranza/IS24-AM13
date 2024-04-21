package it.polimi.ingsw.am13.model;

import it.polimi.ingsw.am13.controller.GameListener;
import it.polimi.ingsw.am13.controller.LobbyException;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.model.player.Token;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameModel {

    private static class LisForTest implements GameListener {

        private final PlayerLobby player;

        public LisForTest(PlayerLobby player) {
            this.player = player;
        }
        public LisForTest(String nick, ColorToken color) {
            this.player = new PlayerLobby(nick, new Token(color));
        }

        @Override
        public PlayerLobby getPlayer() {
            return player;
        }
        @Override
        public void updateGameBegins(int gameId) {
        }
        @Override
        public void updatePlayerJoinedLobby(PlayerLobby player) {
        }
        @Override
        public void updatePlayerLeftLobby(PlayerLobby player) {
        }
        @Override
        public void updateStartGame(GameModelIF model) {
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
        public void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayable, Side side, Coordinates coord, int points) {
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
        public void updatePlayerDisconnected(PlayerLobby player) {
        }
        @Override
        public void updatePlayerReconnected(PlayerLobby player) {
        }

        @Override
        public void updateGameModel(GameModelIF model) {
        }

        @Override
        public Long getPing() {
            return null;
        }
        @Override
        public void updatePing(Long ping) {
        }
    }

    private enum Strategy {
        TRY_GOLD, PLAY_BACK
    }

    private List<PlayerLobby> players;
    private List<GameListener> playerListeners;
    private GameModel game;

    @Test
    public void testCreation() throws InvalidPlayersNumberException, InvalidPlayerException {
        assertThrows(InvalidPlayersNumberException.class, ()->new GameModel(1,
                List.of(new LisForTest("1", ColorToken.RED)) ));
        assertThrows(InvalidPlayersNumberException.class, ()->new GameModel(1,
                List.of(new LisForTest("1", ColorToken.RED),
                        new LisForTest("2", ColorToken.BLUE),
                        new LisForTest("3", ColorToken.RED)) ));
        assertThrows(InvalidPlayersNumberException.class, ()->new GameModel(1,
                List.of(new LisForTest("1", ColorToken.RED),
                        new LisForTest("2", ColorToken.BLACK)) ));

        players = List.of(
                new PlayerLobby("1", ColorToken.RED),
                new PlayerLobby("2", ColorToken.BLUE)
        );
        playerListeners = new ArrayList<>();
        players.forEach(p -> playerListeners.add(new LisForTest(p)));
        game = new GameModel(0, playerListeners);

        //Test of 5 turn-async non-player-specific methods
        assertNull(game.fetchGameStatus());
        assertNull(game.fetchCurrentPlayer());
        assertEquals(players, game.fetchPlayers());

        List<CardPlayableIF> pickables = game.fetchPickables();
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

        List<CardObjectiveIF> commongObj = game.fetchCommonObjectives();
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

        List<CardPlayableIF> pickables = game.fetchPickables();
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
            assertTrue(game.fetchPlayerField(p).isCoordPlaced(Coordinates.origin()));
            assertThrows(GameStatusException.class, ()->game.nextTurn());
            assertThrows(InvalidPlayCardException.class, ()->game.playStarter(p, Side.SIDEFRONT));
            List<Coordinates> coords = game.fetchAvailableCoord(p);
            assertEquals(4, coords.size());
            assertTrue(coords.containsAll(Coordinates.origin().fetchNearCoordinates()));

            List<CardObjectiveIF> possibleObj = game.fetchPersonalObjectives(p);
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
        assertThrows(GameStatusException.class, ()->game.fetchPersonalObjectives(players.getFirst()));
    }

    @Test
    public void testTurnOne() throws InvalidPlayerException, InvalidChoiceException, InvalidPlayersNumberException, VariableAlreadySetException, GameStatusException, RequirementsNotMetException, InvalidPlayCardException, InvalidDrawCardException, InvalidCoordinatesException {
        testStartGame();
        // Now I'm in the beginning of IN_GAME
        assertThrows(GameStatusException.class, ()->game.addObjectivePoints());
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
        List<CardPlayableIF> pickables = game.fetchPickables();
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
        assertThrows(GameStatusException.class, ()->game.addObjectivePoints());
        // Other checks should not need...
    }

    // Oss: I don't have the mathematical certainty that the decks will not reach a turn in which they are empty
    // So The test could fail for the asserts after pickCard, but I daresay this is a rare case.
    // In this case a re-run of the test should fix the fail
    @Test
    public void testTurnPhasesReach20() throws InvalidPlayerException, InvalidChoiceException, InvalidPlayersNumberException, VariableAlreadySetException, InvalidPlayCardException, GameStatusException, RequirementsNotMetException, InvalidDrawCardException {
        testStartGame();

        PlayerLobby player = null;
        Coordinates coord;
        Map<PlayerLobby, CardObjectiveIF> objs = new HashMap<>();
        players.forEach(p -> {
            try {
                objs.put(p, game.fetchHandObjective(p));
            } catch (InvalidPlayerException e) {
                throw new RuntimeException(e);
            }
        });
        int nTurns = 0;
        PlayerLobby first = game.fetchFirstPlayer();
        boolean finalPhase = false;
        int turnsToEnd = 0;

        do {
            assertNotEquals(player, game.fetchCurrentPlayer());
            player = game.fetchCurrentPlayer();
            if(player == first)
                nTurns++;
            assertNotNull(player);
            coord = game.fetchAvailableCoord(player).getFirst();

            if(!finalPhase)
                assertEquals(GameStatus.IN_GAME, game.fetchGameStatus(), String.valueOf(nTurns));
            else if (turnsToEnd > 0)
                assertEquals(GameStatus.FINAL_PHASE, game.fetchGameStatus(), String.valueOf(nTurns));
            else
                fail();
            assertThrows(GameStatusException.class, ()->game.addObjectivePoints());
            assertThrows(GameStatusException.class, ()->game.startGame());
            PlayerLobby finalPlayer = player;
            assertThrows(GameStatusException.class, ()->game.playStarter(finalPlayer, Side.SIDEFRONT));
            assertThrows(GameStatusException.class, ()->game.pickCard(game.fetchPickables().getFirst()));
            assertThrows(GameStatusException.class, ()->game.nextTurn());

            // Test for player not having specified card
            CardPlayableIF c1 = game.fetchPickables().getFirst();
            Coordinates finalCoord = coord;
            assertThrows(InvalidPlayCardException.class, ()->game.playCard(c1, Side.SIDEFRONT, finalCoord));

            // If a gold card can be played, I play that
            // This way I should avoid, with only 2 players, to reach the end of the decks
            CardPlayableIF playedCard = null;
            Side playedSide = Side.SIDEFRONT;
            List<CardPlayableIF> golds = game.fetchHandPlayable(player).stream()
                    .filter(c -> c instanceof CardGold).toList();
            for(CardPlayableIF c : golds) {
                try {
                    game.playCard(c, Side.SIDEFRONT, coord);
                } catch (RequirementsNotMetException e) {
                    continue;
                }
                // I managed to play that gold card
                playedCard = c;
                break;
            }
            if(playedCard == null) {
                List<CardPlayableIF> resources = game.fetchHandPlayable(player).stream()
                        .filter(c -> c instanceof CardResource).toList();
                if(!resources.isEmpty()) {
                    playedCard = resources.getFirst();
                    game.playCard(playedCard, Side.SIDEFRONT, coord);
                }
                else {
                    playedCard = game.fetchHandPlayable(player).getFirst();
                    playedSide = Side.SIDEBACK;
                    game.playCard(playedCard, Side.SIDEBACK, coord);
                }
            }
            if(!finalPhase && game.fetchPoints().get(player) >= 20) {
                turnsToEnd = (player==first) ? 3 : 2;
            } else if(finalPhase)
                turnsToEnd--;
            // Now I surely played a card

            // Test
            if(!finalPhase)
                assertEquals(GameStatus.IN_GAME, game.fetchGameStatus(), String.valueOf(nTurns));
            else
                assertEquals(GameStatus.FINAL_PHASE, game.fetchGameStatus(), String.valueOf(nTurns));
            assertEquals(player, game.fetchCurrentPlayer());
            assertEquals(objs.get(player), game.fetchHandObjective(player));
            assertEquals(2, game.fetchHandPlayable(player).size());
            assertFalse(game.fetchHandPlayable(player).contains(playedCard));   // I assume the other 2 are not changed
            assertEquals(playedSide, playedCard.getVisibleSide());

            Coordinates finalCoord1 = game.fetchAvailableCoord(player).getFirst();
            assertThrows(GameStatusException.class, ()->game.playCard(c1, Side.SIDEBACK, finalCoord1));
            assertThrows(GameStatusException.class, ()->game.nextTurn());

            CardPlayableIF finalPlayedCard = playedCard;
            assertThrows(InvalidDrawCardException.class, ()->game.pickCard(finalPlayedCard));
            CardPlayableIF pickedCard = game.fetchPickables().get(new Random().nextInt(6));
            game.pickCard(pickedCard);

            // Now I also picked a card. For the strategy in playing the cards, the decks should not end
            List<CardPlayableIF> pickables = game.fetchPickables();
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

            if(!finalPhase)
                assertEquals(GameStatus.IN_GAME, game.fetchGameStatus(), String.valueOf(nTurns));
            else
                assertEquals(GameStatus.FINAL_PHASE, game.fetchGameStatus(), String.valueOf(nTurns));
            assertEquals(player, game.fetchCurrentPlayer());
            assertNull(pickedCard.getVisibleSide());
            assertEquals(3, game.fetchHandPlayable(player).size());
            assertTrue(game.fetchHandPlayable(player).contains(pickedCard));    // I assume the other 2 didnt change

            Coordinates finalCoord2 = game.fetchAvailableCoord(player).getFirst();
            assertThrows(GameStatusException.class, ()->game.playCard(pickedCard, Side.SIDEBACK, finalCoord2));
            assertThrows(GameStatusException.class, ()->game.pickCard(game.fetchPickables().getFirst()));

            if(!finalPhase && turnsToEnd > 0)
                finalPhase = true;
        } while(game.nextTurn());

        assertEquals(GameStatus.CALC_POINTS, game.fetchGameStatus());
        assertNull(game.fetchCurrentPlayer());
//        System.out.println(nTurns);
//        for(CardPlayableIF c : game.fetchPickables()) {
//            System.out.println(c);
//        }
    }

    private void playerPlay(PlayerLobby prevPlayer, GameStatus gameStatus, int nTurns, Strategy strategy) throws InvalidPlayerException, InvalidPlayCardException, GameStatusException, RequirementsNotMetException, InvalidDrawCardException {
        assertNotEquals(prevPlayer, game.fetchCurrentPlayer());
        PlayerLobby player = game.fetchCurrentPlayer();
        assertNotNull(player);
        int nCardsHand = game.fetchHandPlayable(player).size();
        int nCardsField = game.fetchPlayerField(player).getCoordinatesPlaced().size();
        assertTrue(nCardsHand <= 3);

        // Test for player not having specified card
        CardPlayableIF c1 = game.fetchPickables().getFirst();
        Coordinates finalCoord = game.fetchAvailableCoord(player).getFirst();
        assertThrows(InvalidPlayCardException.class, ()->game.playCard(c1, Side.SIDEFRONT, finalCoord));

        CardPlayableIF playedCard = null;
        Coordinates coord = game.fetchAvailableCoord(player).getFirst();
        assertFalse(game.fetchPlayerField(player).isCoordPlaced(coord));
        if(strategy == Strategy.TRY_GOLD) {
            // If a gold card can be played, I play that
            // This way I should avoid, with only 2 players, to reach the end of the decks
            List<CardPlayableIF> golds = game.fetchHandPlayable(player).stream()
                    .filter(c -> c instanceof CardGold).toList();
            for (CardPlayableIF c : golds) {
                try {
                    game.playCard(c, Side.SIDEFRONT, coord);
                    assertEquals(Side.SIDEFRONT, c.getVisibleSide());
                } catch (RequirementsNotMetException e) {
                    continue;
                }
                // I managed to play that gold card
                playedCard = c;
                break;
            }
            if (playedCard == null) {
                List<CardPlayableIF> resources = game.fetchHandPlayable(player).stream()
                        .filter(c -> c instanceof CardResource).toList();
                if (!resources.isEmpty()) {
                    playedCard = resources.getFirst();
                    game.playCard(playedCard, Side.SIDEFRONT, coord);
                    assertEquals(Side.SIDEFRONT, playedCard.getVisibleSide());
                } else {
                    playedCard = game.fetchHandPlayable(player).getFirst();
                    game.playCard(playedCard, Side.SIDEBACK, coord);
                    assertEquals(Side.SIDEBACK, playedCard.getVisibleSide());
                }
            }
        } else if (strategy == Strategy.PLAY_BACK) {
            playedCard = game.fetchHandPlayable(player).getFirst();
            // I play always back side, never adding points!
            game.playCard(playedCard, Side.SIDEBACK, coord);
            assertEquals(Side.SIDEBACK, playedCard.getVisibleSide());
        }

        // Test
        assertTrue(game.fetchPlayerField(player).isCoordPlaced(coord));
        assertEquals(nCardsField+1, game.fetchPlayerField(player).getCoordinatesPlaced().size());

        assertEquals(gameStatus, game.fetchGameStatus(), String.valueOf(nTurns));
        assertEquals(player, game.fetchCurrentPlayer());
        assertEquals(nCardsHand-1, game.fetchHandPlayable(player).size());
        assertFalse(game.fetchHandPlayable(player).contains(playedCard));   // I assume the other 2 are not changed

        Coordinates finalCoord1 = game.fetchAvailableCoord(player).getFirst();
        assertThrows(GameStatusException.class, ()->game.playCard(c1, Side.SIDEBACK, finalCoord1));
        assertThrows(GameStatusException.class, ()->game.nextTurn());

        playerPick(player, gameStatus, nTurns);
    }

    private void playerPick(PlayerLobby player, GameStatus gameStatus, int nTurns) throws InvalidDrawCardException, GameStatusException, InvalidPlayerException {
        int nCards = game.fetchHandPlayable(player).size();
        assertTrue(nCards <= 2);
        assertThrows(InvalidDrawCardException.class, () -> game.pickCard(game.fetchHandPlayable(player).getFirst()));
        List<Integer> pickSeq = new ArrayList<>();
        for (int i = 0; i < 6; i++)
            pickSeq.add(i);
        Collections.shuffle(pickSeq);
        CardPlayableIF pickedCard = null;
        for (Integer i : pickSeq) {
            pickedCard = game.fetchPickables().get(i);
            if (pickedCard != null)
                break;
        }
        assertNotNull(pickedCard);
        game.pickCard(pickedCard);

        // Now I also picked a card.
        List<CardPlayableIF> pickables = game.fetchPickables();
        assertEquals(pickables.size(), 6);
        if(pickables.get(1)==null || pickables.get(2)==null)
            assertNull(pickables.get(0));
        if(pickables.get(4)==null || pickables.get(5)==null)
            assertNull(pickables.get(3));

        assertEquals(gameStatus, game.fetchGameStatus(), String.valueOf(nTurns));
        assertEquals(player, game.fetchCurrentPlayer());
        assertNull(pickedCard.getVisibleSide());
        assertEquals(nCards+1, game.fetchHandPlayable(player).size());
        assertTrue(game.fetchHandPlayable(player).contains(pickedCard));    // I assume the other 2 didnt change

        Coordinates finalCoord2 = game.fetchAvailableCoord(player).getFirst();
        CardPlayableIF finalPickedCard = pickedCard;
        assertThrows(GameStatusException.class, () -> game.playCard(finalPickedCard, Side.SIDEBACK, finalCoord2));
        assertThrows(GameStatusException.class, () -> game.pickCard(game.fetchPickables().getFirst()));
    }

    private void findWinner() throws GameStatusException {
        // I assume to start from being in CALC_POINT
        assertEquals(GameStatus.CALC_POINTS, game.fetchGameStatus());
        assertThrows(GameStatusException.class, ()->game.calcWinner());
        game.addObjectivePoints();
        assertEquals(GameStatus.ENDED, game.fetchGameStatus());
        assertThrows(GameStatusException.class, ()->game.addObjectivePoints());
        PlayerLobby expected = players.stream().filter(player -> {
                    try {
                        return game.fetchIsConnected(player);
                    } catch (InvalidPlayerException e) {
                        throw new RuntimeException(e);
                    }
                }).
                max((a,b) -> Integer.compare(game.fetchPoints().get(a), game.fetchPoints().get(b))).orElseThrow();
        assertEquals(expected, game.calcWinner());
        assertDoesNotThrow(()->game.calcWinner());
    }

    @Test
    public void testTurnPhasesReach20_2() throws InvalidPlayerException, InvalidChoiceException, InvalidPlayersNumberException, VariableAlreadySetException, InvalidPlayCardException, GameStatusException, RequirementsNotMetException, InvalidDrawCardException {
        testStartGame();

        PlayerLobby prevPlayer = game.fetchFirstPlayer();
        prevPlayer = (players.get(0)==prevPlayer) ? players.get(1) : players.get(0);    // I initialize the player with the second one
        int nTurns = 0;
        boolean ended = false;
        boolean nextFinal = false;
        GameStatus gameStatus = GameStatus.IN_GAME;

        do {
            if(ended)
                fail(); // I should have ended turn-based phases, so i shouldn't be here
            else
                assertEquals(gameStatus, game.fetchGameStatus(), String.valueOf(nTurns));
            assertNotEquals(prevPlayer, game.fetchCurrentPlayer());
            assertThrows(GameStatusException.class, ()->game.addObjectivePoints());

            nTurns++;
            if(nextFinal) {
                ended = true;
            }

            // First player tries to win
            playerPlay(prevPlayer, gameStatus, nTurns, Strategy.TRY_GOLD);
            prevPlayer = game.fetchCurrentPlayer();
            if(gameStatus==GameStatus.IN_GAME && game.fetchPoints().get(game.fetchCurrentPlayer())>=20) {
                nextFinal = true;
            }

            // Now it's the turn of the second player, so there must be another turn at least
            assertTrue(game.nextTurn());
            if(nextFinal) {
                gameStatus = GameStatus.FINAL_PHASE;
            }

            // Second player plays always back sides
            playerPlay(prevPlayer, gameStatus, nTurns, Strategy.PLAY_BACK);
            prevPlayer = game.fetchCurrentPlayer();
            assertEquals(0, game.fetchPoints().get(prevPlayer));
        } while(game.nextTurn());

        assertEquals(GameStatus.CALC_POINTS, game.fetchGameStatus());
        assertNull(game.fetchCurrentPlayer());
//        System.out.println(nTurns);
//        System.out.println(game.fetchPoints().values());
//        for(CardPlayableIF c : game.fetchPickables()) {
//            System.out.println(c);
//        }
        findWinner();
//        System.out.println(game.calcWinner().getNickname());
    }

    @Test
    public void testTurnPhasesEmptyDecks() throws InvalidPlayerException, InvalidChoiceException, InvalidPlayersNumberException, VariableAlreadySetException, InvalidPlayCardException, GameStatusException, RequirementsNotMetException, InvalidDrawCardException {
        testStartGame();

        PlayerLobby prevPlayer = game.fetchFirstPlayer();
        prevPlayer = (players.get(0)==prevPlayer) ? players.get(1) : players.get(0);    // I initialize the player with the second one
        int nTurns = 0;
        boolean ended = false;
        boolean nextFinal = false;
        GameStatus gameStatus = GameStatus.IN_GAME;

        do {
            if(nextFinal)
                gameStatus = GameStatus.FINAL_PHASE;
            if(ended)
                fail(); // I should have ended turn-based phases, so i shouldn't be here
            else
                assertEquals(gameStatus, game.fetchGameStatus(), String.valueOf(nTurns));
            assertNotEquals(prevPlayer, game.fetchCurrentPlayer());
            assertThrows(GameStatusException.class, ()->game.addObjectivePoints());

            nTurns++;
            if(nextFinal) {
                ended = true;
            }

            // First player plays always back sides
            playerPlay(prevPlayer, gameStatus, nTurns, Strategy.PLAY_BACK);
            prevPlayer = game.fetchCurrentPlayer();
            assertEquals(0, game.fetchPoints().get(prevPlayer));
            if(gameStatus==GameStatus.IN_GAME && game.fetchPickables().get(0)==null && game.fetchPickables().get(3)==null)
                nextFinal = true;

            // Now it's the turn of the second player, so there must be another turn at least
            assertTrue(game.nextTurn());
            if(nextFinal)
                gameStatus = GameStatus.FINAL_PHASE;

            // Second player plays always back sides
            playerPlay(prevPlayer, gameStatus, nTurns, Strategy.PLAY_BACK);
            prevPlayer = game.fetchCurrentPlayer();
            assertEquals(0, game.fetchPoints().get(prevPlayer));
            if(gameStatus==GameStatus.IN_GAME && game.fetchPickables().get(0)==null && game.fetchPickables().get(3)==null)
                nextFinal = true;
        } while(game.nextTurn());

        assertEquals(GameStatus.CALC_POINTS, game.fetchGameStatus());
        assertNull(game.fetchCurrentPlayer());
        int nVisibleCards = game.fetchPickables().stream().filter(Objects::nonNull).toList().size();
        assertEquals(2*nTurns, 74-nVisibleCards);
//        System.out.println(nTurns);
//        System.out.println(game.fetchPoints().values());
//        for(CardPlayableIF c : game.fetchPickables()) {
//            System.out.println(c);
//        }
        findWinner();
    }

    @Test
    public void testDisconnectionsInit() throws InvalidPlayersNumberException, InvalidPlayerException, GameStatusException, ConnectionException, InvalidPlayCardException, InvalidChoiceException, VariableAlreadySetException, LobbyException {
        players = List.of(
                new PlayerLobby("1", ColorToken.RED),
                new PlayerLobby("2", ColorToken.BLUE),
                new PlayerLobby("3", ColorToken.GREEN)
        );
        playerListeners = new ArrayList<>();
        players.forEach(p -> playerListeners.add(new LisForTest(p)));
        game = new GameModel(0, playerListeners);

        for(PlayerLobby player : players) {
            assertNull(game.fetchStarter(player));
            assertTrue(game.fetchHandPlayable(player).isEmpty());
            assertNull(game.fetchHandObjective(player));
            assertTrue(game.fetchAvailableCoord(player).isEmpty());
        }
        assertNull(game.fetchGameStatus());
        game.startGame();

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

        /*
        One player disconnects. The INIT phase must go on with no problem, choosing the starter card and the personal
        objective card for them.
         */
        GameListener disPlayer = playerListeners.getFirst();
        game.disconnectPlayer(disPlayer);
        assertThrows(LobbyException.class, () -> game.disconnectPlayer(disPlayer));
        assertEquals(GameStatus.INIT, game.fetchGameStatus());
        assertNull(game.fetchCurrentPlayer());

        // Test of 3 methods callable in INIT phase
        for(PlayerLobby p : players) {
            if(p == disPlayer.getPlayer()) {
                assertNotNull(game.fetchStarter(p).getVisibleSide());
                assertTrue(game.fetchPlayerField(p).isCoordPlaced(Coordinates.origin()));
                assertThrows(GameStatusException.class, ()->game.nextTurn());
                List<Coordinates> coords = game.fetchAvailableCoord(p);
                assertEquals(4, coords.size());
                assertTrue(coords.containsAll(Coordinates.origin().fetchNearCoordinates()));

                assertNotNull(game.fetchHandObjective(p));
                assertTrue(game.fetchHandObjective(p)==game.fetchPersonalObjectives(p).get(0) ||
                        game.fetchHandObjective(p)==game.fetchPersonalObjectives(p).get(1) );
            } else {
                game.playStarter(p, Side.SIDEBACK);
                assertEquals(Side.SIDEBACK, game.fetchStarter(p).getVisibleSide());
                assertTrue(game.fetchPlayerField(p).isCoordPlaced(Coordinates.origin()));
                assertThrows(GameStatusException.class, ()->game.nextTurn());
                assertThrows(InvalidPlayCardException.class, ()->game.playStarter(p, Side.SIDEFRONT));
                List<Coordinates> coords = game.fetchAvailableCoord(p);
                assertEquals(4, coords.size());
                assertTrue(coords.containsAll(Coordinates.origin().fetchNearCoordinates()));

                List<CardObjectiveIF> possibleObj = game.fetchPersonalObjectives(p);
                assertEquals(2, possibleObj.size());
                assertInstanceOf(CardObjective.class, possibleObj.get(0));
                assertInstanceOf(CardObjective.class, possibleObj.get(1));
                assertNull(possibleObj.get(0).getVisibleSide());
                assertNull(possibleObj.get(1).getVisibleSide());
                assertNull(game.fetchHandObjective(p));

                assertThrows(InvalidChoiceException.class,
                        ()->game.choosePersonalObjective(p, game.fetchCommonObjectives().getFirst()));
                game.choosePersonalObjective(p, possibleObj.getFirst());
                assertInstanceOf(CardObjective.class, game.fetchHandObjective(p));
            }
        }

        // Now every player played their starter and chose their objective card, hence I shoud be in IN_GAME
        assertEquals(GameStatus.IN_GAME, game.fetchGameStatus());
        assertThrows(GameStatusException.class, ()->game.fetchPersonalObjectives(players.getFirst()));
    }

    @Test
    public void testDisconnectionsInGame() throws InvalidPlayersNumberException, InvalidPlayerException, GameStatusException, ConnectionException, InvalidPlayCardException, InvalidChoiceException, VariableAlreadySetException, RequirementsNotMetException, InvalidDrawCardException, LobbyException {
        players = List.of(
                new PlayerLobby("1", ColorToken.RED),
                new PlayerLobby("2", ColorToken.BLUE),
                new PlayerLobby("3", ColorToken.GREEN)
        );
        playerListeners = new ArrayList<>();
        players.forEach(p -> playerListeners.add(new LisForTest(p)));
        game = new GameModel(0, playerListeners);
        game.startGame();
        for(PlayerLobby p : players) {
            game.playStarter(p, Side.SIDEBACK);
            game.choosePersonalObjective(p, game.fetchPersonalObjectives(p).getFirst());
            assertInstanceOf(CardObjective.class, game.fetchHandObjective(p));
        }

        assertEquals(GameStatus.IN_GAME, game.fetchGameStatus());
        for(int i=0 ; i<players.size() ; i++) {
            PlayerLobby p = game.fetchCurrentPlayer();
            game.playCard(game.fetchHandPlayable(p).getFirst(), Side.SIDEBACK,
                    game.fetchAvailableCoord(p).getFirst());
            game.pickCard(game.fetchPickables().getFirst());
            assertTrue(game.nextTurn());
        }

        // Now 1 round (1 turn per each player) is done
        // A player disconnects
        GameListener disPlayer = playerListeners.getFirst();
        game.disconnectPlayer(disPlayer);
        for(int i=0 ; i<players.size() ; i++) {
            PlayerLobby p = game.fetchCurrentPlayer();
            int nPlayedCards = game.fetchPlayerField(p).getCoordinatesPlaced().size();
            List<CardPlayableIF> handCards = game.fetchHandPlayable(p);
            game.playCard(game.fetchHandPlayable(p).getFirst(), Side.SIDEBACK,
                    game.fetchAvailableCoord(p).getFirst());
            if(!game.fetchIsConnected(p)) {
                assertEquals(nPlayedCards, game.fetchPlayerField(p).getCoordinatesPlaced().size()); //I assume nothing is changed in field
                assertEquals(3, game.fetchHandPlayable(p).size());
            } else {
                assertEquals(nPlayedCards+1, game.fetchPlayerField(p).getCoordinatesPlaced().size()); //I assume nothing is changed in field
                assertEquals(2, game.fetchHandPlayable(p).size());
            }
            game.pickCard(game.fetchPickables().getFirst());
            assertEquals(3, game.fetchHandPlayable(p).size());
            if(!game.fetchIsConnected(p))
                assertTrue(game.fetchHandPlayable(p).containsAll(handCards));
            assertTrue(game.nextTurn());
        }

        //Now player reconnects, and will disconnect after playing a card but before drawing
        game.reconnectPlayer(disPlayer);
        for(int i=0 ; i<players.size() ; i++) {
            PlayerLobby p = game.fetchCurrentPlayer();
            int nPlayedCards = game.fetchPlayerField(p).getCoordinatesPlaced().size();
            game.playCard(game.fetchHandPlayable(p).getFirst(), Side.SIDEBACK,
                    game.fetchAvailableCoord(p).getFirst());
            assertEquals(nPlayedCards+1, game.fetchPlayerField(p).getCoordinatesPlaced().size()); //I assume nothing is changed in field
            assertEquals(2, game.fetchHandPlayable(p).size());
            if(p == disPlayer.getPlayer())
                game.disconnectPlayer(disPlayer);
            else {
                CardPlayableIF cardDrawn = game.fetchPickables().getFirst();
                game.pickCard(cardDrawn);
                assertTrue(game.fetchHandPlayable(p).contains(cardDrawn));
            }
            assertEquals(3, game.fetchHandPlayable(p).size());
            assertTrue(game.nextTurn());
        }
    }

    @Test
    public void testDisconnectionsForWinnerMaxPoints() throws InvalidPlayersNumberException, GameStatusException, InvalidPlayCardException, InvalidChoiceException, VariableAlreadySetException, RequirementsNotMetException, InvalidDrawCardException, ConnectionException, InvalidPlayerException, LobbyException {
        players = List.of(
                new PlayerLobby("1", ColorToken.RED),
                new PlayerLobby("2", ColorToken.BLUE),
                new PlayerLobby("3", ColorToken.GREEN)
        );
        playerListeners = new ArrayList<>();
        players.forEach(p -> playerListeners.add(new LisForTest(p)));
        game = new GameModel(0, playerListeners);
        game.startGame();
        for(PlayerLobby p : players) {
            game.playStarter(p, Side.SIDEBACK);
            game.choosePersonalObjective(p, game.fetchPersonalObjectives(p).getFirst());
            assertInstanceOf(CardObjective.class, game.fetchHandObjective(p));
        }
        assertEquals(GameStatus.IN_GAME, game.fetchGameStatus());

        PlayerLobby prevPlayer = game.fetchFirstPlayer();
        prevPlayer = (players.get(0)==prevPlayer) ? players.get(1) : players.get(0);    // I initialize the player with the second one
        int nTurns = 0;
        boolean ended = false;
        boolean nextFinal = false;
        GameStatus gameStatus = GameStatus.IN_GAME;

        do {
            if(ended)
                fail(); // I should have ended turn-based phases, so i shouldn't be here
            else
                assertEquals(gameStatus, game.fetchGameStatus(), String.valueOf(nTurns));
            assertNotEquals(prevPlayer, game.fetchCurrentPlayer());
            assertThrows(GameStatusException.class, ()->game.addObjectivePoints());

            nTurns++;
            if(nextFinal) {
                ended = true;
            }

            // First player tries to win
            playerPlay(prevPlayer, gameStatus, nTurns, Strategy.TRY_GOLD);
            prevPlayer = game.fetchCurrentPlayer();
            if(gameStatus==GameStatus.IN_GAME && game.fetchPoints().get(game.fetchCurrentPlayer())>=20) {
                nextFinal = true;
            }

            // Now it's the turn of the second player, so there must be another turn at least
            assertTrue(game.nextTurn());
            if(nextFinal) {
                gameStatus = GameStatus.FINAL_PHASE;
            }

            // Second player plays always back sides
            playerPlay(prevPlayer, gameStatus, nTurns, Strategy.PLAY_BACK);
            prevPlayer = game.fetchCurrentPlayer();
            assertEquals(0, game.fetchPoints().get(prevPlayer));
            assertTrue(game.nextTurn());

            // Third player plays always back sides
            playerPlay(prevPlayer, gameStatus, nTurns, Strategy.PLAY_BACK);
            prevPlayer = game.fetchCurrentPlayer();
            assertEquals(0, game.fetchPoints().get(prevPlayer));
        } while(game.nextTurn());

        assertEquals(GameStatus.CALC_POINTS, game.fetchGameStatus());
        //Now player 1 should win, but he/she disconnects
        GameListener disPlayer = playerListeners.stream().filter(l -> l.getPlayer()==game.fetchFirstPlayer()).toList().getFirst();
        game.disconnectPlayer(disPlayer);
//        System.out.println(nTurns);
//        System.out.println(game.fetchPoints().values());
//        for(CardPlayableIF c : game.fetchPickables()) {
//            System.out.println(c);
//        }
        findWinner();
        assertNotEquals(game.fetchFirstPlayer(), game.calcWinner());
//        System.out.println(game.calcWinner().getNickname());
    }

}