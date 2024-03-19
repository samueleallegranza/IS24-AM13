package it.polimi.ingsw.am13.model.player;

import it.polimi.ingsw.am13.model.card.Card;
import it.polimi.ingsw.am13.model.card.CardStarter;
import it.polimi.ingsw.am13.model.card.Coordinates;

import java.util.List;
import java.util.Map;

class Field {

    private CardStarter startCard;
    private Map<Coordinates, Card> field;

    public List<Coordinates> getAvailablePos() {
        return null;
    }

    public void playCard(Card card, Coordinates coord) {
    }

}
