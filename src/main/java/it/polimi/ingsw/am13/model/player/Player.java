package it.polimi.ingsw.am13.model.player;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable, PlayerIF {

    /**
     * Player decontextualized from game. It stores nickname and token chosen by the player
     */
    private final PlayerLobby playerLobby;

    /**
     * Player's field which represents the positioning of his played cards.
     */
    private final Field field;

    /**
     * Player's hand of cards, which could be Gold cards or Resource cards.
     */
    private final List<CardPlayable> handCards;

    /**
     * Player's personal objective. It affects only player's points when the game ends.
     */
    private CardObjective personalObjective;

    /**
     * The two objective cards among which the player can choose his personal objective.
     * They can't change once they have been set.
     */
    private final List<CardObjective> possiblePersonalObjectives;

    /**
     * Player's starter card which has been drawn to him. It can't change once it has been set.
     */
    private CardStarter startCard;

    /**
     * Player's points counter. Initialized with 0
     */
    private int points;

    /**
     * Whether a player is currently connected or not (for the corresponding Advanced Function)
     */
    private boolean isConnected;

    /**
     * Constructor for the Player class. It initializes nickname, token, points and starter card (it has to be drawn
     * before creating a Player object). It also initializes an empty field for the player and the space to store the
     * player's hand.
     * @param nick Nickname chosen by the player.
     * @param token Token color chosen for the player.
     */
    public Player(String nick, Token token) {
        this(new PlayerLobby(nick, token));
    }

    /**
     * Constructor for the Player class. It initializes nickname, token, points and starter card (it has to be drawn
     * before creating a Player object). It also initializes an empty field for the player and the space to store the
     * player's hand.
     * @param playerLobby Player decontextualized from game. It has nickname and token already set.
     */
    public Player(PlayerLobby playerLobby) {
        this.startCard = null;
        this.playerLobby = playerLobby;
        this.points = 0;
        field = new Field();
        handCards = new ArrayList<>();
        possiblePersonalObjectives = new ArrayList<>();
        personalObjective = null;
        isConnected = true;
    }

    /**
     * Sets the isConnected variable to false
     * @throws ConnectionException if the isConnected variable was already false (repeated disconnection)
     */
    public void disconnectPlayer() throws ConnectionException {
        if(!isConnected)
            throw new ConnectionException("The player was already not connected");
        isConnected = false;
    }

    /**
     * Sets the isConnected variable to true
     * @throws ConnectionException if the isConnected variable was already true (repeated reconnection)
     */
    public void reconnectPlayer() throws ConnectionException {
        if(isConnected)
            throw new ConnectionException("The player was already connected");
        isConnected = true;
    }

    /**
     *
     * @return whether a player is currently connected or not
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Sets the drawn starter card for this player. Once set, it can't be modified!
     * @param cardStr Starter card already drawn.
     * @throws VariableAlreadySetException Exception is thrown if this method has been called before.
     */
    public void initStarter(CardStarter cardStr) throws VariableAlreadySetException{
        if(this.startCard != null)
            throw new VariableAlreadySetException();
        this.startCard = cardStr;
    }

    /**
     * Sets the two objective cards among which the player can choose his personal objective. Once set, it can't be modified
     * @param possiblePersonalObjective0 first possible personal objective cards
     * @param possiblePersonalObjective1 first possible personal objective cards
     * @throws VariableAlreadySetException if this method has been called before
     */
    public void initPossiblePersonalObjectives(CardObjective possiblePersonalObjective0, CardObjective possiblePersonalObjective1) throws VariableAlreadySetException{
        if(!possiblePersonalObjectives.isEmpty())
            throw new VariableAlreadySetException();
        possiblePersonalObjectives.add(possiblePersonalObjective0);
        possiblePersonalObjectives.add(possiblePersonalObjective1);
    }

    /**
     * Sets the player's personal objective. Once set, it can't be modified!
     * @param cardObj Objective card which will be set as personal objective.
     * @throws VariableAlreadySetException Exception is thrown if this method has been called before.
     * @throws InvalidChoiceException if the objective card does not belong to the list of the possible objective cards
     */
    public void initObjective(CardObjectiveIF cardObj) throws VariableAlreadySetException, InvalidChoiceException{
        if(this.personalObjective != null)
            throw new VariableAlreadySetException();
        CardObjective objChosen = null;
        for(CardObjective obj : possiblePersonalObjectives)
            if(obj == cardObj) {
                objChosen = obj;
                break;
            }
        if(objChosen==null)
            throw new InvalidChoiceException();
        this.personalObjective = objChosen;
    }

    /**
     * Play the corresponding given side of the starter card on the player's field.
     * @param side Starter card's side chosen by the player
     * @throws InvalidPlayCardException Positioning error of the card at coordinates (0,0), or startCard is not yet
     * initialized.
     */
    public void playStarter(Side side) throws InvalidPlayCardException, InvalidChoiceException {
        // check if the starter card has been already set
        if(this.startCard == null)
            throw new InvalidPlayCardException("Starter card has not been drawn yet");
        
        // get the proper side of the starter card
        startCard.placeCardInField(side);
        CardSidePlayable starterSide;
        if(side.equals(Side.SIDEBACK))
            starterSide = this.startCard.getSide(Side.SIDEBACK);
        else {
            if(side.equals(Side.SIDEFRONT))
                starterSide = this.startCard.getSide(Side.SIDEFRONT);
            else
                throw new InvalidChoiceException();
        }

        // play the starter card on the field at coords (0,0)
        try {
            this.field.playCardSide(starterSide, Coordinates.origin());
        } catch (RequirementsNotMetException ignored) {} // No requirements for starter card.

    }

    /**
     * Play a card side of the player's hand on the field at given coordinates. If the card can't be played an exception
     * is thrown.
     * @param sideToPlay Card side to be played on the field.
     * @param coordToPlay Coordinates where the card side has to be played on the field.
     * @throws InvalidPlayCardException Positioning error of the card at given coordinates, or startCard is not yet
     * initialized.
     * @throws RequirementsNotMetException At least one Requirement is not satisfied for the given card.
     */
    public void playCard(CardSidePlayable sideToPlay, Coordinates coordToPlay) throws InvalidPlayCardException, RequirementsNotMetException {
        // check that the player has the side's card on his hand
        CardPlayable cardToPlay = null;
        for(CardPlayable currCard: this.handCards) {
            if(currCard.getSide(Side.SIDEFRONT).equals(sideToPlay) || currCard.getSide(Side.SIDEBACK).equals(sideToPlay)) {
                cardToPlay = currCard;
                if(currCard.getSide(Side.SIDEFRONT).equals(sideToPlay))
                    cardToPlay.placeCardInField(Side.SIDEFRONT);
                else
                    cardToPlay.placeCardInField(Side.SIDEBACK);
                break;
            }
        }

        if(cardToPlay == null) throw new InvalidPlayCardException("card not in the player's hand");
        // play the card on the field (exceptions will be thrown if action is not valid)
        this.field.playCardSide(sideToPlay, coordToPlay);

        // remove the card from the player's hand
        this.handCards.remove(cardToPlay);

        // add points to player for card positioning
        this.addPoints(sideToPlay.calcPoints(this.field));
    }


    /**
     * Add a card to player's hand. An exception is thrown if the hand is full.
     * @param card Card to be added to the hand.
     * @throws PlayerHandException Exception thrown when the hand is already full (it should never happen!)
     */
    public void addCardToHand(CardPlayable card) throws PlayerHandException {
        //Check if there is space for a new card in the hand
        if(this.handCards.size() > 2) throw new PlayerHandException("player's hand is full");

        this.handCards.add(card);
    }

    /**
     * Increment player's points counter by the given amount.
     * @param increment Number of points to add.
     */
    public void addPoints(int increment) {
        this.points += increment;
    }

    /**
     * Calculate and add points for the personal and common objectives.
     * @param common1 First common objective.
     * @param common2 Second common objective.
     */
    public void addObjectivePoints(CardObjective common1, CardObjective common2) {
        // Add points of personal objective
        this.addPoints(this.personalObjective.calcPoints(this.field));

        // Add points of common objectives
        this.addPoints(common1.calcPoints(this.field));
        this.addPoints(common2.calcPoints(this.field));
    }

    // GETTERS

    /**
     * @return Player decontextualized from game. It stores nickname and token chosen by the player
     */
    public PlayerLobby getPlayerLobby() {
        return playerLobby;
    }

    /**
     * @return Starter card of the player. Null if player has not been assigned a starter card yet
     */
    public CardStarter getStarter() {
        return startCard;
    }

    /**
     * @return Player's points counter.
     */
    public int getPoints() {
        return points;
    }

    /**
     * @return Player's hand of cards, which could be Gold cards or Resource cards.
     */
    public List<CardPlayable> getHandCards() {
        return new ArrayList<>(handCards);
    }

    /**
     * @return Player's personal objective. It affects only player's points when the game ends.
     * Null if the personal objective hasn't been initialized yet
     */
    public CardObjective getPersonalObjective() {
        return personalObjective;
    }

    /**
     * @return the two objective cards among which the player can choose his personal objective.
     * Null if they have not been set yet
     */
    public List<CardObjective> getPossiblePersonalObjectives() {
        return new ArrayList<>(possiblePersonalObjectives);
    }

    /**
     * @return The list of coordinates in which new cards can be played.
     * If game has not started yet, the list is empty
     */
    public List<Coordinates> getAvailableCoord(){
        return field.getAvailableCoords();
    }

    /**
     * @return Player's field
     */
    public FieldIF getField() {
        return field;
    }

}
