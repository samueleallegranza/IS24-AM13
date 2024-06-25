package it.polimi.ingsw.am13.client.network.rmi;

import it.polimi.ingsw.am13.client.chat.Chat;
import it.polimi.ingsw.am13.client.chat.ChatMessage;
import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.gamestate.GameStateHandler;
import it.polimi.ingsw.am13.client.view.View;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.network.rmi.GameControllerRMIIF;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

/**
 * Client-side listener for RMI connection.
 * It is a remote object, and it should be exposed by the client by passing it when it connects to the room/game.
 * It handles the updates the server sends to the clients, by modifying the view and updating the internal game's state
 */
public class GameListenerClientRMI extends UnicastRemoteObject implements GameListenerClientRMIIF {

    /**
     * Player associated to this listener
     */
    private final PlayerLobby player;

    /**
     * Handler of the network, which creates this listener
     */
    private final NetworkHandlerRMI networkHandler;

    /**
     * View the listener refers to
     */
    private final View view;

    /**
     * Handler of the game's state
     */
    private GameStateHandler stateHandler;

    private Chat chat;

    /**
     * Creates a new client-side listener, setting the player it represents and the network's handler which created it
     * @param player Player associated to the listener
     * @param networkHandler Handler of the network which created the listener
     */
    public GameListenerClientRMI(PlayerLobby player, NetworkHandlerRMI networkHandler) throws RemoteException {
        super();
        this.player = player;
        this.networkHandler = networkHandler;
        this.view = networkHandler.getView();
        stateHandler = null;
        chat=null;
    }

    /**
     * @return Player associated to this listener
     */
    public PlayerLobby getPlayer() throws RemoteException {
        return player;
    }

    /**
     * A player has joined the room
     * @param player player who joined
     */
    public void updatePlayerJoinedRoom(PlayerLobby player) throws RemoteException {
//        if(this.player.equals(player))
            view.showPlayerJoinedRoom(player);
    }

    /**
     * A player left the room
     * @param player Player who left
     */
    public void updatePlayerLeftRoom(PlayerLobby player) throws RemoteException {
//        if(this.player.equals(player))
            view.showPlayerLeftRoom(player);
    }

    /**
     * The game has started: starter cards and initial cards have been given to the players.
     * The specified model contains the initial set up for the started game.
     * This triggers also the beginning of the thread for sending pings
     * @param state The current game state when the method is called
     * @param controller Controller of the newly started game
     */
    public void updateStartGame(GameState state, GameControllerRMIIF controller)
            throws RemoteException {
        networkHandler.setController(controller);
        stateHandler = new GameStateHandler(state);
        chat=new Chat(state.getPlayers(),player);
        view.showStartGame(stateHandler.getState(),chat);
        networkHandler.startPing();
    }

    /**
     * This method should be called once the player has reconnected to an already started game.
     * It updates the entire game's state, i.e. it creates a new game's state starting from the specified model
     * (reconstructing the current situation in game).
     * @param state The current game state when the method is called
     * @param player Player for whom the update of game model is send
     */
    public void updateGameModel(GameState state, GameControllerRMIIF controller, PlayerLobby player) throws RemoteException {
        stateHandler = new GameStateHandler(state);
        chat = new Chat(state.getPlayers(),player);
        networkHandler.setController(controller);
        networkHandler.startPing();
        view.showStartGameReconnected(stateHandler.getState(), player,chat);
    }

