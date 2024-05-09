package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.List;

public class MsgResponsePickedCard extends MsgResponse implements Serializable {
    /**
     * The player that has picked a card
     */
    private final PlayerLobby player;
    /**
     * The list of cards that are visible to the player (the two tops aof the decks and the four cards that are drawable)
     */
    private final List<? extends CardPlayableIF> updatedVisibleCards;

    /**
     * The card that has been picked.
     */
    private final CardPlayableIF pickedCard;

    public MsgResponsePickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards, CardPlayableIF pickedCard) {
        super("resPickedCard");
        this.player = player;
        this.updatedVisibleCards = updatedVisibleCards;
        this.pickedCard = pickedCard;
    }

    public PlayerLobby getPlayer() {
        return player;
    }

    public List<? extends CardPlayableIF> getUpdatedVisibleCards() {
        return updatedVisibleCards;
    }

    public CardPlayableIF getPickedCard() {
        return pickedCard;
    }
}
