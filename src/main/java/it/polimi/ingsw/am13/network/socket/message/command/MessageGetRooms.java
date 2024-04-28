package it.polimi.ingsw.am13.network.socket.message.command;

public class MessageGetRooms extends MessageCommandBody{
    public transient String command = "getRooms";
    public String field1;
    public int field2;

    public MessageGetRooms(String field1, int field2) {
        this.field1 = field1;
        this.field2 = field2;
    }
}
