package it.polimi.ingsw.am13.controller;

import it.polimi.ingsw.am13.ControlAction;
import it.polimi.ingsw.am13.LisForTest;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestRoom {

    @Test
    public void testCreation() throws LobbyException {
        LisForTest lis = new LisForTest("1", ColorToken.BLUE);
        assertThrows(LobbyException.class, ()->new Room(0, lis, 1));
        assertThrows(LobbyException.class, ()->new Room(0, lis, 5));
        Room room = new Room(0, lis, 4);
        assertEquals(0, room.getGameId());
        assertEquals(1, room.getPlayers().size());
        assertEquals(new PlayerLobby("1", ColorToken.BLUE), room.getPlayers().getFirst());
        assertEquals(ControlAction.JOIN_ROOM, lis.actions.getLast());
        assertFalse(room.isGameStarted());
        assertEquals(4, room.getnPlayersTarget());
    }

    @Test
    public void testJoinRoom() throws LobbyException {
        Room room = new Room(1,
                new LisForTest("1", ColorToken.BLUE),
                3);
        assertThrows(LobbyException.class, ()->room.reconnectToRoom(new LisForTest("2", ColorToken.RED), null));
        room.joinRoom(new LisForTest("2", ColorToken.RED));
        assertEquals(2, room.getPlayers().size());
        assertEquals(new PlayerLobby("2", ColorToken.RED), room.getPlayers().get(1));
        assertFalse(room.isGameStarted());
        room.joinRoom(new LisForTest("3", ColorToken.RED));
        // It should not check the duplicate tokens
        assertEquals(3, room.getPlayers().size());
        assertEquals(new PlayerLobby("3", ColorToken.RED), room.getPlayers().get(2));
        assertTrue(room.isGameStarted());
        assertThrows(LobbyException.class, ()->room.joinRoom(new LisForTest("4", ColorToken.RED)));
        assertEquals(3, room.getPlayers().size());
    }

    @Test
    public void testLeaveRoomAndReconnect() throws LobbyException {
        PlayerLobby p1 = new PlayerLobby("1", ColorToken.RED);
        PlayerLobby p2 = new PlayerLobby("2", ColorToken.RED);
        PlayerLobby p3 = new PlayerLobby("3", ColorToken.RED);
        List<LisForTest> liss = List.of(new LisForTest(p1), new LisForTest(p2), new LisForTest(p3));

        Room room = new Room(1, liss.getFirst(),3);
        assertThrows(LobbyException.class, ()->room.leaveRoom(p2));
        assertEquals(ControlAction.JOIN_ROOM, liss.getFirst().actions.getLast());      // Also the newly joined player is to be notified
        assertTrue(room.leaveRoom(p1));
        assertEquals(ControlAction.LEAVE_ROOM, liss.getFirst().actions.getLast());     // Also the newly joined player is to be notified
        assertEquals(0, room.getPlayers().size());
        room.joinRoom(liss.get(0));
        room.joinRoom(liss.get(1));
        assertFalse(room.leaveRoom(p2));
        assertEquals(ControlAction.LEAVE_ROOM, liss.get(0).actions.getLast());
        assertEquals(ControlAction.LEAVE_ROOM, liss.get(1).actions.getLast());
        room.joinRoom(liss.get(1));
        room.joinRoom(liss.get(2));
        for(LisForTest l : liss)
            assertEquals(ControlAction.JOIN_ROOM, l.actions.getLast());

        assertTrue(room.isGameStarted());
        room.leaveRoom(p1);
        assertNotEquals(ControlAction.LEAVE_ROOM, liss.get(0).actions.getLast());      // For disconnection, the disconnected player does not receive the notify
        assertEquals(ControlAction.DISCONNECTED, liss.get(1).actions.getLast());
        assertEquals(ControlAction.DISCONNECTED, liss.get(2).actions.getLast());
        assertTrue(room.isGameStarted());
        assertEquals(2, room.getPlayers().size());

        //TODO: sistema il parametro null di reconnect che dovrebbe essere il game model...
        assertThrows(LobbyException.class, ()->room.joinRoom(liss.getFirst()));
        room.reconnectToRoom(liss.getFirst(), null);
        assertEquals(3, room.getPlayers().size());
        assertTrue(room.getPlayers().containsAll(liss.stream().map(LisForTest::getPlayer).toList()));
        assertTrue(room.isGameStarted());
        assertEquals(ControlAction.UPDATE_GAMEMODEL, liss.get(0).actions.getLast());      // The reconnected players received the gamemodel
        assertEquals(ControlAction.RECONNECTED, liss.get(1).actions.getLast());
        assertEquals(ControlAction.RECONNECTED, liss.get(2).actions.getLast());

    }

}