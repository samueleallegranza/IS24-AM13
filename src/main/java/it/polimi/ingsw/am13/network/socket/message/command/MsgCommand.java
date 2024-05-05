package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.network.socket.message.Message;

public abstract class MsgCommand extends Message {
    private String nickname;

    public MsgCommand(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
