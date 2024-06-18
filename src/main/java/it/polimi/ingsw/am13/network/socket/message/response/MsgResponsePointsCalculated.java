package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.Map;

/**
 * Response message containing the points of all the players after the last turn has been played
 * (or only a player is left and the reconnection timer expired)
 */
public class MsgResponsePointsCalculated extends MsgResponse implements Serializable {
    /**
     * The points calculated
     */
    private final Map<PlayerLobby, Integer> pointsMap;

    public MsgResponsePointsCalculated(Map<PlayerLobby, Integer> pointsMap) {
        super("resPointsCalculated");
        this.pointsMap = pointsMap;
    }

    public Map<PlayerLobby, Integer> getPoints() {
        return pointsMap;
    }
}
