package it.polimi.ingsw.am13.model;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.*;

import java.util.*;

/**
 * This class manages a match
 * The flow of the match is handled by game phase. The methods to controll the flow of the game are
 * <ul>
 *     <li>Constructor: sets the game phase to null (match created, decks instantiated, but game not yet started)</li>
 *     <li>startGame: starts initialization phase (mainly draws first cards for each player), setting game phase to INIT.</li>
 *     <li><code>playStarter(...)</code> and <code>choosePersonalObjective(...)</code>: every time a player plays
 *     their starter card and chooses one of the 2 personal objective cards, these methods check if initialization
 *     has been completed, and eventually sets game phase to IN_GAME</li>
 *     <li><code>nextTurn()</code>: makes necessary checks for passing to FINAL_PHASE and ultimately to CALC_POINTS
 *     During this game phase, the only method which can change the internal state are <code>pickCard(...)</code> and <code>playCard(...)</code></li>
 *     <li><code>calcObjectivePoints()</code>: Calculates extra points given by objective cards for each player,
 *     and moves the game on to phase ENDED</li>
 *     <li><code>calcWinner()</code>: Only in this ultimate phase this method can be called, calculating which player has won</li>
 * </ul>
 * After game phase becomes ENDED, the internal state of Match and the model in general cannot change any longer.
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
     * The size is >=2 and <=4, the colors of players are all different and cant be a black token among them.
     */
    private final List<Player> players;

    /**
     * Map associating player decontextualized from game to the actual playing player
     */
    private final Map<PlayerLobby, Player> playersMap;

    /**
     * The first player of the match
     */
    private final Player firstPlayer;
    /**
     * The index of the first player in the players list
     */
    private final int firstPlayerIndex;
    /**
     * The player who is playing in the current turn.
     * Null if the gameStatus is different from IN_GAME or FINAL_PHASE (the 2 phases divided in turns)
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
     * This method sets the game status to null (game not already really started), chooses the first player at random from the list of players,
     * initialises the four decks and sets the players list to the list it receives as a parameter.
     * @param players the players of the match
     */
    //TODO wrong phase exceptions
    //TODO: cambia eccezione InvalidPlayersNumberException se 2 giocatori hanno lo stesso colore
    public Match(List<Player> players) throws InvalidPlayersNumberException{
        if(players.size()<2 || players.size()>4)
            throw new InvalidPlayersNumberException();
        List<ColorToken> distinctColors = players.stream().map(p -> p.getPlayerLobby().getToken().getColor()).distinct().toList();
        if(distinctColors.contains(ColorToken.BLACK) || distinctColors.size()!=players.size())
            // Checks if two or more players have same color of black color
            throw new InvalidPlayersNumberException();
        this.players = players;
        playersMap = new HashMap<>();
        for(Player p : players) {
            playersMap.put(p.getPlayerLobby(), p);
        }

//        gameStatus = GameStatus.INIT;
        gameStatus = null;
        Random rnd=new Random(1000000009);
        firstPlayerIndex=rnd.nextInt(players.size()-1);
        firstPlayer=players.get(firstPlayerIndex);
        currentPlayer=null;

        LinkedList<CardResource> cardsResource;
        LinkedList<CardGold> cardsGold;
        LinkedList<CardObjective> cardsObjective;
        LinkedList<CardStarter> cardsStarter;
        CardFactory cardFactory=new CardFactory();
        try {
            cardsResource = cardFactory.createCardsResource();
            cardsGold= cardFactory.createCardsGold();
            cardsObjective= cardFactory.createCardsObjective();
            cardsStarter=cardFactory.createCardsStarter();
        }
        catch (InvalidCardCreationException e){
            System.out.println("Error in the creation of a card");
            throw new RuntimeException(e);
        }
        deckResources = new DeckHandler<>(cardsResource);
        deckGold = new DeckHandler<>(cardsGold);
        deckObjective = new DeckHandler<>(cardsObjective);
        deckStarter = new Deck<>(cardsStarter);

        countSetup=0;
    }

    /**
     * @return a list containing the 6 cards on the table that can be picked by players
     * The order: top of resource deck, 2 visible resource cards, top of gold deck, 2 visible gold cards
     */
    public List<CardPlayable> fetchPickables(){
        List<CardPlayable> pickableCards;
        try {
            pickableCards = new LinkedList<>(deckResources.getPickables());
        } catch (InvalidDrawCardException e) {
            System.out.println("There are no cards left in the resource cards deck");
            throw new RuntimeException(e);
        }
        try {
            pickableCards.addAll(deckGold.getPickables());
        } catch (InvalidDrawCardException e) {
            System.out.println("There are no cards left in the gold cards deck");
            throw new RuntimeException(e);
        }
        return pickableCards;
    }

    /**
     * Returns the visible cards for the common objectives.
     * Note that the deck of objective cards is always visible, with top card visible from its back side.
     * Hence this information is not included in the list
     * @return the list of the 2 common objectives.
     */
    public List<CardObjective> fetchCommonObjectives(){
        return deckObjective.getVisibleCards();
    }

    /**
     * Assigns the starter card to the player
     * @param player to which the starter card is assigned
     */
    private void setStarter(Player player){
      CardStarter cardStarter;
        try {
            cardStarter=deckStarter.draw();
        } catch (InvalidDrawCardException e) {
            System.out.println("The starter deck has no card left in it");
            throw new RuntimeException(e);
        }
        try {
            player.initStarter(cardStarter);
        } catch (VariableAlreadySetException e) {
            System.out.println("The starter card had already been set for this player");
            throw new RuntimeException(e);
        }
    }

    /**
     * @param player One of the players of the match
     * @return The starter card assigned to player
     * @throws InvalidPlayerException If the player is not among the playing players in the match
     */
    public CardStarter fetchStarter(PlayerLobby player) throws InvalidPlayerException {
        if(!playersMap.containsKey(player))
            throw new InvalidPlayerException();
        return playersMap.get(player).getStarter();
    }

    /**
     * This method plays the starting of the given player on the passed side
     * @param playerLobby Player who has chosen which side of the starting card he wants to play
     * @param side The side of the starting card
     * @throws InvalidPlayerException If the player is not among the playing players in the match
     * @throws GameStatusException If game phase is not INIT
     */
    public void playStarter(PlayerLobby playerLobby, Side side) throws GameStatusException, InvalidPlayerException{
        if(gameStatus!=GameStatus.INIT)
            throw new GameStatusException(gameStatus,GameStatus.INIT);
        if(!playersMap.containsKey(playerLobby))
            throw new InvalidPlayerException("The passed player is not one of the players of the match");
        try {
            playersMap.get(playerLobby).playStarter(side);
            countSetup++;
            checkInGamePhase();
        } catch (InvalidPlayCardException e) {
            throw new RuntimeException(e);
        } catch (InvalidChoiceException e){
            System.out.println("The passed side does not belong to the starter card assigned to the given player");
            throw new RuntimeException(e);
        }

    }

    /**
     * Assign to the given player the two objectives he can choose from
     * @param player one of the players of the match
     */
    private void setPossiblePersonalObjectives(Player player) {
        CardObjective cardObjective0, cardObjective1;
        try{
            cardObjective0=deckObjective.drawFromDeck();
        } catch (InvalidDrawCardException e){
            System.out.println("The objective deck has no card left in it");
            throw new RuntimeException(e);
        }
        try{
            cardObjective1=deckObjective.drawFromDeck();
        } catch (InvalidDrawCardException e){
            System.out.println("The objective deck has no card left in it");
            throw new RuntimeException(e);
        }
        try {
            player.initPossiblePersonalObjectives(cardObjective0,cardObjective1);
        } catch (VariableAlreadySetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param player one of the players of the match
     * @return the cards in the hand of the player
     * @throws InvalidPlayerException if the player is not one of the players of the match
     */
    public List<CardPlayable> fetchHandPlayable(PlayerLobby player) throws InvalidPlayerException{
        if(!playersMap.containsKey(player))
            throw new InvalidPlayerException("The passed player is not one of the players of the match");
        return playersMap.get(player).getHandCards();
    }

    /**
     * @param player one of the players of the match
     * @return the personal objective of the player (null if it hasn't been initialized yet)
     * @throws InvalidPlayerException if the passed player is not one of the players of the match
     */
    public CardObjective fetchHandObjective(PlayerLobby player) throws InvalidPlayerException{
        if(!playersMap.containsKey(player))
            throw new InvalidPlayerException("The passed player is not one of the players of the match");
        return playersMap.get(player).getPersonalObjective();
    }
    /**
     * @param player who should be contained in players
     * @return a list containing the two objective cards the player can choose from
     * @throws InvalidPlayerException if the player is not one of the players of this match
     */
    public List<CardObjective> fetchPersonalObjectives(PlayerLobby player) throws InvalidPlayerException{
        if(!playersMap.containsKey(player))
            throw new InvalidPlayerException("The passed player is not one of the players of the match");
        return playersMap.get(player).getPossiblePersonalObjectives();
    }

    /**
     * Sets the personal objective of the player according to his choice
     * @param player one of the players of the match
     * @param cardObjective the objective chosen by the player
     * @throws GameStatusException if this method isn't called in the INIT phase
     * @throws InvalidPlayerException if the player is not one of the players of this match
     * @throws InvalidChoiceException if the objective card does not belong to the list of the possible objective cards for the player
     * @throws VariableAlreadySetException if this method has been called before for the player
     */
    public void choosePersonalObjective(PlayerLobby player, CardObjectiveIF cardObjective)
            throws GameStatusException, InvalidPlayerException, InvalidChoiceException, VariableAlreadySetException {
        if(gameStatus!=GameStatus.INIT)
            throw new GameStatusException(gameStatus,GameStatus.INIT);
        if(!playersMap.containsKey(player))
            throw new InvalidPlayerException("The passed player is not one of the players of the match");
//        try {
        playersMap.get(player).initObjective(cardObjective);
        countSetup++;
        checkInGamePhase();
//        } catch (VariableAlreadySetException e) {
//            System.out.println("This player's objective has already been set");
//        } catch (InvalidChoiceException e){
//            System.out.println("The passed objective card is not one of the two objective cards assigned to the given player");
//        }
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
            throw new GameStatusException("We are currently in "+gameStatus+" phase, this method can only be called in the "+GameStatus.IN_GAME+" or "+GameStatus.FINAL_PHASE+" phases");
        if(player!=currentPlayer)
            throw new InvalidPlayerException("Its not the turn of the passed player");
        if(side==Side.SIDEFRONT) {
            try {
                player.playCard(card.getFront(),coordinates);
            } catch (InvalidPlayCardException e) {
                throw new RuntimeException(e);
            } /*catch (RequirementsNotMetException e) {
                System.out.println("The requirements to play this card aren't satisfied");
            }*/
        }
        else{
            try {
                player.playCard(card.getBack(),coordinates);
            } catch (InvalidPlayCardException e) {
                throw new RuntimeException(e);
            } catch (RequirementsNotMetException e) {
                System.out.println("The requirements to play this card aren't satisfied");
                throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the starter Card, sets the possible objective cards, gives the initial cards to the players.
     * Can be called only if the match has not started yer (<code>gamePhase==null</code>) and sets game phase to INIT.
     * @throws GameStatusException if this method is called when game has already started (<code>gamePhase!=null</code>)
     */
    //TODO should we change the name of this method? something like start setup or something like that, idk
    public void startGame() throws GameStatusException {
//        if(gameStatus!=GameStatus.INIT)
//            throw new GameStatusException(gameStatus,GameStatus.INIT);
        if(gameStatus!=null)
            throw new GameStatusException("Match has already started");
        gameStatus = GameStatus.INIT;

        for(int i=0;i<players.size();i++) {
            Player player=players.get((firstPlayerIndex + i) % players.size());
            setStarter(player);
            setPossiblePersonalObjectives(player);
            for(int j=0;j<2;j++) {
                try {
                    player.addCardToHand(deckResources.drawFromDeck());
                } catch (InvalidDrawCardException e) {
                    System.out.println("There are no cards left in the resources deck");
                    throw new RuntimeException(e);
                } catch (PlayerHandException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                player.addCardToHand(deckGold.drawFromDeck());
            } catch (InvalidDrawCardException e){
                System.out.println("There are no cards left in the gold deck");
                throw new RuntimeException(e);
            } catch (PlayerHandException e){
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method adds the points given by Objective cards to each player.
     * Make the game phase go on to ENDED
     * @throws GameStatusException if this method is called in a phase which is not the CALC_POINTS phase
     */
    public void addObjectivePoints() throws GameStatusException{
        if(gameStatus!=GameStatus.CALC_POINTS)
            throw new GameStatusException(gameStatus,GameStatus.CALC_POINTS);
        for(Player player : players)
            player.addObjectivePoints(deckObjective.showFromTable(0), deckObjective.showFromTable(1));
        gameStatus = GameStatus.ENDED;
    }
    /**
     * This method finds the winner, ie the player with the most points
     * @return the winner of the match
     * @throws GameStatusException if this method is called in a phase which is not the ENDED phase
     */
    public Player calcWinner() throws GameStatusException{
        if(gameStatus!=GameStatus.ENDED)
            throw new GameStatusException(gameStatus,GameStatus.ENDED);
//        Player winner = players.getFirst();
//        for(Player player : players) {
//            if(player.getPoints()>winner.getPoints())
//                winner=player;
//        }
        return players.stream().max(Comparator.comparingInt(Player::getPoints)).orElseThrow();
    }

    /**
     * This method makes the match proceed from the turn that has just been played to the next one,
     * by changing the currentPlayer and, if necessary, changing the GameStatus
     * @return False if there is no other turn to play after the one that has just been played, true otherwise
     * @throws GameStatusException If this method is called in the INIT or CALC POINTS phase
     */
    public boolean nextTurn() throws GameStatusException{
        if(gameStatus!=GameStatus.IN_GAME && gameStatus!=GameStatus.FINAL_PHASE)
            throw new GameStatusException("We are currently in "+gameStatus+" phase, this method can only be called in the "+GameStatus.IN_GAME+" or "+GameStatus.FINAL_PHASE+" phases");
        if(gameStatus==GameStatus.IN_GAME && checkFinalPhase()){
            gameStatus = GameStatus.FINAL_PHASE;
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
                    gameStatus = GameStatus.CALC_POINTS;
                    currentPlayer = null;
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
     * @return the status of the game
     */
    public GameStatus getGameStatus() {
        return gameStatus;
    }

    /**
     * @return the player who is playing in the current turn.
     * Null if the gameStatus is different from IN_GAME or FINAL_PHASE (the 2 phases divided in turns)
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Set<PlayerLobby> getPlayersLobby() {
        return playersMap.keySet();
    }

    /**
     * @return the List of coordinates in which new cards can be played
     */
    public List<Coordinates> fetchAvailableCoord(Player player){
        return player.getAvailableCoord();
    }
}
