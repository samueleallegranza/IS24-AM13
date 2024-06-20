package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.card.CardStarterIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.List;

/**
 * Response message which is sent when a player has successfully played his starter card
 */
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
     * The list of coordinates that are available after playing the starter
     */
    private final List<Coordinates> availableCoords;

    /**
     * Builds a new response message with the given player, the played starter and the available coordinates
     * @param player the player that has played a starter
     * @param starter the starter that has been played
     * @param availableCoords the list of coordinates that are available after playing the card
     */
    public MsgResponsePlayedStarter(PlayerLobby player, CardStarterIF starter, List<Coordinates> availableCoords) {
        super("resPlayedStarter");
        this.player = player;
        this.starter = starter;
        this.availableCoords = availableCoords;
    }

    /**
     * @return the player that has played a starter
     */
    public PlayerLobby getPlayer() {
        return player;
    }

    /**
     * @return the starter that has been played
     */
    public CardStarterIF getStarter() {
        return starter;
    }

    /**
     * @return the list of coordinates that are available after playing the starter
     */
    public List<Coordinates> getAvailableCoords() {
        return availableCoords;
    }
}
