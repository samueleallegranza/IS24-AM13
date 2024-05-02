package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.controller.GameController;

import java.io.Serializable;

public class MsgResponseGameBegins extends MsgResponse implements Serializable {
    //FIXME: potrebbe non servire passare il controller ma per ora lo lascio così
    private final GameController gameController;

    public MsgResponseGameBegins(String command, String type, GameController gameController) {
        super(command, type);
        this.gameController = gameController;
    }
}
