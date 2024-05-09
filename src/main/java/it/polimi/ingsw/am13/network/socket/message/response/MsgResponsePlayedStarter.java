package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.card.CardStarterIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.List;

public class MsgResponsePlayedStarter extends MsgResponse implements Serializable {
    /**
     * The player that has played a starter
     */
    private final PlayerLobby player;
    /**
     * The starter that has been played
     */
    private final CardStarterIF starter;

    /**
     * The coordinates of the starter
     */
    private List<Coordinates> availableCoords;

    public MsgResponsePlayedStarter(PlayerLobby player, CardStarterIF starter, List<Coordinates> availableCoords) {
        super("resPlayedStarter");
        this.player = player;
        this.starter = starter;
        this.availableCoords = availableCoords;
    }

    public PlayerLobby getPlayer() {
        return player;
    }

    public CardStarterIF getStarter() {
        return starter;
    }

}
