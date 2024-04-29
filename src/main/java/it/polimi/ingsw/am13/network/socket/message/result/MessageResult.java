package it.polimi.ingsw.am13.network.socket.message.result;

import it.polimi.ingsw.am13.network.socket.message.Message;

//FIXME: potrebbe richiedere un rework se decidiamo di cambiare Message
//FIXME: struttura alternativa a quella di MessageCommand
public abstract class MessageResult extends Message {
    private final String command;
    private final String type;

    public MessageResult(String command, String type) {
        this.command = command;
        this.type = type;
    }
}

