package it.polimi.ingsw.am13.client.gamestate;

import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.CardStarterIF;

import java.util.List;

public class PlayerClient {

    private final CardStarterIF starterCard;

    private final List<CardObjectiveIF> possibleHandObjectives;

    private CardObjectiveIF handObjective;

    private List<CardPlayableIF> handPlayable;

    private int points;

    private boolean isConnected;

    private final FieldClient field;


    public PlayerClient(CardStarterIF starterCard, List<CardObjectiveIF> possibleHandObjectives) {
        this.starterCard = starterCard;
        this.possibleHandObjectives = possibleHandObjectives;
        field = new FieldClient();
    }

    public CardStarterIF getStarterCard() {
        return starterCard;
    }

    public List<CardObjectiveIF> getPossibleHandObjectives() {
        return possibleHandObjectives;
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

    public void setHandPlayable(List<CardPlayableIF> handPlayable) {
        this.handPlayable = handPlayable;
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
