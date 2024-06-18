package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.controller.RoomIF;

import java.io.Serializable;
import java.util.List;

/**
 * Response message containing the rooms that are currently available
 */
public class MsgResponseGetRooms extends MsgResponse {
    private final List<RoomIF> rooms;

    public MsgResponseGetRooms(List<RoomIF> rooms) {
        super("resGetRooms");
        this.rooms = rooms;
    }

    public List<RoomIF> getRooms() {
        return rooms;
    }
}
