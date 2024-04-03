package it.polimi.ingsw.am13.model;

import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.CardStarterIF;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GameModel {

    /**
     * Unique number indicating the actual game model (hence the game/match to its all entirety)
     */
    private final int gameId;
    /**
     * Class match with all the information regarding the match itself and how to precess it
     */
    private final Match match;

    /**
     * Creates a new instance of <code>GameModel</code> with the specified players.
     * The players used here to create the model are the definitive players, and nobody can be added in a second time.
     * @param gameId Class match with all the information regarding the match itself and how to precess it
     * @param nicks List of nicknames for the players
     * @param colors List of colors chosen for the players, with same order as the players
     * @throws InvalidParameterException If the lists nicks, colors don't have the same size
     * @throws InvalidPlayersNumberException If lists nicks, colors have size <2 or >4, or one of the colors is black,
     * or there are duplicate chosen colors
     */
    public GameModel(int gameId, List<String> nicks, List<ColorToken> colors)
            throws InvalidParameterException, InvalidPlayersNumberException {
        this.gameId = gameId;
        if(nicks.size()!=colors.size())
            throw new InvalidParameterException("Parameters nicks, colors must have the same size");
        List<Player> players = new ArrayList<>();
        for(int i=0 ; i<nicks.size() ; i++)
            players.add(new Player(nicks.get(i), new Token(colors.get(i))));
        this.match = new Match(players);
    }

    /**
     * Creates a new instance of <code>GameModel</code> with the specified players.
     * The players used here to create the model are the definitive players, and nobody can be added in a second time.
     * @param gameId Class match with all the information regarding the match itself and how to precess it
     * @param playersLobby List of players (decontextualized from game) who will take part to the game
     * @throws InvalidPlayersNumberException If lists nicks, colors have size <2 or >4, or one of the colors is black,
     * or there are duplicate chosen colors
     */
    public GameModel(int gameId, Set<PlayerLobby> playersLobby) throws InvalidPlayersNumberException {
        this.gameId = gameId;
        List<Player> players = playersLobby.stream().map(Player::new).toList();
        this.match = new Match(players);
    }

    // TURN-ASYNC METHODS RELATED TO COMMON INFORMATION

    /**
     * @return Unique number indicating the actual game model (hence the game/match to its all entirety)
     */
    public int getGameId() {
        return gameId;
    }

    public Set<? extends PlayerLobby> fetchPlayers() {
        return match.getPlayersLobby();
    }

    /**
     * @return the player who is playing in the current turn.
     * Null if the game phase is different from IN_GAME or FINAL_PHASE (the 2 phases divided in turns)
     */
    public PlayerLobby fetchCurrentPlayer() {
        return match.getCurrentPlayer()==null ? null : match.getCurrentPlayer().getPlayerLobby();
    }

    /**
     * @return the status of the game. See class <code>GameStatus</code> for more details
     */
    public GameStatus fetchGameStatus() {
        return match.getGameStatus();
    }

    /**
     * @return a list containing the 6 cards on the table that can be picked by players
     * The order: top of resource deck, 2 visible resource cards, top of gold deck, 2 visible gold cards
     */
    public List<? extends CardPlayableIF> fetchPickables() {
        return match.fetchPickables();
    }

    /**
     * Returns the visible cards for the common objectives.
     * Note that the deck of objective cards is always visible, with top card visible from its back side.
     * Hence, this information is not included in the list
     * @return the list of the 2 common objectives.
     */
    public List<? extends CardObjectiveIF> fetchCommonObjectives() {
        return match.fetchCommonObjectives();
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
    public List<? extends CardPlayableIF> fetchHandPlayable(PlayerLobby player) throws InvalidPlayerException {
        return match.fetchHandPlayable(player);
    }

    /**
     * @param player one of the players of the match
     * @return the personal objective of the player. Null if it hasn't been initialized yet
     * @throws InvalidPlayerException if the passed player is not one of the players of the match
     */
    public CardObjectiveIF fetchHandObjective(PlayerLobby player) throws InvalidPlayerException {
        return match.fetchHandObjective(player);
    }

    // METHODS CALLABLE IN PHASE <null>

    /**
     * Sets the starter Card, sets the possible objective cards, gives the initial cards to the players.
     * Can be called only if the match has not started yet (<code>gamePhase==null</code>) and sets game phase to INIT.
     * @throws GameStatusException if this method is called when game has already started (<code>gamePhase!=null</code>)
     */
    public void startGame() throws GameStatusException {
        match.startGame();
    }

    // METHODS CALLABLE IN PHASE INIT

    /**
     * Method callable only one per player during INIT phase.
     * It plays the starter card of the given player on the passed side
     * @param player Player who has chosen which side of the starting card he wants to play
     * @param side The side of the starting card
     * @throws InvalidPlayerException If the player's token is not among the playing players in the match
     * @throws GameStatusException If game phase is not INIT
     */
    public void playStarter(PlayerLobby player, Side side) throws InvalidPlayerException, GameStatusException {
        match.playStarter(player, side);
    }

    /**
     * Method callable only during INIT phase. Returns the 2 possible objective cards the player can choose
     * @param player who should be contained in players
     * @return a list containing the two objective cards the player can choose from
     * @throws GameStatusException if this method isn't called in the INIT phase
     * @throws InvalidPlayerException if the player is not one of the players of this match
     */
    public List<? extends CardObjectiveIF> fetchPersonalObjectives(PlayerLobby player)
            throws InvalidPlayerException, GameStatusException {
        if(match.getGameStatus()!=GameStatus.INIT)
            throw new GameStatusException("Initialization has finished, the player has already chosen their objective card");
        return match.fetchPersonalObjectives(player);
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
    }



    //TODO: rivedi da qua in giù



    /**
     * This method makes the match proceed from the turn that has just been played to the next one,
     * by changing the currentPlayer and, if necessary, changing the GameStatus
     * @return False if there is no other turn to play after the one that has just been played, true otherwise
     * @throws GameStatusException If this method is called in the INIT or CALC POINTS phase
     */
    public boolean nextTurn() throws GameStatusException {
        return match.nextTurn();
    }

}
