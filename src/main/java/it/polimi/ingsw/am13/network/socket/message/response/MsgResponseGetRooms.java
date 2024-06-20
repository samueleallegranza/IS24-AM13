package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.controller.RoomIF;

import java.util.List;

/**
 * Response message containing the rooms that are currently available
 */
public class MsgResponseGetRooms extends MsgResponse {
    /**
     * The rooms that are currently available
     */
    private final List<RoomIF> rooms;

    /**
     * Builds a new response message with the given rooms
     * @param rooms the rooms that are currently available
     */
    public MsgResponseGetRooms(List<RoomIF> rooms) {
        super("resGetRooms");
        this.rooms = rooms;
    }

    /**
     * @return the rooms that are currently available
     */
    public List<RoomIF> getRooms() {
        return rooms;
    }
}
