package it.polimi.ingsw.am13.model.player;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;

import java.util.ArrayList;
import java.util.List;

public class Player {
    /**
     * Player's nickname.
     */
    private final String nickname;

    /**
     * Player's token color.
     */
    private final Token token;

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
     * The two objective cards among which the player can choose his personal objective
     */
    private final List<CardObjective> possiblePersonalObjectives;
    /**
     * Player's starter card which has been drawn to him. It can't change once it has been set.
     */
    private CardStarter startCard;

    /**
     * Player's points counter.
     */
    private int points;

    /**
     * Constructor for the Player class. It initializes nickname, token, points and starter card (it has to be drawn
     * before creating a Player object). It also initializes an empty field for the player and the space to store the
     * player's hand.
     * @param nick Nickname chosen by the player.
     * @param token Token color chosen for the player.
     */
    public Player(String nick, Token token) {
        this.startCard = null;
        this.nickname = nick;
        this.token = token;
        this.points = 0;
        field = new Field();
        handCards = new ArrayList<>();
        possiblePersonalObjectives = new ArrayList<>();
        personalObjective=null;
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
     *
     * @return the two objective cards among which the player can choose his personal objective
     */
    public List<CardObjective> getPossiblePersonalObjectives() {
        return possiblePersonalObjectives;
    }

    /**
     * Sets the player's personal objective. Once set, it can't be modified!
     * @param cardObj Objective card which will be set as personal objective.
     * @throws VariableAlreadySetException Exception is thrown if this method has been called before.
     * @throws InvalidChoiceException if the objective card does not belong to the list of the possible objective cards
     */
    public void initObjective(CardObjective cardObj) throws VariableAlreadySetException, InvalidChoiceException{
        if(this.personalObjective != null)
            throw new VariableAlreadySetException();
        if(!possiblePersonalObjectives.contains(cardObj))
            throw new InvalidChoiceException();
        this.personalObjective = cardObj;

    }

    public CardStarter getStarter() {
        return startCard;
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
        CardSidePlayable starterSide;
        if(side.equals(Side.SIDEBACK))
            starterSide = this.startCard.getBack();
        else {
            if(side.equals(Side.SIDEFRONT))
                starterSide = this.startCard.getFront();
            else
                throw new InvalidChoiceException();
        }
        // coordinates for the starter card
        Coordinates starterCoord = null;
        try {
            starterCoord = new Coordinates(0,0);
        } catch (InvalidCoordinatesException ignored) {}

        // play the starter card on the field at coords (0,0)
        try {
            this.field.playCardSide(starterSide, starterCoord);
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
            if(currCard.getFront().equals(sideToPlay) || currCard.getBack().equals(sideToPlay)) {
                cardToPlay = currCard;
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

    public String getNickname() {
        return nickname;
    }

    public Token getToken() {
        return token;
    }

    public int getPoints() {
        return points;
    }

    public List<CardPlayable> getHandCards() {
        return handCards;
    }

    public CardObjective getPersonalObjective() {
        return personalObjective;
    }

    /**
     *
     * @return the List of coordinates in which new cards can be played
     */
    public List<Coordinates> getAvailableCoord(){
        return field.getAvailableCoord();
    }
}
