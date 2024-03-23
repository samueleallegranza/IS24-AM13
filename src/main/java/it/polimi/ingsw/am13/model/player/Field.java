package it.polimi.ingsw.am13.model.player;

import it.polimi.ingsw.am13.model.card.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Field {

    private final CardStarter startCard; //maybe useless
    private final Map<Coordinates, CardSidePlayable> field;

    public Field(CardStarter starterCard) {
        this.startCard = starterCard;
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

    public Map<Coordinates, CardSidePlayable> getField() {
        return field;
    }

    public Map<Resource, Integer> countResources() {
        Map<Resource, Integer> freqs = new HashMap<>();
        for(CardSidePlayable card : field.values()) {
            for (Resource r : card.getCenterResources()) {
                if (freqs.containsKey(r))
                    freqs.put(r, freqs.get(r) + 1);
                else
                    freqs.put(r, 1);
            }
            for (Corner c : card.getCorners()) {
                if(c.isCovered())        // There can be a resource only if the corner if not covered (there could be a NO_RESOURCE)
                    continue;
                Resource r = c.getResource();
                if (freqs.containsKey(r))
                    freqs.put(r, freqs.get(r) + 1);
                else
                    freqs.put(r, 1);
            }
        }
        return freqs;
    }
}