    /**
     * A player has played their starter card.
     * @param player The player that played the starter card.
     * @param cardStarter The starter card played.
     * @param availableCoords The list of coordinates that are available to play a card
     */
    public void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter, List<Coordinates> availableCoords) throws RemoteException {
        stateHandler.updatePlayedStarter(player, cardStarter, availableCoords);
        view.showPlayedStarter(player);
    }

    /**
     * A player has chosen their personal objective card.
     * @param player The player that chose the personal objective card.
     * @param chosenObj Objective card chosen by the player
     */
    public void updateChosenPersonalObjective(PlayerLobby player, CardObjectiveIF chosenObj) throws RemoteException {
        stateHandler.updateChosenPersonalObjective(player, chosenObj);
        view.showChosenPersonalObjective(player);
    }

    /**
     * The game has begun the turn-based phase.
     */
    public void updateInGame() throws RemoteException {
        stateHandler.updateInGame();
        view.showInGame();
    }

    /**
     * The turn has passed to another player.
     * @param player The player that is going to play the next turn.
     */
    public void updateNextTurn(PlayerLobby player) throws RemoteException {
        stateHandler.updateNextTurn(player);
        view.showNextTurn();
    }

    /**
     * A player has played a card.
     * @param player The player that played the card.
     * @param cardPlayed The card played.
     * @param coord The coordinates where the card has been placed, relative to the player's field.
     * @param points The points given by the card.
     * @param availableCoords The list of coordinates that are available to play a card
     */
    public void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayed, Coordinates coord,
                                 int points, List<Coordinates> availableCoords) throws RemoteException {
        stateHandler.updatePlayedCard(player, cardPlayed, coord, points, availableCoords);
        view.showPlayedCard(player, coord);
    }

    /**
     * A player has picked a card from the common visible cards.
     * @param player The player that picked the card.
     * @param updatedVisibleCards The updated list of visible cards in the common field.
     * @param pickedCard The card picked by the player
     */
    public void updatePickedCard(PlayerLobby player, List<CardPlayableIF> updatedVisibleCards, CardPlayableIF pickedCard) throws RemoteException {
        stateHandler.updatePickedCard(player, updatedVisibleCards, pickedCard);
        view.showPickedCard(player);
    }

    /**
     * The game is in the final phase.
     * @param turnsToEnd Number of turns to reach the end of the turn-based phase
     */
    public void updateFinalPhase(int turnsToEnd) throws RemoteException {
        stateHandler.updateFinalPhase(turnsToEnd);
        view.showFinalPhase();
    }

    /**
     * The points given by Objective cards (common and personal) have been calculated.
     * Following the game's rules, the specified points are the final ones
     * @param pointsMap A map containing the points of each player.
     */
    public void updatePoints(Map<PlayerLobby, Integer> pointsMap) throws RemoteException {
        stateHandler.updatePoints(pointsMap);
        view.showUpdatePoints();
    }

    /**
     * The winner has been calculated
     *
     * @param winners The player(s) that won the game.
     */
    public void updateWinner(List<PlayerLobby> winners) throws RemoteException {
        stateHandler.updateWinner(winners);
        view.showWinner();
    }

    /**
     * The game has ended.
     * After this update, the server should not respond to any other request
     * This triggers also the interruption for the thread sending pings
     */
    public void updateEndGame() throws RemoteException {
        view.showEndGame();
        networkHandler.stopPing();
    }

    /**
     * A player has disconnected from the game.
     * @param player The player that has disconnected.
     */
    public void updatePlayerDisconnected(PlayerLobby player) throws RemoteException {
        stateHandler.updatePlayerDisconnected(player);
        view.showPlayerDisconnected(player);
    }

    /**
     * A player has reconnected to the game.
     * @param player The player that has reconnected.
     */
    public void updatePlayerReconnected(PlayerLobby player) throws RemoteException {
        stateHandler.updatePlayerReconnected(player);
        view.showPlayerReconnected(player);
    }

    /**
     * Updates the client with a chat message
     *
     * @param sender    of the message
     * @param receivers of the message
     * @param text      content of the message
     */
    @Override
    public void updateChatMessage(PlayerLobby sender, List<PlayerLobby> receivers, String text) throws RemoteException{
        chat.addMessage(receivers,new ChatMessage(sender,text));
        view.showChatMessage(sender,receivers);
    }
}
