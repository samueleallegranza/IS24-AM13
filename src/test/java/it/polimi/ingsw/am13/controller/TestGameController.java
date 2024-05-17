package it.polimi.ingsw.am13.controller;

import it.polimi.ingsw.am13.ControlAction;
import it.polimi.ingsw.am13.LisForTest;
import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.GameStatus;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.PlayerIF;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.network.socket.message.response.MsgResponsePlayerDisconnected;
import it.polimi.ingsw.am13.network.socket.message.response.MsgResponsePlayerReconnected;
import it.polimi.ingsw.am13.network.socket.message.response.MsgResponseWinner;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameController {

    List<LisForTest> liss;
    GameController controller;
    GameModelIF model;

    @Test
    public void testCreation() throws LobbyException {
        liss = new ArrayList<>(List.of(
                new LisForTest("1", ColorToken.BLUE),
                new LisForTest("2", ColorToken.RED),
                new LisForTest("3", ColorToken.GREEN)
        ));
        Lobby.resetLobby();
        Lobby lobby = Lobby.getInstance();
        lobby.createRoom(liss.get(0), 3);
        int id = lobby.getRooms().getFirst().getGameId();
        lobby.joinRoom(id, liss.get(1));
        lobby.joinRoom(id, liss.get(2));
        assertTrue(lobby.getRooms().getFirst().isGameStarted());

        // I fetch the controller created with the started game (via the update triggered by lobby)
        controller = liss.getFirst().controller;
        model = liss.getFirst().model;
        assertEquals(id, controller.getGameId());
        assertEquals(3, controller.getPlayers().size());
        assertTrue(liss.stream().map(LisForTest::getPlayer).toList().containsAll(controller.getPlayers()));
        assertEquals(GameStatus.INIT, model.fetchGameStatus());
        assertEquals(3, model.fetchPlayersLobby().size());
        assertTrue(liss.stream().map(LisForTest::getPlayer).toList().containsAll(model.fetchPlayersLobby()));
    }

    @Test
    public void testUpdatePing() throws LobbyException {
        testCreation();
        for(LisForTest l : liss)
            assertNotEquals(ControlAction.UPDATE_PING, l.actions);
        List<Integer> dim0 = liss.stream().map(l -> l.actions.size()).toList();

        controller.updatePing(liss.getFirst().getPlayer());
        assertEquals(dim0.get(0)+1, liss.get(0).actions.size());
        assertEquals(ControlAction.UPDATE_PING, liss.get(0).actions.getLast());
        assertEquals(dim0.get(1), liss.get(1).actions.size());
        assertEquals(dim0.get(2), liss.get(2).actions.size());

        controller.updatePing(liss.get(2).getPlayer());
        assertEquals(dim0.get(0)+1, liss.get(0).actions.size());
        assertEquals(dim0.get(1), liss.get(1).actions.size());
        assertEquals(dim0.get(2)+1, liss.get(2).actions.size());
        assertEquals(ControlAction.UPDATE_PING, liss.get(2).actions.getLast());

        controller.updatePing(liss.getFirst().getPlayer());
        assertEquals(dim0.get(0)+2, liss.get(0).actions.size());
        assertEquals(ControlAction.UPDATE_PING, liss.get(0).actions.getLast());
        assertEquals(dim0.get(1), liss.get(1).actions.size());
        assertEquals(dim0.get(2)+1, liss.get(2).actions.size());

    }

    @Test
    void run() {
        // TODO come testarlo
    }

    @Test
    public void testPlayStarterAndChooseObj() throws LobbyException, InvalidPlayerException, InvalidPlayCardException, GameStatusException, InvalidChoiceException, VariableAlreadySetException {
        testCreation();

        //Test of 3 possibly changed turn-async non-player-specific methods
        assertEquals(GameStatus.INIT, model.fetchGameStatus());
        assertNull(model.fetchCurrentPlayer());

        List<CardPlayableIF> pickables = model.fetchPickables();
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
        for(PlayerIF player : model.fetchPlayers()) {
            assertInstanceOf(CardStarter.class, player.getStarter());
            assertNull(player.getStarter().getVisibleSide()); // Card starter has not been player yet

            assertEquals(3, model.fetchHandPlayable(player.getPlayerLobby()).size());
            assertEquals(2, model.fetchHandPlayable(player.getPlayerLobby()).stream().
                    filter(c -> c instanceof CardResource).toList().size());
            assertEquals(1, model.fetchHandPlayable(player.getPlayerLobby()).stream().
                    filter(c -> c instanceof CardGold).toList().size());
            for(CardPlayableIF card : model.fetchHandPlayable(player.getPlayerLobby()))
                assertNull(card.getVisibleSide());

            assertNull(model.fetchHandObjective(player.getPlayerLobby()));

            List<Coordinates> coords = model.fetchAvailableCoord(player.getPlayerLobby());
            assertEquals(0, coords.size());
        }

        // Test of 3 methods callable in INIT phase
        boolean first = true;
        for(PlayerLobby p : controller.getPlayers()) {
            controller.playStarter(p, Side.SIDEBACK);
            assertThrows(InvalidPlayCardException.class, ()->controller.playStarter(p, Side.SIDEFRONT));
            assertEquals(Side.SIDEBACK, model.fetchStarter(p).getVisibleSide());
            assertTrue(model.fetchPlayerField(p).isCoordPlaced(Coordinates.origin()));
            List<Coordinates> coords = model.fetchAvailableCoord(p);
            assertEquals(4, coords.size());
            assertTrue(coords.containsAll(Coordinates.origin().fetchNearCoordinates()));

            List<CardObjectiveIF> possibleObj = model.fetchPersonalObjectives(p);
            assertEquals(2, possibleObj.size());
            assertInstanceOf(CardObjective.class, possibleObj.get(0));
            assertInstanceOf(CardObjective.class, possibleObj.get(1));
            assertNull(possibleObj.get(0).getVisibleSide());
            assertNull(possibleObj.get(1).getVisibleSide());
            assertNull(model.fetchHandObjective(p));

            assertThrows(InvalidChoiceException.class,
                    ()->controller.choosePersonalObjective(p, model.fetchCommonObjectives().getFirst()));
            controller.choosePersonalObjective(p, possibleObj.getFirst());
            if (first) {
                assertThrows(VariableAlreadySetException.class,         // I assume that after throwing exception, state doesn't change
                        ()->controller.choosePersonalObjective(p, possibleObj.getFirst()));
                assertThrows(VariableAlreadySetException.class, ()->controller.choosePersonalObjective(p, possibleObj.get(1)));
            }
            assertInstanceOf(CardObjective.class, model.fetchHandObjective(p));
            first = false;
        }

        // Now every player played their starter and chose their objective card, hence I shoud be in IN_GAME
        assertEquals(GameStatus.IN_GAME, model.fetchGameStatus());
    }

    @Test
    public void testTurnOneAllOK() throws LobbyException, InvalidPlayerException, InvalidCoordinatesException, RequirementsNotMetException, InvalidPlayCardException, GameStatusException, InvalidDrawCardException, InvalidChoiceException, VariableAlreadySetException {
        testPlayStarterAndChooseObj();
        PlayerLobby currPlayer = model.fetchCurrentPlayer();
        PlayerLobby otherPlayer = (currPlayer==liss.get(0).getPlayer()) ? liss.get(1).getPlayer() : liss.get(0).getPlayer();

        assertThrows(InvalidPlayerException.class, ()->controller.playCard(otherPlayer,
                model.fetchHandPlayable(otherPlayer).getFirst(),
                Side.SIDEBACK,
                model.fetchAvailableCoord(otherPlayer).getFirst()));
        assertThrows(GameStatusException.class, ()->controller.pickCard(currPlayer, model.fetchPickables().getFirst()));

        CardObjectiveIF personalObjective = model.fetchHandObjective(currPlayer);
        // Test for RequirementsNotMet: now requirements of the only cardGold in hand are surely not met
        CardPlayableIF c1 = model.fetchHandPlayable(currPlayer).stream()
                .filter(c -> c instanceof CardGold).toList().getFirst();
        Coordinates coord = model.fetchAvailableCoord(currPlayer).getFirst();
        assertThrows(RequirementsNotMetException.class, ()->controller.playCard(currPlayer, c1, Side.SIDEFRONT, coord));

        // Test for player not having specified card
        CardPlayableIF c2 = model.fetchPickables().getFirst();
        assertThrows(InvalidPlayCardException.class, ()->controller.playCard(currPlayer, c2, Side.SIDEFRONT, coord));

        //Now a valid play
        CardPlayableIF c3 = model.fetchHandPlayable(currPlayer).stream()
                .filter(c -> c instanceof CardResource).toList().getFirst();
        controller.playCard(currPlayer, c3, Side.SIDEBACK, new Coordinates(1,1));

        assertEquals(GameStatus.IN_GAME, model.fetchGameStatus());
        assertEquals(currPlayer, model.fetchCurrentPlayer());
        assertEquals(personalObjective, model.fetchHandObjective(currPlayer));
        assertEquals(2, model.fetchHandPlayable(currPlayer).size());
        assertFalse(model.fetchHandPlayable(currPlayer).contains(c3));   // I assume the other 2 are not changed
        assertEquals(Side.SIDEBACK, c3.getVisibleSide());
        assertEquals(6, model.fetchAvailableCoord(currPlayer).size());
        assertFalse(model.fetchAvailableCoord(currPlayer).contains(new Coordinates(1, 1)));
        assertTrue(model.fetchAvailableCoord(currPlayer).contains(new Coordinates(0,2)));
        assertTrue(model.fetchAvailableCoord(currPlayer).contains(new Coordinates(2,2)));
        assertTrue(model.fetchAvailableCoord(currPlayer).contains(new Coordinates(2,0)));       // I assume the other ones are not changed

        Coordinates coord2 = model.fetchAvailableCoord(currPlayer).getFirst();
        assertThrows(GameStatusException.class, ()->controller.playCard(currPlayer, c1, Side.SIDEBACK, coord2));

        CardPlayableIF c4 = model.fetchPickables().get(4);
        assertThrows(InvalidPlayerException.class, ()->controller.pickCard(otherPlayer, c4));
        assertThrows(InvalidDrawCardException.class, ()->controller.pickCard(currPlayer, c3));
        controller.pickCard(currPlayer, c4);

        // Now i picked the first gold card of the 2 visible ones
        List<CardPlayableIF> pickables = model.fetchPickables();
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

        assertEquals(GameStatus.IN_GAME, model.fetchGameStatus());
        assertNotEquals(currPlayer, model.fetchCurrentPlayer());
        assertNull(c4.getVisibleSide());
        assertEquals(3, model.fetchHandPlayable(currPlayer).size());
        assertTrue(model.fetchHandPlayable(currPlayer).contains(c4));

        assertThrows(InvalidPlayerException.class, ()->controller.playCard(currPlayer, c4, Side.SIDEBACK, new Coordinates(2,2)));
        assertThrows(InvalidPlayerException.class, ()->controller.pickCard(currPlayer, model.fetchPickables().getFirst()));
    }

    @Test
    public void testDisconnectionsInGame() throws InvalidPlayerException, InvalidChoiceException, LobbyException, InvalidPlayCardException, VariableAlreadySetException, GameStatusException, RequirementsNotMetException, InvalidDrawCardException, ConnectionException, InterruptedException {
        testPlayStarterAndChooseObj();
        PlayerLobby currPlayer = model.fetchCurrentPlayer();

        assertEquals(GameStatus.IN_GAME, model.fetchGameStatus());
        for(int i=0 ; i<controller.getPlayers().size() ; i++) {
            PlayerLobby p = model.fetchCurrentPlayer();
            controller.playCard(p, model.fetchHandPlayable(p).getFirst(), Side.SIDEBACK,
                    model.fetchAvailableCoord(p).getFirst());
            controller.pickCard(p, model.fetchPickables().getFirst());
        }

        // Now 1 round (1 turn per each player) is done
        // A player disconnects
        PlayerLobby disPlayer = (currPlayer==liss.get(0).getPlayer()) ? liss.get(1).getPlayer() : liss.get(0).getPlayer();
        assertThrows(InvalidPlayerException.class, ()->controller.disconnectPlayer(new PlayerLobby("Pippo", ColorToken.RED)));
        controller.disconnectPlayer(disPlayer, 2000);
        for(LisForTest l : liss)
            if(!l.getPlayer().equals(disPlayer)) {
                assertEquals(ControlAction.DISCONNECTED, l.actions.getLast());
                assertEquals(disPlayer, ((MsgResponsePlayerDisconnected)l.updates.getLast()).getPlayer());
            }
        for(int i=0 ; i<controller.getPlayers().size() ; i++) {
            PlayerLobby p = model.fetchCurrentPlayer();
            assertNotEquals(disPlayer, p);        // controller manages disconnected players with ghost turns
            controller.playCard(p, model.fetchHandPlayable(p).getFirst(), Side.SIDEBACK,
                    model.fetchAvailableCoord(p).getFirst());
            controller.pickCard(p, model.fetchPickables().getFirst());
        }

        //Now player reconnects, and will disconnect after playing a card but before drawing
        LisForTest newLis = new LisForTest(disPlayer);
        controller.reconnectPlayer(newLis);
        for(LisForTest l : liss)
            if(!l.getPlayer().equals(disPlayer)) {
                assertEquals(ControlAction.RECONNECTED, l.actions.getLast());
                assertEquals(disPlayer, ((MsgResponsePlayerReconnected)l.updates.getLast()).getPlayer());
            }
        for(int i=0 ; i<controller.getPlayers().size()+2 ; i++) {
            PlayerLobby p = model.fetchCurrentPlayer();
            controller.playCard(p, model.fetchHandPlayable(p).getFirst(), Side.SIDEBACK,
                    model.fetchAvailableCoord(p).getFirst());
            if(p == disPlayer)
                controller.disconnectPlayer(disPlayer, 2000);
            else {
                CardPlayableIF cardDrawn = model.fetchPickables().getFirst();
                controller.pickCard(p, cardDrawn);
                assertTrue(model.fetchHandPlayable(p).contains(cardDrawn));
            }
            assertEquals(3, model.fetchHandPlayable(p).size());
        }
        controller.reconnectPlayer(liss.stream().filter(l->l.getPlayer().equals(disPlayer)).findFirst().orElseThrow());

        //Now player will disconnect after another player has played a card but before drawing
        for(LisForTest l : liss)
            if(!l.getPlayer().equals(disPlayer)) {
                assertEquals(ControlAction.RECONNECTED, l.actions.getLast());
                assertEquals(disPlayer, ((MsgResponsePlayerReconnected)l.updates.getLast()).getPlayer());
            }
        if(model.fetchCurrentPlayer().equals(disPlayer)) {
            controller.playCard(disPlayer, model.fetchHandPlayable(disPlayer).getFirst(), Side.SIDEBACK, model.fetchAvailableCoord(disPlayer).getFirst());
            controller.pickCard(disPlayer, model.fetchPickables().getFirst());
        }
        currPlayer = model.fetchCurrentPlayer();
        controller.playCard(currPlayer, model.fetchHandPlayable(currPlayer).getFirst(), Side.SIDEBACK, model.fetchAvailableCoord(currPlayer).getFirst());
        controller.disconnectPlayer(disPlayer, 2000);
        for(LisForTest l : liss)
            if(!l.getPlayer().equals(disPlayer)) {
                assertEquals(ControlAction.DISCONNECTED, l.actions.getLast());
                assertEquals(disPlayer, ((MsgResponsePlayerDisconnected)l.updates.getLast()).getPlayer());
            }
        controller.pickCard(currPlayer, model.fetchPickables().getFirst());
        controller.reconnectPlayer(liss.stream().filter(l->l.getPlayer().equals(disPlayer)).findFirst().orElseThrow());

        LisForTest lisOnlu = liss.stream().filter(l->l.getPlayer().equals(model.fetchCurrentPlayer())).findFirst().orElseThrow();
        for(LisForTest l : liss)
            if(l != lisOnlu)
                controller.disconnectPlayer(l.getPlayer(), 2000);
        // Now only 1 player has remained
        Thread.sleep(1000);
        assertEquals(ControlAction.DISCONNECTED, lisOnlu.actions.getLast());
        // Now 1 player reconnect in time for not making lisOnly win
        LisForTest lisOther = liss.getFirst()!=lisOnlu ? liss.getFirst() : liss.get(1);
        controller.reconnectPlayer(lisOther);
        assertEquals(ControlAction.RECONNECTED, lisOnlu.actions.getLast());
        assertEquals(ControlAction.UPDATE_GAMEMODEL, lisOther.actions.getLast());
        int dim0 = lisOnlu.actions.size();

        // Even though I wait for enough time, player should not win, as time for reconnection stopped
        Thread.sleep(3000);
        assertEquals(dim0, lisOnlu.actions.size());

        // Now really 1 player remains
        controller.disconnectPlayer(lisOther.getPlayer(), 2000);
        Thread.sleep(3000);
        assertEquals(ControlAction.WINNER, lisOnlu.actions.getLast());
    }

    @Test
    public void testDisconnectionInit() throws LobbyException, InvalidPlayerException, ConnectionException, InterruptedException {
        testCreation();
        PlayerLobby p1 = liss.getFirst().getPlayer();
        controller.disconnectPlayer(p1, 2000);
        assertThrows(InvalidPlayCardException.class,
                ()->controller.playStarter(p1, Side.SIDEFRONT));
        assertThrows(VariableAlreadySetException.class,
                ()->controller.choosePersonalObjective(p1, model.fetchPersonalObjectives(p1).getFirst()));

        PlayerLobby p2 = liss.get(1).getPlayer();
        controller.disconnectPlayer(p2, 2000);
        assertThrows(InvalidPlayCardException.class,
                ()->controller.playStarter(p2, Side.SIDEFRONT));
        assertThrows(VariableAlreadySetException.class,
                ()->controller.choosePersonalObjective(p2, model.fetchPersonalObjectives(p2).getFirst()));

        Thread.sleep(3000);
        assertEquals(ControlAction.WINNER, liss.getLast().actions.getLast());
        assertEquals(liss.getLast().getPlayer(), ((MsgResponseWinner)liss.getLast().updates.getLast()).getPlayer());
    }

    @Test
    public void testAllDisconnected() throws InvalidPlayerException, InvalidChoiceException, LobbyException, RequirementsNotMetException, InvalidDrawCardException, InvalidCoordinatesException, InvalidPlayCardException, VariableAlreadySetException, GameStatusException, ConnectionException, InterruptedException {
        testTurnOneAllOK();

        int id = model.getGameId();
        assertEquals(1, Lobby.getInstance().getRooms().stream().filter(r -> r.getGameId()==id).toList().size());
        for(LisForTest l : liss) {
//            if(l.getPlayer().equals(model.fetchCurrentPlayer()))
//                System.out.println("ciao");
            controller.disconnectPlayer(l.getPlayer(), 2000);
        }
        Thread.sleep(3000);
        assertTrue(Lobby.getInstance().getRooms().stream().filter(r -> r.getGameId()==id).toList().isEmpty());
    }

    //TODO aggiungi
    // run di gioco completa fino a fine, con fine dei deck
    // disconnessione per ping,
}