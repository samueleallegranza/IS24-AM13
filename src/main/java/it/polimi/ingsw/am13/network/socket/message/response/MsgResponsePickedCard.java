package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.List;

/**
 * Response message which is sent when a player has successfully picked a card
 */
public class MsgResponsePickedCard extends MsgResponse implements Serializable {
    /**
     * The player that has picked a card
     */
    private final PlayerLobby player;
    /**
     * The list of cards that are visible to the player (the two tops of the decks and the four cards that are drawable)
     */
    private final List<? extends CardPlayableIF> updatedVisibleCards;

    /**
     * The card that has been picked.
     */
    private final CardPlayableIF pickedCard;

    /**
     * Builds a new response message with the given player, the updated visible cards and the picked card
     * @param player the player that has picked a card
     * @param updatedVisibleCards the list of cards that are visible to the player (the two tops of the decks and the four cards that are drawable)
     * @param pickedCard the card that has been picked
     */
    public MsgResponsePickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards, CardPlayableIF pickedCard) {
        super("resPickedCard");
        this.player = player;
        this.updatedVisibleCards = updatedVisibleCards;
        this.pickedCard = pickedCard;
    }

    /**
     * @return the player that has picked a card
     */
    public PlayerLobby getPlayer() {
        return player;
    }

    /**
     * @return the list of cards that are visible to the player (the two tops of the decks and the four cards that are drawable)
     */
    public List<? extends CardPlayableIF> getUpdatedVisibleCards() {
        return updatedVisibleCards;
    }

    /**
     * @return the card that has been picked
     */
    public CardPlayableIF getPickedCard() {
        return pickedCard;
    }
}
