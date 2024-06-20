package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;

/**
 * Response message containing the information describing the choice of a personal objective
 */
public class MsgResponseChosenPersonalObjective extends MsgResponse implements Serializable {
    /**
     * The player that has chosen the personal objective
     */
    private final PlayerLobby player;

    /**
     * The chosen personal objective
     */
    private final CardObjectiveIF chosenObjective;

    /**
     * Builds a new response message with the given player and chosen personal objective
     * @param player the player that has chosen the personal objective
     * @param chosenObjective the chosen personal objective
     */
    public MsgResponseChosenPersonalObjective(PlayerLobby player, CardObjectiveIF chosenObjective) {
        super("resChosenPersonalObjective");
        this.player = player;
        this.chosenObjective = chosenObjective;
    }

    /**
     * @return the player that has chosen the personal objective
     */
    public PlayerLobby getPlayer() {
        return player;
    }

    /**
     * @return the chosen personal objective
     */
    public CardObjectiveIF getChosenObjective() {
        return chosenObjective;
    }
}
