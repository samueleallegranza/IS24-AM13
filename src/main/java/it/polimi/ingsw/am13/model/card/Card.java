package it.polimi.ingsw.am13.model.card;

public abstract class Card {
    private final String id;
    private Coordinates coord;
    private boolean isPlayed;
    private Side playedSide;

    //Costruttore
    Card(String id) {
        this.id = id;
    }

    public Side getPlayedSide() {
        return playedSide;
    }
}
