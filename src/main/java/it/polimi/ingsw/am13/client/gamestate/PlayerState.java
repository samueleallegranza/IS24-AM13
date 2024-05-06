package it.polimi.ingsw.am13.client.gamestate;

import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.CardStarterIF;
import it.polimi.ingsw.am13.model.player.PlayerIF;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of the state of one player in game.
 * It stores information about their starter card, the possible hand objectives and the chosen one, the cards in hand,
 * the points, the field (with the placed cards) and whether they are connected.
 * All the information can be set in any time, with no limitation or checks.
 */
public class PlayerState {

    /**
     * Starter card of the player. Null if it has not been set yet.
     */
    private CardStarterIF starterCard;

    /**
     * List of (2) possible objective card, from with the player must choose for their personal objective card to hold for the game.
     * Null if they have not been set yet.
     */
    private List<CardObjectiveIF> possibleHandObjectives;

    /**
     * Personal objective card of the player. Null if it has not been chosen yet.
     */
    private CardObjectiveIF handObjective;

    /**
     * Flag indicating if the player is currently connecting or not. It is set to true by default
     */
    private boolean isConnected;

    /**
     * List of (3 or 2) hand card of the player. Empty if the player has no card
     */
    private final List<CardPlayableIF> handPlayable;

    /**
     * Points of the players, initially 0
     */
    private int points;

    /**
     * Field of the player. It could be an empty field if the player has not played yet
     */
    private final FieldState field;

    /**
     * Builds a new representation of the player's state starting from their interface.
     * @param player Interface of the player whose state is to be built
     */
    public PlayerState(PlayerIF player) {
        this.starterCard = player.getStarter();
        this.possibleHandObjectives = new ArrayList<>(player.getPossiblePersonalObjectives());
        this.handObjective = player.getPersonalObjective();
        this.isConnected = true;
        this.handPlayable = new ArrayList<>(player.getHandCards());
        this.points = player.getPoints();
        this.field = new FieldState(player.getField());
    }

    /**
     * @return Starter card of the player. Null if it has not been set yet.
     */
    public CardStarterIF getStarterCard() {
        return starterCard;
    }

    /**
     * Sets the player's starter card.
     * Following game's rules, this method should be used only once if the starter card is not already set
     * @param starterCard Starter card to set
     */
    void setStarterCard(CardStarterIF starterCard) {
        this.starterCard = starterCard;
    }

    /**
     * @return List of (2) possible objective card, from with the player must choose for their personal objective card to hold for the game.
     * Null if they have not been set yet.
     */
    public List<CardObjectiveIF> getPossibleHandObjectives() {
        return possibleHandObjectives;
    }

    /**
     * Sets the player's (2) possible objective cards.
     * Following game's rules, this method should be used only once if the possible objective cards are not already set.
     * Note that it is not created another list
     * @param possibleHandObjectives List of possible objective cards to set
     */
    void setPossibleHandObjectives(List<CardObjectiveIF> possibleHandObjectives) {
        this.possibleHandObjectives = possibleHandObjectives;
    }

    /**
     * @return Personal objective card of the player. Null if it has not been chosen yet.
     */
    public CardObjectiveIF getHandObjective() {
        return handObjective;
    }

    /**
     * Sets the player's chosen personal objective card.
     * Following game's rules, this method should be used only once if the personal objective card is not already set
     * @param handObjective Personal objective card to set
     */
    void setHandObjective(CardObjectiveIF handObjective) {
        this.handObjective = handObjective;
    }

    /**
     * @return List of (3 or 2) hand card of the player. Empty if the player has no card
     */
    public List<CardPlayableIF> getHandPlayable() {
        return handPlayable;
    }

    /**
     * Removes the given card from the player's hand. If the given card is not in player's hand, nothing changes.
     * @param card Card to remove from player's hand
     */
    void removeCardPlayed(CardPlayableIF card) {
        handPlayable.remove(card);
    }

    /**
     * Adds the given card to the player's hand
     * @param card Card to add to player's hand
     */
    void addCardPicked(CardPlayableIF card) {
        handPlayable.add(card);
    }

    /**
     * @return Points of the players, initially 0
     */
    public int getPoints() {
        return points;
    }

    /**
     * Sets the player's current points.
     * @param points Points to set
     */
    void setPoints(int points) {
        this.points = points;
    }

    /**
     * @return Flag indicating if the player is currently connecting or not. It is set to true by default
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Sets the flag for connection of player
     * @param connected Flag for connection to set
     */
    void setConnected(boolean connected) {
        isConnected = connected;
    }

    /**
     * @return Field of the player. It could be an empty field if the player has not played yet
     */
    public FieldState getField() {
        return field;
    }

}
