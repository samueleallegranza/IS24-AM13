package it.polimi.ingsw.am13.network.socket.message.response;

/**
 * Response message which is sent to signal the end of the game
 */
public class MsgResponseEndGame extends MsgResponse{
    public MsgResponseEndGame() {
        super("resEndGame");
    }
}
