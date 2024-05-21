package it.polimi.ingsw.am13.controller;

import it.polimi.ingsw.am13.ControlAction;
import it.polimi.ingsw.am13.LisForTest;
import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.model.exceptions.ConnectionException;
import it.polimi.ingsw.am13.model.exceptions.GameStatusException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.network.socket.message.response.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestLobby {

    @Test
    public void testGetInstance() {
        Lobby.resetLobby();
        Lobby lobby = Lobby.getInstance();
        assertEquals(lobby, Lobby.getInstance());
        assertTrue(lobby.getRooms().isEmpty());
        assertThrows(LobbyException.class, ()->lobby.joinRoom(1, new LisForTest("a", ColorToken.RED)));
        assertThrows(LobbyException.class, ()->lobby.endGame(1));
        assertEquals(lobby, Lobby.getInstance());
    }

    @Test
    public void testCreateRoom() throws LobbyException {
        Lobby.resetLobby();
        Lobby lobby = Lobby.getInstance();
        PlayerLobby p1 = new PlayerLobby("1", ColorToken.RED);
        LisForTest lis1 = new LisForTest(p1);
        assertThrows(LobbyException.class, ()->lobby.createRoom(lis1, 1));
        assertThrows(LobbyException.class, ()->lobby.createRoom(lis1, 5));
        assertTrue(lobby.getRooms().isEmpty());
        lobby.createRoom(lis1, 3);
        assertEquals(1, lobby.getRooms().size());
        assertEquals(1, lobby.getRooms().getFirst().getPlayers().size());
        assertEquals(p1, lobby.getRooms().getFirst().getPlayers().getFirst());
        assertEquals(3, lobby.getRooms().getFirst().getnPlayersTarget());
        assertFalse(lobby.getRooms().getFirst().isGameStarted());

        PlayerLobby p1SameNick = new PlayerLobby("1", ColorToken.BLUE);
        assertThrows(LobbyException.class, ()->lobby.createRoom(new LisForTest(p1SameNick),2));
        PlayerLobby p2 = new PlayerLobby("3", ColorToken.RED);
        LisForTest lis2 = new LisForTest(p2);
        lobby.createRoom(lis2, 2);
        assertEquals(2, lobby.getRooms().size());
        assertEquals(1, lobby.getRooms().getLast().getPlayers().size());
        assertEquals(p2, lobby.getRooms().getLast().getPlayers().getFirst());
        assertEquals(2, lobby.getRooms().getLast().getnPlayersTarget());
        assertFalse(lobby.getRooms().getFirst().isGameStarted());
    }

    @Test
    public void testJoinRoom() throws LobbyException {
        Lobby.resetLobby();
        Lobby lobby = Lobby.getInstance();
        int DIM = 5;
        PlayerLobby[] ps = new PlayerLobby[DIM];
        ps[0] = new PlayerLobby("1", ColorToken.RED);
        ps[1] = new PlayerLobby("2", ColorToken.BLUE);
        ps[2] = new PlayerLobby("3", ColorToken.YELLOW);
        ps[3] = new PlayerLobby("4", ColorToken.BLUE);
        ps[4] = new PlayerLobby("5", ColorToken.RED);
        LisForTest[] liss = new LisForTest[DIM];
        for(int i=0 ; i<DIM ; i++)
            liss[i] = new LisForTest(ps[i]);

        lobby.createRoom(liss[0], 3);
        RoomIF r1 = lobby.getRooms().getLast();
        lobby.createRoom(liss[4], 2);
        RoomIF r2 = lobby.getRooms().getLast();
        assertEquals(1, liss[0].updates.size());
        assertEquals(1, liss[4].updates.size());
        int id1 = r1.getGameId();
        int id2 = r2.getGameId();
        int idDifferent = id2+1!=id1 ? id2+1 : id2+2;
        assertThrows(LobbyException.class, ()->lobby.joinRoom(idDifferent+100, liss[1]));
        assertThrows(LobbyException.class, ()->lobby.joinRoom(id1, new LisForTest("1", ColorToken.BLUE)));
        assertThrows(LobbyException.class, ()->lobby.joinRoom(id1, new LisForTest("5", ColorToken.BLUE)));
        assertThrows(LobbyException.class, ()->lobby.joinRoom(id1, new LisForTest("2", ColorToken.RED)));
        assertEquals(2, lobby.getRooms().size());
        assertEquals(1, r1.getPlayers().size());
        assertEquals(1, r2.getPlayers().size());

        // Player 1 joins Room 1 --> 2/3 players
        lobby.joinRoom(id1, liss[1]);
        assertEquals(2, r1.getPlayers().size());
        assertTrue(r1.getPlayers().containsAll(List.of(ps[0], ps[1])));
        assertFalse(r1.isGameStarted());
        for(int i=0 ; i<=1 ; i++) {
            assertEquals(ControlAction.JOIN_ROOM, liss[i].actions.getLast());
            assertEquals(ps[1], ((MsgResponsePlayerJoinedRooom)liss[i].updates.getLast()).getPlayer());
        }
        assertEquals(0, liss[3].actions.size());
        assertEquals(1, liss[4].actions.size());        // Notifies of joining room must be sent only to listeners in that room

        // Player 2 joins Room 1 --> 3/3 players
        lobby.joinRoom(id1, liss[2]);
        assertEquals(3, r1.getPlayers().size());
        assertTrue(r1.getPlayers().contains(ps[2]));
        assertTrue(r1.isGameStarted());
        // Now the game is started, so 2 notifies are sent
        for(int i=0 ; i<=2 ; i++) {
            assertEquals(ControlAction.JOIN_ROOM, liss[i].actions.get(liss[i].actions.size()-2));
            assertEquals(ps[2], ((MsgResponsePlayerJoinedRooom)liss[i].updates.get(liss[i].updates.size()-2)).getPlayer());
            assertEquals(ControlAction.START_GAME, liss[i].actions.getLast());
            GameState state = ((MsgResponseStartGame)liss[i].updates.getLast()).getGameState();
            assertEquals(id1, state.getGameId());
        }
        assertEquals(0, liss[3].actions.size());
        assertEquals(1, liss[4].actions.size());        // Notifies of joining room must be sent only to listeners in that room
        assertThrows(LobbyException.class, ()->lobby.joinRoom(id1, liss[3]));

        assertEquals(1, r2.getPlayers().size());
        assertFalse(r2.isGameStarted());
        assertThrows(LobbyException.class, ()->lobby.joinRoom(id2, new LisForTest("2", ColorToken.BLUE)));
        lobby.joinRoom(id2, liss[3]);
        assertEquals(2, r2.getPlayers().size());
        assertTrue(r2.getPlayers().containsAll(List.of(ps[3], ps[4])));
        assertTrue(r2.isGameStarted());
        assertEquals(3, r1.getPlayers().size());
        assertTrue(r1.getPlayers().containsAll(List.of(ps[0], ps[1], ps[2])));
        // To the other room are sent no new notifies
        for(int i=0 ; i<=2 ; i++)
            assertEquals(id1, ((MsgResponseStartGame)liss[i].updates.getLast()).getGameState().getGameId());
        // Players in room2 are notified (as before with players in room1)
        for(int i=3 ; i<=4 ; i++)
            assertEquals(id2, ((MsgResponseStartGame)liss[i].updates.getLast()).getGameState().getGameId());
    }

    @Test
    public void testLeaveRoom() throws LobbyException {
        Lobby.resetLobby();
        Lobby lobby = Lobby.getInstance();
        int DIM = 3;
        PlayerLobby[] ps = new PlayerLobby[DIM];
        ps[0] = new PlayerLobby("1", ColorToken.RED);
        ps[1] = new PlayerLobby("2", ColorToken.BLUE);
        ps[2] = new PlayerLobby("3", ColorToken.YELLOW);
        LisForTest[] liss = new LisForTest[DIM];
        for(int i=0 ; i<DIM ; i++)
            liss[i] = new LisForTest(ps[i]);

        // If, after leaving, the room is empty, it is destroyed
        assertThrows(LobbyException.class, ()->lobby.leaveRoom(liss[0]));
        lobby.createRoom(liss[0], 3);
        assertEquals(1, lobby.getRooms().size());
        lobby.leaveRoom(liss[0]);
        assertTrue(lobby.getRooms().isEmpty());

        // Now player 0 created the room and player 1 joins
        lobby.createRoom(liss[0], 3);
        assertThrows(LobbyException.class, ()->lobby.leaveRoom(liss[1]));
        int id = lobby.getRooms().getFirst().getGameId();
        lobby.joinRoom(id, liss[1]);
        assertEquals(4, liss[0].actions.size());
        assertEquals(1, liss[1].actions.size());

        // Now player 1 leaves
        lobby.leaveRoom(liss[1]);
        assertEquals(5, liss[0].actions.size());
        assertEquals(2, liss[1].actions.size());
        for(int i=0 ; i<=1 ; i++) {
            assertEquals(ControlAction.LEAVE_ROOM, liss[i].actions.getLast());
            assertEquals(ps[1], ((MsgResponsePlayerLeftRoom)liss[i].updates.getLast()).getPlayer());
        }
        assertFalse(lobby.getRooms().getFirst().isGameStarted());

        // Now player 1 rejoins, player 2 joins and the game is started
        lobby.joinRoom(id, liss[1]);
        lobby.joinRoom(id, liss[2]);
        assertTrue(lobby.getRooms().getFirst().isGameStarted());

        // Now player 0 disconnects (leaves room)
        List<Integer> lastDims = Arrays.stream(liss).map(l -> l.actions.size()).toList();
        lobby.leaveRoom(liss[0]);
        assertEquals(lastDims.getFirst(), liss[0].actions.size());
        assertEquals(lastDims.getFirst(), liss[0].updates.size());      // Player disconnected does not receive update
        for(int i=1 ; i<=2 ; i++) {
            assertEquals(lastDims.get(i)+1, liss[i].actions.size());
            assertEquals(ControlAction.DISCONNECTED, liss[i].actions.getLast());
            assertEquals(lastDims.get(i)+1, liss[i].updates.size());
            assertEquals(ps[0], ((MsgResponsePlayerDisconnected)liss[i].updates.getLast()).getPlayer());
        }
    }

    @Test
    public void testReconnectPlayer() throws LobbyException, ConnectionException, GameStatusException, InvalidPlayerException {
        Lobby.resetLobby();
        Lobby lobby = Lobby.getInstance();
        int DIM = 3;
        PlayerLobby[] ps = new PlayerLobby[DIM];
        ps[0] = new PlayerLobby("1", ColorToken.RED);
        ps[1] = new PlayerLobby("2", ColorToken.BLUE);
        ps[2] = new PlayerLobby("3", ColorToken.YELLOW);
        LisForTest[] liss = new LisForTest[DIM];
        for(int i=0 ; i<DIM ; i++)
            liss[i] = new LisForTest(ps[i]);

        assertThrows(LobbyException.class, ()->lobby.reconnectPlayer(liss[0]));
        lobby.createRoom(liss[0], 3);
        int id = lobby.getRooms().getFirst().getGameId();
        lobby.joinRoom(id, liss[1]);
        lobby.joinRoom(id, liss[2]);
        assertTrue(lobby.getRooms().getFirst().isGameStarted());
        // Now game is started

        // Now player 0 disconnects and the reconnects
        assertThrows(ConnectionException.class, ()->lobby.reconnectPlayer(liss[0]));
        liss[0].controller.disconnectPlayer(ps[0]);
        List<Integer> lastDims = Arrays.stream(liss).map(l -> l.actions.size()).toList();
        lobby.reconnectPlayer(liss[0]);
        assertEquals(3, lobby.getRooms().getFirst().getPlayers().size());
        assertTrue(lobby.getRooms().getFirst().getPlayers().containsAll(List.of(ps[0], ps[1], ps[2])));
        for(int i=0 ; i<=2 ; i++) {
            assertEquals(lastDims.get(i)+1, liss[i].actions.size());
            if(i>0)
                assertEquals(ControlAction.RECONNECTED, liss[i].actions.getLast());
            else
                assertEquals(ControlAction.UPDATE_GAMEMODEL, liss[i].actions.getLast());
            assertEquals(lastDims.get(i)+1, liss[i].updates.size());
            if(i>0)
                assertEquals(ps[0], ((MsgResponsePlayerReconnected)liss[i].updates.getLast()).getPlayer());
        }
    }

    @Test
    public void testEndGame() throws LobbyException {
        Lobby.resetLobby();
        Lobby lobby = Lobby.getInstance();
        int DIM = 3;
        PlayerLobby[] ps = new PlayerLobby[DIM];
        ps[0] = new PlayerLobby("1", ColorToken.RED);
        ps[1] = new PlayerLobby("2", ColorToken.BLUE);
        ps[2] = new PlayerLobby("3", ColorToken.YELLOW);
        LisForTest[] liss = new LisForTest[DIM];
        for(int i=0 ; i<DIM ; i++)
            liss[i] = new LisForTest(ps[i]);

        lobby.createRoom(liss[0], 3);
        int id = lobby.getRooms().getFirst().getGameId();
        lobby.joinRoom(id, liss[1]);
        lobby.joinRoom(id, liss[2]);
        assertTrue(lobby.getRooms().getFirst().isGameStarted());
        // Now the game has started

        assertThrows(LobbyException.class, ()->lobby.endGame(id+1));
        assertEquals(1, lobby.getRooms().size());
        lobby.endGame(id);
        assertTrue(lobby.getRooms().isEmpty());
    }
}