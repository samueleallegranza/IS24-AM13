package it.polimi.ingsw.am13.model;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameModel {

    private enum Strategy {
        TRY_GOLD, PLAY_BACK
    }

    private List<PlayerLobby> players;
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

        players = List.of(
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
        assertThrows(GameStatusException.class, ()->game.fetchPersonalObjectives(players.getFirst()));
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
            assertThrows(GameStatusException.class, ()->game.addoObjectivePoints());
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
                    .filter(c -> c instanceof CardGold).map(c -> (CardPlayableIF)c).toList();
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
                        .filter(c -> c instanceof CardResource).map(c -> (CardPlayableIF)c).toList();
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
            //TODO: c'è un modo per accertarci del giusto posizionamento della carta?
            //TODO: c'è un modo per accertarci della giusta aggiunta dei punti della carta?

            Coordinates finalCoord1 = game.fetchAvailableCoord(player).getFirst();
            assertThrows(GameStatusException.class, ()->game.playCard(c1, Side.SIDEBACK, finalCoord1));
            assertThrows(GameStatusException.class, ()->game.nextTurn());

            CardPlayableIF finalPlayedCard = playedCard;
            assertThrows(InvalidDrawCardException.class, ()->game.pickCard(finalPlayedCard));
            CardPlayableIF pickedCard = game.fetchPickables().get(new Random().nextInt(6));
            game.pickCard(pickedCard);

            // Now I also picked a card. For the strategy in playing the cards, the decks should not end
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
        int nCards = game.fetchHandPlayable(player).size();
        assertTrue(nCards <= 3);

        // Test for player not having specified card
        CardPlayableIF c1 = game.fetchPickables().getFirst();
        Coordinates finalCoord = game.fetchAvailableCoord(player).getFirst();
        assertThrows(InvalidPlayCardException.class, ()->game.playCard(c1, Side.SIDEFRONT, finalCoord));

        CardPlayableIF playedCard = null;
        if(strategy == Strategy.TRY_GOLD) {
            // If a gold card can be played, I play that
            // This way I should avoid, with only 2 players, to reach the end of the decks
            Coordinates coord = game.fetchAvailableCoord(player).getFirst();
            List<CardPlayableIF> golds = game.fetchHandPlayable(player).stream()
                    .filter(c -> c instanceof CardGold).map(c -> (CardPlayableIF) c).toList();
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
                        .filter(c -> c instanceof CardResource).map(c -> (CardPlayableIF) c).toList();
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
            Coordinates coord = game.fetchAvailableCoord(player).getFirst();
            // I play always back side, never adding points!
            game.playCard(playedCard, Side.SIDEBACK, coord);
            assertEquals(Side.SIDEBACK, playedCard.getVisibleSide());
        }

        // Test
        assertEquals(gameStatus, game.fetchGameStatus(), String.valueOf(nTurns));
        assertEquals(player, game.fetchCurrentPlayer());
        assertEquals(nCards-1, game.fetchHandPlayable(player).size());
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
        List<? extends CardPlayableIF> pickables = game.fetchPickables();
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
            assertThrows(GameStatusException.class, ()->game.addoObjectivePoints());

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
            assertThrows(GameStatusException.class, ()->game.addoObjectivePoints());

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
    }

}