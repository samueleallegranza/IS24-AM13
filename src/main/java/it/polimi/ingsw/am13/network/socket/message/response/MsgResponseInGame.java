package it.polimi.ingsw.am13.network.socket.message.response;

/**
 * Response message which is sent when the game reaches the in game phase
 */
public class MsgResponseInGame extends MsgResponse{
    public MsgResponseInGame() {
        super("resInGame");
    }
}
