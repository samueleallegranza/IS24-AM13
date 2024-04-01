package it.polimi.ingsw.am13.model;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.Player;
import it.polimi.ingsw.am13.model.player.Token;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestMatch {
    private Player player0;
    private Player player1;

    private Match match;
    @Test
    public void testGameSetup(){
        List<Player> players;
        player0=new Player("Al",new Token(ColorToken.RED));
        player1=new Player("John",new Token(ColorToken.BLACK));
        players=new ArrayList<>(Arrays.asList(player0,player1));
        match=null;
        try{
            match=new Match(players);
        } catch (InvalidPlayersNumberException e){
            System.out.println("Invalid number of players");
        }
        assertEquals(match.getGameStatus(),GameStatus.INIT);
        try{
        match.startGame();
        } catch (GameStatusException e){
            System.out.println(e);
        }
        for(Player player : players){
            //CardStarter cardStarter=match.fetchStarter(player);
            try {
                match.playStarter(player, Side.SIDEFRONT);
            } catch (GameStatusException | InvalidPlayerException e){
                System.out.println(e);
            }
            List<CardObjective> cardObjectives=null;
            try {
                cardObjectives=match.fetchPersonalObjectives(player);
            } catch (InvalidPlayerException e){
                System.out.println(e);
            }
            try{
                match.choosePersonalObjective(player,cardObjectives.get(0));
            } catch (GameStatusException | InvalidPlayerException e){
                System.out.println(e);
            }
            CardObjective objectiveInHand=null;
            try{
                objectiveInHand=match.fetchHandObjective(player);
            } catch (InvalidPlayerException e){
                System.out.println(e);
            }
            assertEquals(objectiveInHand,cardObjectives.get(0));
        }
        assertEquals(match.getGameStatus(),GameStatus.IN_GAME);

    }

    @Test
    public void testPickAndPlay() throws RequirementsNotMetException{
        testGameSetup();
        Player currentPlayer=match.getCurrentPlayer();
        assertEquals(currentPlayer,player0);
        List<CardPlayable> handCards=currentPlayer.getHandCards();
        Coordinates coordinates=null;
        try{
            coordinates=new Coordinates(1,1);
        } catch (InvalidCoordinatesException e){
            System.out.println("Invalid Coordinates");
        }
        try {
            match.playCard(currentPlayer,handCards.get(0),Side.SIDEFRONT,coordinates);
        } catch (GameStatusException | InvalidPlayerException e) {
            System.out.println(e);
        }
        assertEquals(currentPlayer.getHandCards().size(),2);
        List<CardPlayable> pickableCards=match.fetchPickables();
        try {
            match.pickCard(pickableCards.get(0));
        } catch (GameStatusException | InvalidDrawCardException e) {
            System.out.println(e);
        }
        assertEquals(currentPlayer.getHandCards().size(),3);
        assertTrue(currentPlayer.getHandCards().contains(pickableCards.get(0)));
    }

    @Test
    public void testSecondTurn() throws RequirementsNotMetException{
        testPickAndPlay();
        try {
            match.nextTurn();
        } catch (GameStatusException e) {
            System.out.println(e);
        }
        Player currentPlayer=match.getCurrentPlayer();
        assertEquals(currentPlayer,player1);
    }

    @Test
    public void testCompleteGame() throws RequirementsNotMetException{
        testGameSetup();
        boolean hasNextTurn=true;
        int i=1;
        do{
            Player currentPlayer=match.getCurrentPlayer();
            List<CardPlayable> handCards=currentPlayer.getHandCards();
            Coordinates coordinates=null;
            try{
                coordinates=new Coordinates(i,i);
            } catch (InvalidCoordinatesException e){
                System.out.println("Invalid Coordinates");
            }

            /*System.out.println(i);
            for(Resource resource : handCards.get(0).getBack().getRequirements().keySet())
                System.out.println(resource);
            for(Resource resource : handCards.get(0).getFront().getRequirements().keySet())
                System.out.println(resource);
             */
            try {
                if(handCards.get(0).getFront().getCorners().get(1).isPlaceable()){
                    try {
                        match.playCard(currentPlayer, handCards.get(0), Side.SIDEFRONT, coordinates);
                    } catch (RequirementsNotMetException e){
                        match.playCard(currentPlayer, handCards.get(0), Side.SIDEBACK, coordinates);
                    }
                }
                else {
                    match.playCard(currentPlayer, handCards.get(0), Side.SIDEBACK, coordinates);
                }
            } catch (GameStatusException | InvalidPlayerException e) {
                System.out.println(e);
            }
            assertEquals(currentPlayer.getHandCards().size(),2);
            List<CardPlayable> pickableCards=match.fetchPickables();
            try {
                for (int j = 1; j < pickableCards.size(); j++) {

                    if(pickableCards.get(j)!=null) {
                        //System.out.println(pickableCards.get(j));
                        match.pickCard(pickableCards.get(j));
                        break;
                    }
                }
            } catch (GameStatusException | InvalidDrawCardException e) {
                System.out.println(e);
            }
            //System.out.println(currentPlayer.getPoints());
            assertEquals(currentPlayer.getHandCards().size(),3);
            if(currentPlayer==player1)
                i++;
            try {
                hasNextTurn=match.nextTurn();
            } catch (GameStatusException e) {
                System.out.println(e);
            }
        } while (hasNextTurn);
        try {
            match.addObjectivePoints();
        } catch(GameStatusException e){
            System.out.println(e);
        }
        try{
            System.out.println(match.calcWinner().getNickname());
        } catch(GameStatusException e){
            System.out.println(e);
        }
    }
}
