package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.controller.GameController;

import java.io.Serializable;

public class MsgResponseGameBegins extends MsgResponse implements Serializable {

    public MsgResponseGameBegins() {
        super("resGameBegins");
    }
}
