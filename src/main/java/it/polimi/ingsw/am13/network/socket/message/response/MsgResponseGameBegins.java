package it.polimi.ingsw.am13.network.socket.message.response;

import java.io.Serializable;

/**
 * Response message that is sent when the game begins
 */
//todo rimuovi questa classe (no usages)?
public class MsgResponseGameBegins extends MsgResponse implements Serializable {

    public MsgResponseGameBegins() {
        super("resGameBegins");
    }
}
