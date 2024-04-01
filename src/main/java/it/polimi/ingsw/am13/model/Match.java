package it.polimi.ingsw.am13.model;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class manages a match
 */
//TODO: currently it isn't possible to retrieve the field through Match (we are missing getters).
//TODO In principle we have enough information (we know all the actions of the player) in the client to build it there,
//TODO so that's not strictly necessary, it depends on how we want to implement this
//TODO: player currently does not expose available coordinates

//TODO: we currently don't manage at all the possibility of a player being disconnected
public class Match {
    private final DeckHandler<CardResource> deckResources;
    private final DeckHandler<CardGold> deckGold;
    private final DeckHandler<CardObjective> deckObjective;
    private final Deck<CardStarter> deckStarter;
    /**
     * The players of the match
     */
    private final List<Player> players;
    /**
     * The first player of the match
     */
    private final Player firstPlayer;
    /**
     * The index of the first player in the players list
     */
    private final int firstPlayerIndex;
    /**
     * The player who is playing in the current turn
     */
    private Player currentPlayer;
    /**
     * The status of the game
     */
    private GameStatus gameStatus;
    /**
     * This variable keeps track of how many turns are left during the final phase of the game
     */
    private int turnsToEnd;

    /**
     * This variable keeps track of how players have chosen their objective card and played their starter card
     */
    private int countSetup;

    /**
     * This method sets the game status to INIT (initialization), chooses the first player at random from the list of players,
     * initialises the four decks and sets the players list to the list it receives as a parameter.
     * @param players the players of the match
     */
    //TODO wrong phase exceptions
    public Match(List<Player> players) throws InvalidPlayersNumberException{
        if(players.size()<2 || players.size()>4)
            throw new InvalidPlayersNumberException();
        gameStatus=GameStatus.INIT;
        this.players = players;
        Random rnd=new Random(1000000009);
        firstPlayerIndex=rnd.nextInt(players.size()-1);
        firstPlayer=players.get(firstPlayerIndex);
        currentPlayer=null;

        LinkedList<CardResource> cardsResource=null;
        LinkedList<CardGold> cardsGold=null;
        LinkedList<CardObjective> cardsObjective=null;
        LinkedList<CardStarter> cardsStarter=null;
        CardFactory cardFactory=new CardFactory();
        try {
            cardsResource = cardFactory.createCardsResource();
            cardsGold= cardFactory.createCardsGold();
            cardsObjective= cardFactory.createCardsObjective();
            cardsStarter=cardFactory.createCardsStarter();
        }
        catch (InvalidCardCreationException e){
            System.out.println("Error in the creation of a card");
        }
        deckResources = new DeckHandler<>(cardsResource);
        deckGold = new DeckHandler<>(cardsGold);
        deckObjective = new DeckHandler<>(cardsObjective);
        deckStarter = new Deck<>(cardsStarter);

        countSetup=0;
    }

    /**
     *
     * @return a list containing the 6 cards on the table that can be picked by players
     */
    public List<CardPlayable> fetchPickables(){
        List<CardPlayable> pickableCards=new LinkedList<>();
        try {
            pickableCards.addAll(deckResources.getPickables());
        } catch (InvalidDrawCardException e) {
            System.out.println("There are no cards left in the resource cards deck");
        }
        try {
            pickableCards.addAll(deckGold.getPickables());
        } catch (InvalidDrawCardException e) {
            System.out.println("There are no cards left in the gold cards deck");
        }
        return pickableCards;
    }

    /**
     *
     * @return the two common objectives
     */
    public List<CardObjective> fetchCommonObjectives(){
        return deckObjective.getVisibleCards();
    }

    /**
     * Assigns the starter card to the player
     * @param player to which the starter card is assigned
     */
    private void setStarter(Player player){
      CardStarter cardStarter=null;
        try {
            cardStarter=deckStarter.draw();
        } catch (InvalidDrawCardException e) {
            System.out.println("The starter deck has no card left in it");
        }
        try {
            player.initStarter(cardStarter);
        } catch (VariableAlreadySetException e) {
            System.out.println("The starter card had already been set for this player");
        }
    }
    /**
     *
     * @param player one of the players of the match
     * @return the starter card assigned to player
     */
    public CardStarter fetchStarter(Player player){
        return player.getStarter();
    }
    /**
     * This method plays the starting of the given player on the passed side
     * @param player who has chosen which side of the starting card he wants to play
     * @param side the side of the starting card
     */
    public void playStarter(Player player, Side side) throws GameStatusException, InvalidPlayerException{
        if(gameStatus!=GameStatus.INIT)
            throw new GameStatusException(gameStatus,GameStatus.INIT);
        if(!players.contains(player))
            throw new InvalidPlayerException("The passed player is not one of the players of the match");
        try {
            player.playStarter(side);
            countSetup++;
            checkInGamePhase();
        } catch (InvalidPlayCardException e) {
            System.out.println(e);
        } catch (InvalidChoiceException e){
            System.out.println("The passed side does not belong to the starter card assigned to the given player");
        }

    }

