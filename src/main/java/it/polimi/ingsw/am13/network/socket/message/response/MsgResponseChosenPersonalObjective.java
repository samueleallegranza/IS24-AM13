package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardStarterIF;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;

public class MsgResponseChosenPersonalObjective extends MsgResponse implements Serializable {
    /**
     * The player that has chosen the personal objective
     */
    private final PlayerLobby player;

    /**
     * The chosen personal objective
     */
    private final CardObjectiveIF chosenObjective;

    public MsgResponseChosenPersonalObjective(PlayerLobby player, CardObjectiveIF chosenObjective) {
        super("resChosenPersonalObjective");
        this.player = player;
        this.chosenObjective = chosenObjective;
    }

    public PlayerLobby getPlayer() {
        return player;
    }

    public CardObjectiveIF getChosenObjective() {
        return chosenObjective;
    }
}
