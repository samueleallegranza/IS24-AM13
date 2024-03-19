package it.polimi.ingsw.am13.model.card;

import java.util.Arrays;
import java.util.List;

public abstract class Card {
    private final String id;
    private final List<CardSide> sides;
    private Coordinates coord;
    private boolean isPlayed;
    private Side playedSide;

    //Costruttore
    Card(String id, CardSide front, CardSide back) {
        this.id = id;
        sides = Arrays.asList(front, back);
    }
}
