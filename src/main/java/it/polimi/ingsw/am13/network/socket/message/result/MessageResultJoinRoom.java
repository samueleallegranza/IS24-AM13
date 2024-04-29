package it.polimi.ingsw.am13.network.socket.message.result;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

public class MessageResultJoinRoom extends MessageResult {
    private final PlayerLobby player;

    public MessageResultJoinRoom(String command, String type, PlayerLobby player) {
        super(command, type);
        this.player = player;
    }
}
