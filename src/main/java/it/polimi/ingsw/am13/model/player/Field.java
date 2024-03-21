package it.polimi.ingsw.am13.model.player;

import it.polimi.ingsw.am13.model.card.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Field {

    //private CardStarter startCard; //I'm pretty sure this is useless, you can just get the card at (0,0)
    private Map<Coordinates, CardSidePlayable> field;

    public Field() {
        field=new HashMap<>();
    }

    public CardSidePlayable getCardAt(Coordinates coordinates){
        return field.get(coordinates);
    }
    public List<Coordinates> getAvailablePos() {
        return null;
    }

    public void playCardSide(CardSidePlayable card, Coordinates coord) {
        field.put(coord,card);
    }

    //public void addCardSide(CardSidePlayable card, Coordinates coord) I believe that's what the above method does

}
