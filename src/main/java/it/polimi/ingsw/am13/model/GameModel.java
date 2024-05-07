package it.polimi.ingsw.am13.model;

import it.polimi.ingsw.am13.controller.GameController;
import it.polimi.ingsw.am13.controller.GameListener;
import it.polimi.ingsw.am13.controller.ListenerHandler;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.*;

import java.util.*;

public class GameModel implements GameModelIF {

    /**
     * Unique number indicating the actual game model (hence the game/match to its all entirety)
     */
    private final int gameId;
    /**
     * Class match with all the information regarding the match itself and how to precess it
     */
    private final Match match;

    /**
     * This is used to notify the view when a change occurs in the GameModel after a game event happens.
     */
    private final ListenerHandler listenerHandler;

    /**
     * Creates a new instance of <code>GameModel</code> with the specified players.
     * The players used here to create the model are the definitive players, and nobody can be added in a second time.
     * @param gameId Class match with all the information regarding the match itself and how to precess it
     * @param listenerHandler Handler of the GameListeners corresponding to the players who will take part in the game
     * @throws InvalidPlayersNumberException If lists nicks, colors have size <2 or >4, or one of the colors is black,
     * or there are duplicate chosen colors
     */
    public GameModel(int gameId, ListenerHandler listenerHandler) throws InvalidPlayersNumberException {
        this.gameId = gameId;
        List<Player> players = listenerHandler.getListeners().stream().map(GameListener::getPlayer).map(Player::new).toList();
        this.match = new Match(players);
        this.listenerHandler = listenerHandler;
    }

    // METHODS USED TO MANAGE THE DISCONNECTION AND RECONNECTION OF A PLAYER

    /**
     *
     * @return Returns the list of GameListener handled by this class.
     */
    public List<GameListener> getListeners(){
        return listenerHandler.getListeners();
    }

    /**
     * Disconnects the given player, by calling the corresponding method in match, removing the listener and
     * notifying the listeners
     * @param player Player to disconnect
     * @throws InvalidPlayerException if the player associated to the GameListener is not a player of the match
     * @throws ConnectionException if the player was already disconnected when this method was called
     */
    public void disconnectPlayer(PlayerLobby player) throws InvalidPlayerException, ConnectionException {
        match.disconnectPlayer(player);
        listenerHandler.notifyPlayerDisconnected(player);
        listenerHandler.removeListener(player);
    }

    /**
     * Reconnects the given player. It also notifies the players of this
     * @param gameListener New listener of the player who want to reconnect
     * @throws InvalidPlayerException if the player associated to the GameListener is not a player of the match
     * @throws ConnectionException if the player was already connected when this method was called
     */
    public void reconnectPlayer(GameListener gameListener) throws InvalidPlayerException, ConnectionException {
        listenerHandler.addListener(gameListener);
        match.reconnectPlayer(gameListener.getPlayer());
        listenerHandler.notifyPlayerReconnected(gameListener.getPlayer(), this);
    }

    public boolean fetchIsConnected(PlayerLobby player) throws InvalidPlayerException {
        return match.fetchIsConnected(player);
    }

    public int countConnected(){
        return match.countConnected();
    }


    // TURN-ASYNC METHODS RELATED TO COMMON INFORMATION

    /**
     * @return Unique number indicating the actual game model (hence the game/match to its all entirety)
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * @return List of players, decontextualized from game (their nicknames/tokens, nothing else)
     */
    public List<PlayerLobby> fetchPlayersLobby() {
        return match.getPlayers().stream().map(Player::getPlayerLobby).toList();
    }

    /**
     * @return List of players
     */
    public List<PlayerIF> fetchPlayers() {
        return new ArrayList<>(match.getPlayers());
    }

    /**
     * The first player of the match is the one that will do the first play of the card in phase IN_GAME
     * @return The first player of the match
     */
    public PlayerLobby fetchFirstPlayer() {
        return match.getFirstPlayer().getPlayerLobby();
    }

    /**
     * @return the player who is playing in the current turn.
     * Null if the game phase is different from IN_GAME or FINAL_PHASE (the 2 phases divided in turns)
     */
    public PlayerLobby fetchCurrentPlayer() {
        return match.getCurrentPlayer()==null ? null : match.getCurrentPlayer().getPlayerLobby();
    }

    /**
     * @return A map of points accumalated till this point for each player.
     * If the turn-bases phases haven't started yet, the points will be 0
     */
    public Map<PlayerLobby, Integer> fetchPoints() {
        Map<PlayerLobby, Integer> points = new HashMap<>();
        match.getPlayers().forEach(p -> points.put(p.getPlayerLobby(), p.getPoints()));
        return points;
    }

    /**
     * @return the status of the game. See class <code>GameStatus</code> for more details
     */
    public GameStatus fetchGameStatus() {
        return match.getGameStatus();
    }

