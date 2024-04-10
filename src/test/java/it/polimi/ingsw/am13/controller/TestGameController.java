package it.polimi.ingsw.am13.controller;


import it.polimi.ingsw.am13.model.exceptions.GameStatusException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayersNumberException;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class TestGameController {
    GameController gameController;
    @Test
    public void testConstruction(){
        ArrayList<PlayerLobby> players=new ArrayList<>(Arrays.asList(new PlayerLobby("Al", ColorToken.RED),new PlayerLobby("John",ColorToken.BLUE)));
        gameController=null;
        try{
            gameController=new GameController(0,players);
        } catch (InvalidPlayersNumberException|GameStatusException e){
            throw new RuntimeException();
        }
    }

    @Test
    public void testPickCard(){
        testConstruction();
    }
}
