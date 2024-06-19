package it.polimi.ingsw.am13.model;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.Player;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.model.player.Token;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestMatch {
    private Player player0;
    private Player player1;
    private Player player2;
    private Match match;
    private PlayerLobby disPlayer;
    @Test
    public void testGameSetup() throws InvalidPlayCardException {
        List<Player> players;
        player0=new Player("Al",new Token(ColorToken.RED));
        player1=new Player("John",new Token(ColorToken.RED));
        assertThrows(InvalidPlayersNumberException.class, ()->new Match(Arrays.asList(player0, player1)));
        player1=new Player("John",new Token(ColorToken.BLUE));
        players=new ArrayList<>(Arrays.asList(player0,player1));

        try{
            match=new Match(players);
        } catch (InvalidPlayersNumberException e){
            //System.out.println("Invalid number of players");
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
                match.playStarter(player.getPlayerLobby(), Side.SIDEBACK);
            } catch (GameStatusException | InvalidPlayerException e){
                throw new RuntimeException(e);
            }
            List<CardObjective> cardObjectives;
            try {
                cardObjectives=match.fetchPersonalObjectives(player.getPlayerLobby());
            } catch (InvalidPlayerException e){
                throw new RuntimeException(e);
            }
            try{
                match.choosePersonalObjective(player.getPlayerLobby(),cardObjectives.getFirst());
            } catch (GameStatusException | InvalidPlayerException | InvalidChoiceException |
                     VariableAlreadySetException e){
                throw new RuntimeException(e);
            }
            CardObjective objectiveInHand;
            try{
                objectiveInHand=match.fetchHandObjective(player.getPlayerLobby());
            } catch (InvalidPlayerException e){
                throw new RuntimeException(e);
            }
            assertEquals(objectiveInHand,cardObjectives.getFirst());
        }
        assertEquals(match.getGameStatus(),GameStatus.IN_GAME);

    }

    @Test
    public void testPickAndPlay() throws RequirementsNotMetException, InvalidPlayCardException {
        testGameSetup();
        Player currentPlayer=match.getCurrentPlayer();
        assertEquals(currentPlayer,match.getFirstPlayer());
        List<CardPlayable> handCards=currentPlayer.getHandCards();
        Coordinates coordinates=null;
        try{
            coordinates=new Coordinates(1,1);
        } catch (InvalidCoordinatesException e){
            //System.out.println("Invalid Coordinates");
        }
        try {
            match.playCard(handCards.getFirst(),Side.SIDEFRONT,coordinates);
        } catch (GameStatusException | InvalidPlayCardException e) {
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
    public void testSecondTurn() throws RequirementsNotMetException, InvalidPlayCardException {
        testPickAndPlay();
        try {
            match.nextTurn();
        } catch (GameStatusException e) {
            throw new RuntimeException(e);
        }
        Player currentPlayer=match.getCurrentPlayer();
        assertNotEquals(currentPlayer,match.getFirstPlayer());
    }

    @Test
    public void testCompleteGame() throws RequirementsNotMetException, InvalidPlayCardException {
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
                //System.out.println("Invalid Coordinates");
            }

            /*//System.out.println(i);
            for(Resource resource : handCards.getFirst().getSide(Side.SIDEBACK).getRequirements().keySet())
                //System.out.println(resource);
            for(Resource resource : handCards.getFirst().getSide(Side.SIDEFRONT).getRequirements().keySet())
                //System.out.println(resource);
             */
            try {
                if(handCards.getFirst().getSide(Side.SIDEFRONT).getCorners().get(1).isPlaceable()){
                    try {
                        match.playCard(handCards.getFirst(), Side.SIDEFRONT, coordinates);
                    } catch (RequirementsNotMetException e){
                        match.playCard(handCards.getFirst(), Side.SIDEBACK, coordinates);
                    }
                }
                else {
                    match.playCard(handCards.getFirst(), Side.SIDEBACK, coordinates);
                }
            } catch (GameStatusException e) {
                throw new RuntimeException(e);
            }
            assertEquals(currentPlayer.getHandCards().size(),2);
            List<CardPlayable> pickableCards=match.fetchPickables();
            try {
                for (int j = 1; j < pickableCards.size(); j++) {
                    if(pickableCards.get(j)!=null) {
                        ////System.out.println(pickableCards.get(j));
                        match.pickCard(pickableCards.get(j));
                        break;
                    }
                }
            } catch (GameStatusException | InvalidDrawCardException e) {
                throw new RuntimeException(e);
            }
            ////System.out.println(currentPlayer.getPoints());
            assertEquals(currentPlayer.getHandCards().size(),3);
            if(currentPlayer!=match.getFirstPlayer())
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
            System.out.println(match.calcWinner().get(0).getNickname());
//            assertEquals(match.calcWinner(), player0);
        } catch(GameStatusException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGameSetupWithDisconnection() throws InvalidPlayCardException, InvalidPlayerException {
        List<Player> players;
        player0=new Player("Al",new Token(ColorToken.RED));
        player1=new Player("John",new Token(ColorToken.RED));
        assertThrows(InvalidPlayersNumberException.class, ()->new Match(Arrays.asList(player0, player1)));
        player1=new Player("John",new Token(ColorToken.BLUE));
        player2=new Player("Jack",new Token(ColorToken.GREEN));
        players=new ArrayList<>(Arrays.asList(player0,player1,player2));

        try{
            match=new Match(players);
        } catch (InvalidPlayersNumberException e){
            //System.out.println("Invalid number of players");
        }

        assertNull(match.getGameStatus());
        try{
            match.startGame();
        } catch (GameStatusException e){
            throw new RuntimeException(e);
        }
        assertEquals(match.getGameStatus(),GameStatus.INIT);
        disPlayer = player0.getPlayerLobby();
        if(player0.equals(match.getFirstPlayer()))
            disPlayer = player1.getPlayerLobby();
        try {
            match.disconnectPlayer(disPlayer);
        } catch (ConnectionException | InvalidPlayerException e) {
            throw new RuntimeException(e);
        }
        try {
            assertFalse(match.fetchIsConnected(disPlayer));
        } catch (InvalidPlayerException e) {
            throw new RuntimeException(e);
        }
        assertEquals(match.countConnected(),2);
        for(Player player : players){
            if(match.fetchIsConnected(player.getPlayerLobby())) {
                try {
                    match.playStarter(player.getPlayerLobby(), Side.SIDEFRONT);
                } catch (GameStatusException | InvalidPlayerException e) {
                    throw new RuntimeException(e);
                }
                List<CardObjective> cardObjectives;
                try {
                    cardObjectives = match.fetchPersonalObjectives(player.getPlayerLobby());
                } catch (InvalidPlayerException e) {
                    throw new RuntimeException(e);
                }
                try {
                    match.choosePersonalObjective(player.getPlayerLobby(), cardObjectives.getFirst());
                } catch (GameStatusException | InvalidPlayerException | InvalidChoiceException |
                         VariableAlreadySetException e) {
                    throw new RuntimeException(e);
                }
                CardObjective objectiveInHand;
                try {
                    objectiveInHand = match.fetchHandObjective(player.getPlayerLobby());
                } catch (InvalidPlayerException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(objectiveInHand, cardObjectives.getFirst());
            }
        }
        assertEquals(match.getGameStatus(),GameStatus.IN_GAME);
    }

    @Test
    public void testPickAndPlayWithDisconnection() throws RequirementsNotMetException, InvalidPlayCardException, InvalidPlayerException, ConnectionException, GameStatusException {
        testGameSetupWithDisconnection();
        match.reconnectPlayer(disPlayer);
        Player currentPlayer = match.getCurrentPlayer();
        assertEquals(currentPlayer, match.getFirstPlayer());
        List<CardPlayable> handCards = currentPlayer.getHandCards();
        Coordinates coordinates = null;
        try{
            coordinates = new Coordinates(1,1);
        } catch (InvalidCoordinatesException e){
            //System.out.println("Invalid Coordinates");
        }
        try {
            match.playCard(handCards.getFirst(),Side.SIDEFRONT,coordinates);
        } catch (GameStatusException | InvalidPlayCardException e) {
            throw new RuntimeException(e);
        }
        assertEquals(currentPlayer.getHandCards().size(),2);
        match.disconnectPlayer(match.getFirstPlayer().getPlayerLobby());
        assertEquals(currentPlayer.getHandCards().size(),3);
        match.nextTurn();
        assertNotEquals(match.getCurrentPlayer(),match.getFirstPlayer());
    }

    @Test
    public void testCompleteGameWithDisconnection() throws RequirementsNotMetException, InvalidPlayCardException, InvalidPlayerException, ConnectionException {
        testGameSetupWithDisconnection();
        match.reconnectPlayer(disPlayer);
        boolean hasNextTurn;    // Set to true by default
        int i=1;
        do{
            Player currentPlayer=match.getCurrentPlayer();
            List<CardPlayable> handCards=currentPlayer.getHandCards();
            Coordinates coordinates=null;
            try{
                coordinates=new Coordinates(i,i);
            } catch (InvalidCoordinatesException e){
                //System.out.println("Invalid Coordinates");
            }

            try {
                if(handCards.getFirst().getSide(Side.SIDEFRONT).getCorners().get(1).isPlaceable()){
                    try {
                        match.playCard(handCards.getFirst(), Side.SIDEFRONT, coordinates);
                    } catch (RequirementsNotMetException e){
                        match.playCard(handCards.getFirst(), Side.SIDEBACK, coordinates);
                    }
                }
                else {
                    match.playCard(handCards.getFirst(), Side.SIDEBACK, coordinates);
                }
            } catch (GameStatusException e) {
                throw new RuntimeException(e);
            }
            assertEquals(currentPlayer.getHandCards().size(),2);
            List<CardPlayable> pickableCards=match.fetchPickables();
            try {
                for (int j = 0; j < pickableCards.size(); j++) {
                    if(pickableCards.get(j)!=null) {
//                        //System.out.println(pickableCards.get(j));
                        match.pickCard(pickableCards.get(j));
                        break;
                    } else if(j==pickableCards.size()-1) {
//                        //System.out.println("No pickable card");
                        match.pickCard(null);
                    }
                }
            } catch (GameStatusException | InvalidDrawCardException e) {
                throw new RuntimeException(e);
            }
            ////System.out.println(currentPlayer.getPoints());
            if(match.getGameStatus()==GameStatus.IN_GAME)
                assertEquals(currentPlayer.getHandCards().size(),3);
            Player lastPlayer=match.getPlayers().get((match.getPlayers().indexOf(match.getFirstPlayer())-1+3)%3);
            if(currentPlayer==lastPlayer)
                i++;
            try {
                hasNextTurn=match.nextTurn();
            } catch (GameStatusException e) {
                throw new RuntimeException(e);
            }
//            if(match.getGameStatus()==GameStatus.FINAL_PHASE)
                //System.out.println(match.getGameStatus());
        } while (hasNextTurn);
        try {
            match.addObjectivePoints();
        } catch(GameStatusException e){
            throw new RuntimeException(e);
        }
        //in GameController calcWinner is called immediately after addObjectivePoints, so this scenario isn't possible
        //I just used it because it's easier to test than a player who has more points but disconnects before the CALC POINTS phase
        Player wouldWin=match.getPlayers().getFirst();
        for(Player player : match.getPlayers())
            if(player.getPoints()>wouldWin.getPoints())
                wouldWin=player;
        match.disconnectPlayer(wouldWin.getPlayerLobby());
        //System.out.println(wouldWin.getPlayerLobby().getNickname()+" would have won");
        try{
            System.out.println("But he disconnected right before the end so "+match.calcWinner().get(0).getNickname()+" won");
//            assertEquals(match.calcWinner(), player0);
        } catch(GameStatusException e){
            throw new RuntimeException(e);
        }
    }

    //test some of the exceptions that are not tested by the other test of this class
    @Test
    public void testExceptions(){
        List<Player> players;
        player0=new Player("Al",new Token(ColorToken.RED));
        player1=new Player("John",new Token(ColorToken.BLUE));
        assertThrows(InvalidPlayersNumberException.class, ()->new Match(Collections.singletonList(player0)));
        players=new ArrayList<>(Arrays.asList(player0,player1));

        try{
            match=new Match(players);
        } catch (InvalidPlayersNumberException e){
            //System.out.println("Invalid number of players");
        }

        PlayerLobby unexistentPlayer=new PlayerLobby("j",ColorToken.RED);
        try {
            match.startGame();
        } catch (GameStatusException e) {
            throw new RuntimeException(e);
        }
        assertThrows(InvalidPlayerException.class,() -> match.disconnectPlayer(unexistentPlayer));
        assertThrows(InvalidPlayerException.class,() -> match.reconnectPlayer(unexistentPlayer));
        assertThrows(InvalidPlayerException.class,()->match.fetchAvailableCoord(unexistentPlayer));

        assertThrows(InvalidPlayerException.class,()->match.playStarter(unexistentPlayer,Side.SIDEFRONT));

        assertThrows(InvalidPlayerException.class,()->match.fetchPersonalObjectives(unexistentPlayer));
        assertThrows(InvalidPlayerException.class,()->match.choosePersonalObjective(unexistentPlayer,null));

        assertThrows(GameStatusException.class,()->match.playCard(null,Side.SIDEFRONT,new Coordinates(1,1)));
        assertThrows(GameStatusException.class,()->match.pickCard(null));

        assertThrows(InvalidPlayerException.class,()->match.getFieldByPlayer(unexistentPlayer));
    }
}