    /**
     * List of all visible cards (that are pickable during turn phases).
     * The list is of size 6, with order: top of deck (with <code>getVisibleSide()==Side.SIDEBACK</code>),
     * and 2 visible cards (with <code>getVisibleSide()==Side.SIDEFRONT</code>), and repetion of this.
     * Elements can be null. If a deck is empty but both its cards are present, only the first element of the set of 3 will be null.
     * Besides this first element of the set, also one or both of the other ones can be null (if it remains only one or no cards
     * of this type to be picked)
     * @return a new list containing the 6 cards on the table that can be picked by players
     * The order: top of resource deck, 2 visible resource cards, top of gold deck, 2 visible gold cards
     */
    public List<CardPlayableIF> fetchPickables() {
        return new ArrayList<>(match.fetchPickables());
    }

    /**
     * List of all visible cards (that are pickable during turn phases).
     * The list is of size 6, with order: top of deck (with <code>getVisibleSide()==Side.SIDEBACK</code>),
     * and 2 visible cards (with <code>getVisibleSide()==Side.SIDEFRONT</code>), and repetion of this.
     * If a deck is empty but both its cards are present, only the first optional of the set of 3 will be empty.
     * Besides this first element of the set, also one or both of the other ones can be empty optionals
     * @return a list containing the 6 cards on the table that can be picked by players
     * The order: top of resource deck, 2 visible resource cards, top of gold deck, 2 visible gold cards
     */
    public List<Optional<? extends CardPlayableIF>> fetchPickablesOptional() {
        return new ArrayList<>(match.fetchPickablesOptional().stream()
                .map(c -> (Optional<? extends CardPlayableIF>)c).toList());
    }

    /**
     * Returns the visible cards for the common objectives.
     * Note that the deck of objective cards is always visible, with top card visible from its back side.
     * Hence, this information is not included in the list
     * @return the list of the 2 common objectives.
     */
    public List<CardObjectiveIF> fetchCommonObjectives() {
        return new ArrayList<>(match.fetchCommonObjectives());
    }


    // TURN-ASYNC METHODS RELATED TO SPECIFIC PLAYER


    /**
     * @param player One of the players of the match
     * @return The starter card assigned to player. Null if player has not been assigned a starter card yet
     * @throws InvalidPlayerException If the player is not among the playing players in the match
     */
    public CardStarterIF fetchStarter(PlayerLobby player) throws InvalidPlayerException {
        return match.fetchStarter(player);
    }

    /**
     * @param player one of the players of the match
     * @return the cards in the hand of the player. The list if empty if player has no cards yet
     * @throws InvalidPlayerException if the player is not one of the players of the match
     */
    public List<CardPlayableIF> fetchHandPlayable(PlayerLobby player) throws InvalidPlayerException {
        return new ArrayList<>(match.fetchHandPlayable(player));
    }

    /**
     * @param player one of the players of the match
     * @return the personal objective of the player. Null if it hasn't been initialized yet
     * @throws InvalidPlayerException if the passed player is not one of the players of the match
     */
    public CardObjectiveIF fetchHandObjective(PlayerLobby player) throws InvalidPlayerException {
        return match.fetchHandObjective(player);
    }

    /**
     * @return the List of coordinates in which new cards can be played
     * If game has not started yet, the list is empty
     * @throws InvalidPlayerException If player is not among this match's players
     */
    public List<Coordinates> fetchAvailableCoord(PlayerLobby player) throws InvalidPlayerException {
        return match.fetchAvailableCoord(player);
    }

    /**
     * @param player Player whose field is returned
     * @return Field (interface) of the given player
     * @throws InvalidPlayerException If the player is not among the playing players in the match
     */
    public FieldIF fetchPlayerField(PlayerLobby player) throws InvalidPlayerException {
        return match.getFieldByPlayer(player);
    }


    // METHODS CALLABLE IN PHASE <null>


    /**
     * Sets the starter Card, sets the possible objective cards, gives the initial cards to the players.
     * Can be called only if the match has not started yet (<code>gamePhase==null</code>) and sets game phase to INIT.
     * @throws GameStatusException if this method is called when game has already started (<code>gamePhase!=null</code>)
     */
    public void startGame(GameController controller) throws GameStatusException {
        match.startGame();
        listenerHandler.notifyStartGame(this, controller);
    }


    // METHODS CALLABLE IN PHASE INIT


    /**
     * Method callable only once per player during INIT phase.
     * It plays the starter card of the given player on the passed side
     * @param player Player who has chosen which side of the starting card he wants to play
     * @param side The side of the starting card
     * @throws InvalidPlayerException If the player is not among the playing players in the match
     * @throws GameStatusException If game phase is not INIT
     * @throws InvalidPlayCardException Positioning error of the card at coordinates (0,0).
     */
    public void playStarter(PlayerLobby player, Side side)
            throws InvalidPlayerException, GameStatusException, InvalidPlayCardException {
        match.playStarter(player, side);
        listenerHandler.notifyPlayedStarter(player, match.fetchStarter(player), match.fetchAvailableCoord(player));
        if(match.getGameStatus() == GameStatus.IN_GAME)
            listenerHandler.notifyInGame();
    }

