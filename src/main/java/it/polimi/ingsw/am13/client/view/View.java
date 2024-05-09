package it.polimi.ingsw.am13.client.view;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;

/**
 * Interface representing methods exposed by a generic game view.
 * Some methods handle the lobby phase, when the view should show the list of available rooms and players joining the room.
 * The other methods can be used to modify the view in order to respond to the updates coming from the server.
 */
public interface View {

    /**
     * Shows a startup screen. It should be used as first introduction view when the view is created
     */
    void showStartupScreen();

    /**
     * Shows a generic exception in the view (specific exception could be handled in different ways, also depending
     * on the phase or the state of game)
     * @param e Exception to be shown
     */
    void showException(Exception e);

    /**
     * Show a generic message for log visible to the client
     * @param msg Message to be shown
     */
    void showGenericLogMessage(String msg);

    /**
     * Shows the list of rooms returned by the server, which the player can join/reconnect to
     * @param rooms List of rooms
     */
    void showRooms(List<RoomIF> rooms);

    /**
     * Shows a player joining the room, possibly the client themself
     * @param playerLobby Player who joined the room
     */
    void showPlayerJoinedRoom(PlayerLobby playerLobby);

    /**
     * Shows a player leaving the room, possibly the client themself
     * @param player Player who left the room
     */
    void showPlayerLeftRoom(PlayerLobby player);

    /**
     * Shows the game starting. It gives the view the reference for the {@link GameState} which is kept up to date
     * @param state Reference to the game's state which is kept up to date
     */
    void showStartGame(GameState state);

    //TODO: corrisponderebbe a updateGameModel, non sonon sicuro del nome e forse potrebbe essere
    // fuso insieme a showStartGame
    void showStartGameReconnected(GameState state);


    /**
     * Shows a player playing their starter card
     * @param player Player who played their starter card
     */
    void showPlayedStarter(PlayerLobby player);

    /**
     * Shows a players choosing their personal objective card
     * @param player Player who chose their personal objective card
     */
    void showChosenPersonalObjective(PlayerLobby player);

    /**
     * Show the game entering the turn-based phase
     */
    void showInGame();

    /**
     * Shows a player placing one of their hand cards on the field
     * @param player Player who placed the card on field
     * @param coord Coordinates of the field where the card has been placed
     */
    void showPlayedCard(PlayerLobby player, Coordinates coord);

    /**
     * Shows a player picking a card
     * @param player Player who picked a card
     */
    void showPickedCard(PlayerLobby player);

    /**
     * Show the game moving on to the next turn
     */
    void showNextTurn();

    /**
     * Shows the game entering the final phase (last turns before adding extra points)
     */
    void showFinalPhase();

    /**
     * Shows the points updated after the turn-based phase is finished
     */
    void showUpdatePoints();

    /**
     * Show the winner
     */
    void showWinner();

    /**
     * Show the end of the game (after which the server deletes the game)
     */
    void showEndGame();

    /**
     * Show a player disconnecting from the game
     * @param player Player who disconnected
     */
    void showPlayerDisconnected(PlayerLobby player);

    /**
     * Show a player reconnecting to the game
     * @param player Player who reconnected
     */
    void showPlayerReconnected(PlayerLobby player);

}
