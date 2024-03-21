package it.polimi.ingsw.am13.model.player;

import it.polimi.ingsw.am13.model.card.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Field {

    private CardStarter startCard;
    private Map<Coordinates, CardSidePlayable> field;

    // TODO: usato x fare testing
    public void setField(Map<Coordinates, CardSidePlayable> field) {
        this.field = field;
    }

    public List<Coordinates> getAvailablePos() {
        return null;
    }

    public void playCard(Card card, Coordinates coord) {
    }

    public Map<Coordinates, CardSidePlayable> getField() {
        return field;
    }

    public Map<Resource, Integer> countResources() {
        Map<Resource, Integer> freqs = new HashMap<>();
        for(CardSidePlayable card : field.values()) {
            for(Resource r : card.getCenterResources()) {
                if (freqs.keySet().contains(r))
                    freqs.put(r, freqs.get(r) + 1);
                else
                    freqs.put(r, 1);
            }
            for(Corner c : card.getCorners()) {
                Resource r = c.getResource();
                if (freqs.keySet().contains(r))
                    freqs.put(r, freqs.get(r) + 1);
                else
                    freqs.put(r, 1);
            }
        }
        return freqs;
    }
}
