package it.polimi.ingsw.am13.network.socket.message.response;

/**
 * Response message which is sent when the server has correctly received a ping from a client
 */
public class MsgResponsePing extends MsgResponse{
    /**
     * Builds a new response message
     */
    public MsgResponsePing() {
        super("resPing");
    }
}
