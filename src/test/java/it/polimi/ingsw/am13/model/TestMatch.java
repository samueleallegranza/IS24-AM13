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

import static org.junit.jupiter.api.Assertions.*;

public class TestMatch {
    private Player player0;
    private Player player1;
    private Match match;
    @Test
    public void testGameSetup(){
        List<Player> players;
        player0=new Player("Al",new Token(ColorToken.RED));
        player1=new Player("John",new Token(ColorToken.RED));
        assertThrows(InvalidPlayersNumberException.class, ()->new Match(Arrays.asList(player0, player1)));
        player1=new Player("John",new Token(ColorToken.BLUE));
        players=new ArrayList<>(Arrays.asList(player0,player1));

        try{
            match=new Match(players);
        } catch (InvalidPlayersNumberException e){
            System.out.println("Invalid number of players");
        }
//        assertEquals(match.getGameStatus(),GameStatus.INIT);
        assertNull(match.getGameStatus());
        try{
            match.startGame();
        } catch (GameStatusException e){
            throw new RuntimeException(e);
        }
        assertEquals(match.getGameStatus(),GameStatus.INIT);
        for(Player player : players){
            //CardStarter cardStarter=match.fetchStarter(player);
            try {
                match.playStarter(player, Side.SIDEFRONT);
            } catch (GameStatusException | InvalidPlayerException e){
                throw new RuntimeException(e);
            }
            List<CardObjective> cardObjectives;
            try {
                cardObjectives=match.fetchPersonalObjectives(player);
            } catch (InvalidPlayerException e){
                throw new RuntimeException(e);
            }
            try{
                match.choosePersonalObjective(player,cardObjectives.getFirst());
            } catch (GameStatusException | InvalidPlayerException | InvalidChoiceException |
                     VariableAlreadySetException e){
                throw new RuntimeException(e);
            }
            CardObjective objectiveInHand;
            try{
                objectiveInHand=match.fetchHandObjective(player);
            } catch (InvalidPlayerException e){
                throw new RuntimeException(e);
            }
            assertEquals(objectiveInHand,cardObjectives.getFirst());
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
            match.playCard(currentPlayer,handCards.getFirst(),Side.SIDEFRONT,coordinates);
        } catch (GameStatusException | InvalidPlayerException e) {
            throw new RuntimeException(e);
        }
        assertEquals(currentPlayer.getHandCards().size(),2);
        List<CardPlayable> pickableCards=match.fetchPickables();
        try {
            match.pickCard(pickableCards.getFirst());
        } catch (GameStatusException | InvalidDrawCardException e) {
            throw new RuntimeException(e);
        }
        assertEquals(currentPlayer.getHandCards().size(),3);
        assertTrue(currentPlayer.getHandCards().contains(pickableCards.getFirst()));
    }

    @Test
    public void testSecondTurn() throws RequirementsNotMetException{
        testPickAndPlay();
        try {
            match.nextTurn();
        } catch (GameStatusException e) {
            throw new RuntimeException(e);
        }
        Player currentPlayer=match.getCurrentPlayer();
        assertEquals(currentPlayer,player1);
    }

    @Test
    public void testCompleteGame() throws RequirementsNotMetException{
        testGameSetup();
        boolean hasNextTurn;    // Set to true by default
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
            for(Resource resource : handCards.getFirst().getBack().getRequirements().keySet())
                System.out.println(resource);
            for(Resource resource : handCards.getFirst().getFront().getRequirements().keySet())
                System.out.println(resource);
             */
            try {
                if(handCards.getFirst().getFront().getCorners().get(1).isPlaceable()){
                    try {
                        match.playCard(currentPlayer, handCards.getFirst(), Side.SIDEFRONT, coordinates);
                    } catch (RequirementsNotMetException e){
                        match.playCard(currentPlayer, handCards.getFirst(), Side.SIDEBACK, coordinates);
                    }
                }
                else {
                    match.playCard(currentPlayer, handCards.getFirst(), Side.SIDEBACK, coordinates);
                }
            } catch (GameStatusException | InvalidPlayerException e) {
                throw new RuntimeException(e);
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
                throw new RuntimeException(e);
            }
            //System.out.println(currentPlayer.getPoints());
            assertEquals(currentPlayer.getHandCards().size(),3);
            if(currentPlayer==player1)
                i++;
            try {
                hasNextTurn=match.nextTurn();
            } catch (GameStatusException e) {
                throw new RuntimeException(e);
            }
        } while (hasNextTurn);
        try {
            match.addObjectivePoints();
        } catch(GameStatusException e){
            throw new RuntimeException(e);
        }
        try{
            System.out.println(match.calcWinner().getNickname());
//            assertEquals(match.calcWinner(), player0);
        } catch(GameStatusException e){
            throw new RuntimeException(e);
        }
    }

}
