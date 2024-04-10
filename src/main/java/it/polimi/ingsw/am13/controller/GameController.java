package it.polimi.ingsw.am13.controller;

import it.polimi.ingsw.am13.model.GameModel;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;

public class GameController {
    private final GameModel gameModel;

    /**
     * Creates a new instance of <code>GameController</code> with the specified players.
     * The players used here to create the model are the definitive players, and nobody can be added afterwards.
     * It also starts the game
     * @param gameId Class match with all the information regarding the match itself and how to precess it
     * @param playersLobby List of players (decontextualized from game) who will take part to the game
     * @throws InvalidPlayersNumberException If lists nicks, colors have size <2 or >4, or one of the colors is black,
     * or there are duplicate chosen colors
     * @throws GameStatusException if this method is called when game has already started (<code>gamePhase!=null</code>)
     */
    public GameController(int gameId, List<PlayerLobby> playersLobby) throws InvalidPlayersNumberException, GameStatusException {
        gameModel=new GameModel(gameId,playersLobby);
        gameModel.startGame();
    }

    /**
     * @return Unique number indicating the actual game model (hence the game/match to its all entirety)
     */
    int getGameId(){
        return gameModel.getGameId();
    }


    /**
     * Method callable only once per player during INIT phase.
     * It plays the starter card of the given player on the passed side
     * @param player Player who has chosen which side of the starting card he wants to play
     * @param side The side of the starting card
     * @throws InvalidPlayerException If the player is not among the playing players in the match
     * @throws GameStatusException If game phase is not INIT
     * @throws InvalidPlayCardException Positioning error of the card at coordinates (0,0).
     */
    public void playStarter(PlayerLobby player, Side side) throws InvalidPlayerException, InvalidPlayCardException, GameStatusException {
        gameModel.playStarter(player,side);
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
        gameModel.choosePersonalObjective(player, cardObj);
    }

    /**
     * Plays a given card side on the field, at the given coordinates
     * @param card Card which is being played. It must be in player's hand
     * @param side Indicates whether the card is going to be played on the front or on the back
     * @param coord Coordinates in the field of the player where the card is going to be positioned
     * @throws RequirementsNotMetException If the requirements for playing the specified card in player's field are not met
     * @throws InvalidPlayCardException If the player doesn't have the specified card
     * @throws GameStatusException If this method is called in the INIT or CALC POINTS phase
     */
    public void playCard(CardPlayableIF card, Side side, Coordinates coord)
            throws RequirementsNotMetException, InvalidPlayCardException, GameStatusException {
        gameModel.playCard(card, side, coord);
    }

    /**
     * Picks one of the 6 cards on the table, and makes the game proceed to the next Turn.
     * If there is no other turn to play after this one, it adds the objective points and calculates the winner.
     * @param card A playable card that should be in the field
     * @throws InvalidDrawCardException if the passed card is not on the table
     * @throws GameStatusException if this method is called in the INIT or CALC POINTS phase
     */
    public void pickCard(CardPlayableIF card) throws InvalidDrawCardException, GameStatusException {
        gameModel.pickCard(card);
        if(!gameModel.nextTurn()){
            gameModel.addObjectivePoints();
            gameModel.calcWinner();
        }
    }


}