    /**
     * Assign to the given player the two objectives he can choose from
     * @param player one of the players of the match
     */
    private void setPossiblePersonalObjectives(Player player){
        CardObjective cardObjective0=null,cardObjective1=null;
        try{
            cardObjective0=deckObjective.drawFromDeck();
        } catch (InvalidDrawCardException e){
            System.out.println("The objective deck has no card left in it");
        }
        try{
            cardObjective1=deckObjective.drawFromDeck();
        } catch (InvalidDrawCardException e){
            System.out.println("The objective deck has no card left in it");
        }
        try {
            player.initPossiblePersonalObjectives(cardObjective0,cardObjective1);
        } catch (VariableAlreadySetException e) {
            System.out.println("The possible objective cards had already been set for this player");
        }
    }

    /**
     *
     * @param player one of the players of the match
     * @return the cards in the hand of the player
     * @throws InvalidPlayerException if the player is not one of the players of the match
     */
    public List<CardPlayable> fetchHandPlayable(Player player) throws InvalidPlayerException{
        if(!players.contains(player))
            throw new InvalidPlayerException("The passed player is not one of the players of the match");
        return player.getHandCards();
    }

    /**
     *
     * @param player one of the players of the match
     * @return the personal objective of the player
     * @throws InvalidPlayerException if the passed player is not one of the players of the match
     */
    public CardObjective fetchHandObjective(Player player) throws InvalidPlayerException{
        if(!players.contains(player))
            throw new InvalidPlayerException("The passed player is not one of the players of the match");
        return player.getPersonalObjective();
    }
    /**
     *
     * @param player who should be contained in players
     * @return a list containing the two objective cards the player can choose from
     */
    public List<CardObjective> fetchPersonalObjectives(Player player) throws InvalidPlayerException{
        if(!players.contains(player))
            throw new InvalidPlayerException("The passed player is not one of the players of the match");
        return player.getPossiblePersonalObjectives();
    }

    /**
     * Sets the personal objective of the player according to his choice
     * @param player one of the players of the match
     * @param cardObjective the objective chosen by the player
     * @throws GameStatusException if this method isn't called in the INIT phase
     * @throws InvalidPlayerException if the player is not one of the players of this match
     */
    public void choosePersonalObjective(Player player, CardObjective cardObjective) throws GameStatusException, InvalidPlayerException{
        if(gameStatus!=GameStatus.INIT)
            throw new GameStatusException(gameStatus,GameStatus.INIT);
        if(!players.contains(player))
            throw new InvalidPlayerException("The passed player is not one of the players of the match");
        try {
            player.initObjective(cardObjective);
            countSetup++;
            checkInGamePhase();
        } catch (VariableAlreadySetException e) {
            System.out.println("This player's objective has already been set");
        } catch (InvalidChoiceException e){
            System.out.println("The passed objective card is not one of the two objective cards assigned to the given player");
        }
    }



    /**
     * Plays a given card side on the field of a given player, at the given coordinates
     * @param player who is playing the card
     * @param card which is being played
     * @param side indicates whether the card is going to be played on the front or on the back
     * @param coordinates in the field of the player where the card is going to be positioned
     * @throws GameStatusException if this method is called in the INIT or CALC POINTS phase
     * @throws InvalidPlayerException if it's not the turn of the passed player
     */
    public void playCard(Player player, CardPlayable card, Side side, Coordinates coordinates) throws GameStatusException,InvalidPlayerException,RequirementsNotMetException{
        if(gameStatus!=GameStatus.IN_GAME && gameStatus!=GameStatus.FINAL_PHASE)
            throw new GameStatusException("We are currently in "+gameStatus+" phase, this method can only be called in the "+GameStatus.IN_GAME.toString()+" or "+GameStatus.FINAL_PHASE+" phases");
        if(player!=currentPlayer)
            throw new InvalidPlayerException("Its not the turn of the passed player");
        if(side==Side.SIDEFRONT) {
            try {
                player.playCard(card.getFront(),coordinates);
            } catch (InvalidPlayCardException e) {
                System.out.println(e);
            } /*catch (RequirementsNotMetException e) {
                System.out.println("The requirements to play this card aren't satisfied");
            }*/
        }
        else{
            try {
                player.playCard(card.getBack(),coordinates);
            } catch (InvalidPlayCardException e) {
                System.out.println(e);
            } catch (RequirementsNotMetException e) {
                System.out.println("The requirements to play this card aren't satisfied");
            }
        }
    }

    /**
     * Picks one of the 6 cards on the table
     * @param cardPlayable a playable card that should be in the field
     * @throws InvalidDrawCardException if the passed card is not on the table
     * @throws GameStatusException if this method is called in the INIT or CALC POINTS phase
     */
    public void pickCard(CardPlayable cardPlayable) throws GameStatusException, InvalidDrawCardException{
        if(gameStatus!=GameStatus.IN_GAME && gameStatus!=GameStatus.FINAL_PHASE)
            throw new GameStatusException("We are currently in "+gameStatus+" phase, this method can only be called in the "+GameStatus.IN_GAME+" or "+GameStatus.FINAL_PHASE+" phases");
        boolean found;
        found=deckResources.pickCard(cardPlayable);
        if(!found)
            found=deckGold.pickCard(cardPlayable);
        if(!found)
            throw new InvalidDrawCardException("The given card is not on the table");
        try {
            currentPlayer.addCardToHand(cardPlayable);
        } catch (PlayerHandException e) {
            System.out.println(e);
        }
    }

