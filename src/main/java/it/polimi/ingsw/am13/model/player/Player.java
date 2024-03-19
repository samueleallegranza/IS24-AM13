package it.polimi.ingsw.am13.model.player;

import it.polimi.ingsw.am13.model.card.CardObjective;
import it.polimi.ingsw.am13.model.card.CardPlayable;

import java.util.ArrayList;
import java.util.List;

class Player {

    private final String nick;
    private final Token token;
    private final Field field;
    private final List<CardPlayable> handCards;
    private CardObjective personalObjective;
    private int points;

    public Player(String nick, Token token) {
        this.nick = nick;
        this.token = token;
        field = new Field(); //impostare giusto costruttore
        handCards = new ArrayList<>();
    }

}
