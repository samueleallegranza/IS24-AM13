package it.polimi.ingsw.am13.client.gamestate;

import it.polimi.ingsw.am13.model.card.CardSidePlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO pensa se far implements FieldIF (c'è getResourcesInField() che dà un pò fastidio...)
public class FieldClient {

    private Map<Coordinates, CardSidePlayableIF> field;

    private List<Coordinates> availableCoords;

    public List<Coordinates> getPlacedCoords() {
        return new ArrayList<>(field.keySet());
    }

    public CardSidePlayableIF getCardSideAtCoord(Coordinates coord) {
        return field.get(coord);
    }

    public void setCardSideAtCoord(CardSidePlayableIF cardSide, Coordinates coord) {
        field.put(coord, cardSide);
    }

    public List<Coordinates> getAvailableCoords() {
        return availableCoords;
    }

    public void setAvailableCoords(List<Coordinates> availableCoords) {
        this.availableCoords = availableCoords;
    }
}