    /**
     * Sets the starter Card, sets the possible objective cards, gives the initial cards
     * to the players
     * @throws GameStatusException if this method is called in a phase which is not the INIT phase
     */
    //TODO should we change the name of this method? something like start setup or something like that, idk
    public void startGame() throws GameStatusException{
        if(gameStatus!=GameStatus.INIT)
            throw new GameStatusException(gameStatus,GameStatus.INIT);
        for(int i=0;i<players.size();i++) {
            Player player=players.get((firstPlayerIndex + i) % players.size());
            setStarter(player);
            setPossiblePersonalObjectives(player);
            for(int j=0;j<2;j++) {
                try {
                    player.addCardToHand(deckResources.drawFromDeck());
                } catch (InvalidDrawCardException e) {
                    System.out.println("There are no cards left in the resources deck");
                } catch (PlayerHandException e) {
                    System.out.println(e);
                }
            }
            try {
                player.addCardToHand(deckGold.drawFromDeck());
            } catch (InvalidDrawCardException e){
                System.out.println("There are no cards left in the gold deck");
            } catch (PlayerHandException e){
                System.out.println(e);
            }
        }
    }

    /**
     * This method adds the points given by Objective cards to each player
     * @throws GameStatusException if this method is called in a phase which is not the CALC_POINTS phase
     */
    //TODO currently we don't check in any class that this is only called once
    public void addObjectivePoints() throws GameStatusException{
        if(gameStatus!=GameStatus.CALC_POINTS)
            throw new GameStatusException(gameStatus,GameStatus.CALC_POINTS);
        for(Player player : players)
            player.addObjectivePoints(deckObjective.showFromTable(0),deckObjective.showFromTable(1));
    }
    /**
     * This method finds the winner, ie the player with the most points
     * @return the winner of the match
     * @throws GameStatusException if this method is called in a phase which is not the CALC_POINTS phase
     */
    public Player calcWinner() throws GameStatusException{
        if(gameStatus!=GameStatus.CALC_POINTS)
            throw new GameStatusException(gameStatus,GameStatus.CALC_POINTS);
        Player winner=players.get(0);
        for(Player player : players) {
            if(player.getPoints()>winner.getPoints())
                winner=player;
        }
        return winner;
    }

    /**
     * This method makes the match proceed from the turn that has just been played to the next one,
     * by changing the currentPlayer and, if necessary, changing the GameStatus
     * @return false if there is no other turn to play after the one that has just been played, true otherwise
     * @throws GameStatusException if this method is called in the INIT or CALC POINTS phase
     */
    public boolean nextTurn() throws GameStatusException{
        if(gameStatus!=GameStatus.IN_GAME && gameStatus!=GameStatus.FINAL_PHASE)
            throw new GameStatusException("We are currently in "+gameStatus+" phase, this method can only be called in the "+GameStatus.IN_GAME+" or "+GameStatus.FINAL_PHASE+" phases");
        if(gameStatus==GameStatus.IN_GAME && checkFinalPhase()){
            gameStatus=GameStatus.FINAL_PHASE;
            int currentPlayerIndex=0;
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i) == currentPlayer)
                    currentPlayerIndex = i;
            }
            turnsToEnd=players.size()+(firstPlayerIndex-currentPlayerIndex-1+players.size())%players.size();
        }
        else {
            if (gameStatus == GameStatus.FINAL_PHASE) {
                if (turnsToEnd == 0) {
                    gameStatus=GameStatus.CALC_POINTS;
                    return false;
                }
                turnsToEnd--;
            }
        }
        int i=0;
        while (players.get(i)!=currentPlayer)
            i++;
        currentPlayer=players.get((i+1)%players.size());
        return true;
    }

    /**
     *
     * @return true if the conditions triggering the end of the game (a player has reached 20 points
     * or both the Resources and Gold decks are finished) are satisfied, false otherwise
     */
    private boolean checkFinalPhase(){
        return currentPlayer.getPoints() >= 20 || (deckResources.isDeckEmpty() && deckGold.isDeckEmpty());
    }

    /**
     * This method checks if the initial phase (game setup) has been completed, and if so, sets the game status to IN_GAME
     */
    private void checkInGamePhase(){
        if(countSetup==2*players.size()) {
            gameStatus = GameStatus.IN_GAME;
            currentPlayer = firstPlayer;
        }
    }
    /**
     *
     * @return the status of the game
     */
    public GameStatus getGameStatus() {
        return gameStatus;
    }

    /**
     *
     * @return the player who is playing in the current turn
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     *
     * @return the List of coordinates in which new cards can be played
     */
    public List<Coordinates> fetchAvailableCoord(Player player){
        return player.getAvailableCoord();
    }
}
