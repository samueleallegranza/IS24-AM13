package it.polimi.ingsw.am13.client.gamestate;

import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.CardSidePlayableIF;
import it.polimi.ingsw.am13.model.card.CardStarterIF;
import it.polimi.ingsw.am13.model.player.FieldIF;
import it.polimi.ingsw.am13.model.player.PlayerIF;

import java.util.ArrayList;
import java.util.List;

public class PlayerClient {

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
    private final FieldClient field;

    public PlayerClient(PlayerIF player) {
        this.starterCard = player.getStarter();
        this.possibleHandObjectives = player.getPossiblePersonalObjectives();
        this.handObjective = player.getPersonalObjective();
        this.isConnected = true;
        this.handPlayable = new ArrayList<>(player.getHandCards());
        this.points = player.getPoints();
        this.field = new FieldClient(player.getField());
    }

//    /**
//     * Player created with no parameter set.
//     * The player should be constructed with this method if the game has not started, that is it exists
//     * but the {@link it.polimi.ingsw.am13.model.GameStatus} is still null
//     */
//    public PlayerClient() {
//        this.starterCard = null;
//        this.possibleHandObjectives = null;
//        this.handObjective = null;
//        this.isConnected = true;
//        this.handPlayable = new ArrayList<>();
//        this.points = 0;
//        this.field = new FieldClient();
//    }
//
//    /**
//     * Player created with their starter card, the possible personal objective card from witch to choose and their first card in hand
//     * The player should be constructed with this method if the game has just started, that is the
//     * {@link it.polimi.ingsw.am13.model.GameStatus} is INIT
//     * @param starterCard Starter card given to the player
//     * @param possibleHandObjectives The (2) objective cards given to the player, from which they will choose their personal
//     *                               objective card to hold for the rest of the game
//     * @param handPlayable The first (3) card the player receive
//     */
//    public PlayerClient(CardStarterIF starterCard, List<CardObjectiveIF> possibleHandObjectives, List<CardPlayableIF> handPlayable) {
//        this.starterCard = starterCard;
//        this.possibleHandObjectives = possibleHandObjectives;
//        this.handObjective = null;
//        this.isConnected = true;
//        this.handPlayable = handPlayable;
//        this.points = 0;
//        this.field = new FieldClient();
//    }
//
//    /**
//     * Player created in a certain point of the started game
//     * The player should be constructed with this method if the game has actually started, that is the
//     * {@link it.polimi.ingsw.am13.model.GameStatus} is beyond the INIT phase (from IN_GAME on)
//     * {@link it.polimi.ingsw.am13.model.GameStatus} is INIT
//     * @param starterCard Starter card given to the player
//     * @param handObjective Personal objective card chosen by the player
//     * @param handPlayable The current hand cards of the player
//     * @param points The current amount of points of the player
//     * @param field The current field (interface) of the player
//     */
//    public PlayerClient(CardStarterIF starterCard, CardObjectiveIF handObjective, List<CardPlayableIF> handPlayable, int points, FieldIF field) {
//        this.starterCard = starterCard;
//        this.possibleHandObjectives = null;
//        this.handObjective = handObjective;
//        this.isConnected = true;
//        this.handPlayable = handPlayable;
//        this.points = points;
//        this.field = new FieldClient(field);
//    }

    public CardStarterIF getStarterCard() {
        return starterCard;
    }

    public void setStarterCard(CardStarterIF starterCard) {
        this.starterCard = starterCard;
    }

    public List<CardObjectiveIF> getPossibleHandObjectives() {
        return possibleHandObjectives;
    }

    public void setPossibleHandObjectives(List<CardObjectiveIF> possibleHandObjectives) {
        this.possibleHandObjectives = possibleHandObjectives;
    }

    public CardObjectiveIF getHandObjective() {
        return handObjective;
    }

    public void setHandObjective(CardObjectiveIF handObjective) {
        this.handObjective = handObjective;
    }

    public List<CardPlayableIF> getHandPlayable() {
        return handPlayable;
    }

    public void removeCardPlayed(CardPlayableIF card) {
        handPlayable.remove(card);
    }

    public void addCardPicked(CardPlayableIF card) {
        handPlayable.add(card);
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public FieldClient getField() {
        return field;
    }

}
