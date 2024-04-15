package it.polimi.ingsw.am13.controller;

import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.CardStarterIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;
import java.util.Map;

public interface GameListener {

    PlayerLobby getPlayer();

    void updateStartGame(GameModelIF model);
    void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter);
    void updateChosenPersonalObjective(PlayerLobby player);
    void updateNextTurn(PlayerLobby player);
    void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayable, Side side, Coordinates coord);

    /**
     * Updates the view when a player picks a card from the common visible cards.
     * @param player The player that picked the card.
     * @param updatedVisibleCards The updated list of visible cards in the common field.
     */
    void updatePickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards);
    void updatePoints(Map<PlayerLobby, Integer> pointsMap);
    void updateWinner(PlayerLobby winner);
    void updatePlayerDisconnected(PlayerLobby player);
    void updatePlayerReconnected(PlayerLobby player);

    Long getPing();
    void updatePing(Long ping);
}
