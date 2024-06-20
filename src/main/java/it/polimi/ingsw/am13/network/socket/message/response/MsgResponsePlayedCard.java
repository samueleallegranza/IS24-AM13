package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.List;

/**
 * Response message which is sent when a player has successfully played a card
 */
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
     * The coordinates at which the card that has been played
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

    /**
     * Builds a new response message with the given player, the played card, the coordinates of the card, the points that the player has earned and the available coordinates
     * @param player the player that has played a card
     * @param cardPlayer the card that has been played
     * @param coordinates the coordinates at which the card that has been played
     * @param points the points that the player has earned by playing the card
     * @param availableCoordinates the list of coordinates that are available after playing the card
     */
    public MsgResponsePlayedCard(PlayerLobby player, CardPlayableIF cardPlayer, Coordinates coordinates, Integer points, List<Coordinates> availableCoordinates) {
        super("resPlayedCard");
        this.player = player;
        this.cardPlayer = cardPlayer;
        this.coordinates = coordinates;
        this.points = points;
        this.availableCoordinates = availableCoordinates;
    }

    /**
     * @return the player that has played a card
     */
    public PlayerLobby getPlayer() {
        return player;
    }

    /**
     * @return the card that has been played
     */
    public CardPlayableIF getCardPlayer() {
        return cardPlayer;
    }

    /**
     * @return the coordinates of the card that has been played
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * @return the points that the player has earned by playing the card
     */
    public Integer getPoints() {
        return points;
    }

    /**
     * @return the list of coordinates that are available after playing the card
     */
    public List<Coordinates> getAvailableCoordinates() {
        return availableCoordinates;
    }



}
