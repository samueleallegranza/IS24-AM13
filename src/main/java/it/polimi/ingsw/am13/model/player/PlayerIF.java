package it.polimi.ingsw.am13.model.player;

import it.polimi.ingsw.am13.model.card.*;

import java.util.List;

public interface PlayerIF {

    /**
     * @return Player decontextualized from game. It stores nickname and token chosen by the player
     */
    public PlayerLobby getPlayerLobby();

    /**
     * @return Starter card of the player. Null if player has not been assigned a starter card yet
     */
    public CardStarterIF getStarter();

    /**
     * @return Player's points counter. Initially it will return 0
     */
    public int getPoints();

    /**
     * @return Player's hand of cards, which could be {@link CardGold} or {@link CardResource}.
     * It is empty if the player has no cards yet
     */
    public List<? extends CardPlayableIF> getHandCards();

    /**
     * @return Player's personal objective. It affects only player's points when the game ends.
     * Null if the personal objective hasn't been initialized yet
     */
    public CardObjectiveIF getPersonalObjective();

    /**
     * @return the two objective cards among which the player can choose his personal objective.
     * Null if they have not been set yet
     */
    public List<CardObjectiveIF> getPossiblePersonalObjectives();

    /**
     * @return Player's field. Always non-null (it could be an empty field if the player has not played yet)
     */
    public FieldIF getField();

}
