package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.Map;

public class MsgResponsePointsCalculated extends MsgResponse implements Serializable {
    /**
     * The points calculated
     */
    private final Map<PlayerLobby, Integer> pointsMap;

    public MsgResponsePointsCalculated(String command, String type, Map<PlayerLobby, Integer> pointsMap) {
        super(command, type);
        this.pointsMap = pointsMap;
    }

    public Map<PlayerLobby, Integer> getPoints() {
        return pointsMap;
    }
}
