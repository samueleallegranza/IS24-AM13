package it.polimi.ingsw.am13.network.socket.message.response;

/**
 * Response message which is sent to signal the end of the game
 */
public class MsgResponseEndGame extends MsgResponse{
    /**
     * Builds a new response message
     */
    public MsgResponseEndGame() {
        super("resEndGame");
    }
}
