package it.polimi.ingsw.am13.controller;


import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import org.junit.jupiter.api.Test;

import java.util.*;

public class TestGameController {
    GameController gameController;
    List<CardPlayable> playableCards;
    //List<Player> players;

    @Test
    public void testConstruction(){
        ArrayList<PlayerLobby> players=new ArrayList<>(Arrays.asList(new PlayerLobby("Al", ColorToken.RED),new PlayerLobby("John",ColorToken.BLUE)));
        gameController=null;
        // TODO: da ricontrollare in seguito ai cambiamenti
//        try{
//            gameController=new GameController(0,new ListenerHandler((Arrays.asList(new GameListenerForTesting(players.get(0)),new GameListenerForTesting(players.get(1))))));
//        } catch (InvalidPlayersNumberException e){
//            throw new RuntimeException();
//        }
    }

    @Test
    void testUpdatePing() {
    }

    @Test
    public void testDisconnectPlayer() {

    }



    @Test
    public void testPlayCard() {

    }

    @Test
    public void testPickCard(){
        testConstruction();
    }
}
