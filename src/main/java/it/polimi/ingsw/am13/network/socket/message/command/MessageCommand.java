package it.polimi.ingsw.am13.network.socket.message.command;
import it.polimi.ingsw.am13.network.socket.message.Message;
public class MessageCommand extends Message {
    public transient String command;
    public String nickname;
    public MessageCommandBody body;

    public MessageCommand(String nickname, MessageCommandBody body) {
        this.nickname = nickname;
        this.body = body;
        this.command = body.getCommand();
    }
}
