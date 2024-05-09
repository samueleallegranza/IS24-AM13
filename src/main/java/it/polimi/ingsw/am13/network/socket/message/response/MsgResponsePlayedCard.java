package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.List;

public class MsgResponsePlayedCard extends MsgResponse implements Serializable {
    /**
     * The player that has played a card
     */
    private final PlayerLobby player;
    /**
     * The card that has been played
     */
    private final CardPlayableIF cardPlayer;

    /**
     * The coordinates of the card that has been played
     */
    private final Coordinates coordinates;
    /**
     * The points that the player has earned by playing the card
     */
    private final Integer points;
    /**
     * The list of coordinates that are available after playing the card
     */
    private final List<Coordinates> availableCoordinates;

    public MsgResponsePlayedCard(PlayerLobby player, CardPlayableIF cardPlayer, Coordinates coordinates, Integer points, List<Coordinates> availableCoordinates) {
        super("resPlayedCard");
        this.player = player;
        this.cardPlayer = cardPlayer;
        this.coordinates = coordinates;
        this.points = points;
        this.availableCoordinates = availableCoordinates;
    }

    public PlayerLobby getPlayer() {
        return player;
    }

    public CardPlayableIF getCardPlayer() {
        return cardPlayer;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Integer getPoints() {
        return points;
    }

    public List<Coordinates> getAvailableCoordinates() {
        return availableCoordinates;
    }



}
