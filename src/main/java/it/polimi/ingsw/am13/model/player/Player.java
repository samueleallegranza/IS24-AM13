package it.polimi.ingsw.am13.model.player;

import it.polimi.ingsw.am13.model.card.*;

import java.util.ArrayList;

class Player {

    private final String nickname;
    private final Token token;
    private Field field;
    private final ArrayList<CardPlayable> handCards;
    private CardObjective personalObjective;
    private int points;

    public Player(String nick, Token token) {
        this.nickname = nick;
        this.token = token;
        handCards = new ArrayList<>();
    }

    // TODO: Complete checks before call or add logic for it
    public boolean chooseStarter(CardSidePlayable startCard) {
//        // Create player field with the chosen starter card as root
//        field = new Field(startCard);
        return true;
    }

    // TODO: Complete checks before call or add logic for it
    public boolean chooseObjective(CardObjective objCard) {
//        // Set the passed card as personal objective
//        this.personalObjective = objCard;
        return true;
    }

    public boolean playCard(CardSidePlayable playedCardSide, Coordinates playedCoord) {
//        // Check if the given card is in the player's hand. Return false if it is not.
//        boolean isOnHand = false;
//        int handCardIdx = 0;
//        for(Card card : this.handCards) {
//            handCardIdx++;
//            for(CardSide side : card.getSides()) {
//                // TODO: Check if it is comparing correctly between
//                if(side == playedCardSide){
//                    isOnHand = true;
//                    break;
//                }
//            }
//            if(isOnHand) break;
//        }
//        if(!isOnHand) return false;
//
//        // Try to place the card on the field, return if position or card is invalid.
//        boolean retval = this.field.playCardSide(playedCardSide, playedCoord);
//        if(retval == false) return false;
//
//        // Remove card from hand
//        this.handCards.remove(handCardIdx);
//
        return true;
    }

    public boolean addCardToHand(CardPlayable card) {
//        //Check if there is space for a new card in the hand
//        if(this.handCards.size() > 2) return false;
//
//        this.handCards.add(card);
        return true;
    }

    public void updatePoints(int increment) {
        this.points += increment;
    }

    public void addObjectivePoints() {
        // TODO: To be implemented yet. Waiting for access to the objective's calcPoints()
    }

}