    /**
     * Method callable only during INIT phase. Returns the 2 possible objective cards the player can choose
     * @param player who should be contained in players
     * @return a list containing the two objective cards the player can choose from
     * @throws GameStatusException if this method isn't called in the INIT phase
     * @throws InvalidPlayerException if the player is not one of the players of this match
     */
    public List<CardObjectiveIF> fetchPersonalObjectives(PlayerLobby player)
            throws InvalidPlayerException, GameStatusException {
        if(match.getGameStatus()!=GameStatus.INIT)
            throw new GameStatusException("Initialization has finished, the player has already chosen their objective card");
        return new ArrayList<>(match.fetchPersonalObjectives(player));
    }

    /**
     * Method callable only during INIT phase. Sets the personal objective of the player according to his choice.
     * The objective card must be between the 2 possible cards given to the player. They can be retrieved with
     * <code>fetchPersonalObjectives(player)</code>
     * @param player one of the players of the match
     * @param cardObj the objective chosen by the player
     * @throws GameStatusException if this method isn't called in the INIT phase
     * @throws InvalidPlayerException if the player is not one of the players of this match
     * @throws InvalidChoiceException if the objective card does not belong to the list of the possible objective cards for the player
     * @throws VariableAlreadySetException if this method has been called before for the player
     */
    public void choosePersonalObjective(PlayerLobby player, CardObjectiveIF cardObj)
            throws InvalidPlayerException, InvalidChoiceException, VariableAlreadySetException, GameStatusException {
        match.choosePersonalObjective(player, cardObj);
        listenerHandler.notifyChosenPersonalObjective(player, cardObj);
        if(match.getGameStatus() == GameStatus.IN_GAME)
            listenerHandler.notifyInGame();
    }


    // METHODS CALLABLE IN PHASE IN_GAME, FINAL_PHASE


    /**
     * Makes the match proceed from the turn that has just been played to the next one,
     * by changing the currentPlayer and, if necessary, changing the GameStatus
     * @return False if there is no other turn to play after the one that has just been played, true otherwise
     * @throws GameStatusException If this method is called in the INIT or CALC POINTS phase
     */
    public boolean nextTurn() throws GameStatusException {
        boolean beforeInGame = match.getGameStatus() == GameStatus.IN_GAME;
        boolean res = match.nextTurn();
        if(res) {
            listenerHandler.notifyNextTurn(fetchCurrentPlayer());
            if(beforeInGame && match.getGameStatus()==GameStatus.FINAL_PHASE)
                listenerHandler.notifyFinalPhase();
        }
        return res;
    }

    /**
     * Plays a given card side on the field, at the given coordinates
     * @param card Card which is being played. It must be in player's hand
     * @param side Indicates whether the card is going to be played on the front or on the back
     * @param coord Coordinates in the field of the player where the card is going to be positioned
     * @throws RequirementsNotMetException If the requirements for playing the specified card in player's field are not met
     * @throws InvalidPlayCardException If the player doesn't have the specified card, or generic positioning error
     * @throws GameStatusException If this method is called in the INIT or CALC POINTS phase
     */
    public void playCard(CardPlayableIF card, Side side, Coordinates coord)
            throws RequirementsNotMetException, InvalidPlayCardException, GameStatusException {
        match.playCard(card, side, coord);
        try {
            listenerHandler.notifyPlayedCard(match.getCurrentPlayer().getPlayerLobby(), card, coord, match.getCurrentPlayer().getPoints(), fetchAvailableCoord(match.getCurrentPlayer().getPlayerLobby()));
        } catch (InvalidPlayerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Picks one of the 6 cards on the table
     * @param card The playable card to pick (that should be in the common field)
     * @throws InvalidDrawCardException if the passed card is not on the table
     * @throws GameStatusException if this method is called in the INIT or CALC POINTS phase
     */
    public void pickCard(CardPlayableIF card) throws InvalidDrawCardException, GameStatusException {
        match.pickCard(card);
        listenerHandler.notifyPickedCard(match.getCurrentPlayer().getPlayerLobby(), fetchPickables(), card);
    }


    // METHODS CALLABLE IN PHASE CALC_POINTS


    /**
     * Method callable once reached phase CALC_POINTS.
     * This method adds the points given by Objective cards to each player.
     * Make the game phase go on to ENDED
     * @throws GameStatusException if this method is called in a phase which is not the CALC_POINTS phase
     */
    public void addObjectivePoints() throws GameStatusException {
        listenerHandler.notifyPointsCalculated(fetchPoints());
        match.addObjectivePoints();
    }


    // METHODS CALLABLE IN PHASE ENDED


    /**
     * Method callable only once reached phase ENDED
     * It finds the winner, ie the player with the most points
     * @return the winner of the match
     * @throws GameStatusException if this method is called in a phase which is not the ENDED phase
     */
    public PlayerLobby calcWinner() throws GameStatusException {
        PlayerLobby winner = match.calcWinner();
        listenerHandler.notifyWinner(winner);
        return winner;
    }

}
