package it.polimi.ingsw.am13.model;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.*;

import java.util.*;

/**
 * This class manages a match
 * The flow of the match is handled by game phase. The methods to control the flow of the game are
 * <ul>
 *     <li>{@link #Match(List)}: sets the game phase to null (match created, decks instantiated, but game not yet started)</li>
 *     <li>{@link #startGame()}: starts initialization phase (mainly draws first cards for each player), setting game phase to INIT.</li>
 *     <li>{@link #playStarter(PlayerLobby, Side)} and {@link #choosePersonalObjective(PlayerLobby, CardObjectiveIF)}:
 *     every time a player plays their starter card and chooses one of the 2 personal objective cards, these methods check if initialization
 *     has been completed, and eventually sets game phase to IN_GAME</li>
 *     <li>{@link #nextTurn()}: makes necessary checks for passing to FINAL_PHASE and ultimately to CALC_POINTS
 *     During this game phase, the only method which can change the internal state are <code>pickCard(...)</code> and <code>playCard(...)</code></li>
 *     <li>{@link #addObjectivePoints()}: Calculates extra points given by objective cards for each player,
 *     and moves the game on to phase ENDED</li>
 *     <li>{@link #calcWinner()}: Only in this ultimate phase this method can be called, calculating which player has won</li>
 * </ul>
 * After game phase becomes ENDED, the internal state of Match and the model in general cannot change any longer.
 */

//TODO: Pensa se gestire il caso in cui availableCoord è empty (giocatore non piò fare più niente)
public class Match {
    private final DeckHandler<CardResource> deckResources;
    private final DeckHandler<CardGold> deckGold;
    private final DeckHandler<CardObjective> deckObjective;
    private final Deck<CardStarter> deckStarter;

    /**
     * The players of the match. The list is created by building the entire class, and cannot change
     * The size is >=2 and <=4, the colors of players are all different
     * It includes all the players, even if they are disconnected
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
     * Count of the actions done by a player during their turn. It has a relevant value only during phase IN_GAME, FINAL_PHASE
     * The possible values are referred to current player and are:
     * <ul>
     *     <li>0: Turn has started, player did nothing yet</li>
     *     <li>1: Player has played a card on their field, but has not picked any card</li>
     *     <li>2: Player picked a card after playing, now turn is ended and game can move on to another player</li>
     * </ul>
     */
    private int turnActionsCounter;

    /**
     * Initializes the match. It sets the game status to null (game not already really started), chooses the first player
     * at random from the list of players, instantiates the four decks and sets the players list to the list it receives as a parameter.
     * @param players the players of the match
     * @throws InvalidPlayersNumberException If the number of players is not between 2 and 4,
     * or the players have tokens of same colors
     */
    public Match(List<Player> players) throws InvalidPlayersNumberException{
        if(players.size()<2 || players.size()>4)
            throw new InvalidPlayersNumberException();
        List<ColorToken> distinctColors = players.stream().map(p -> p.getPlayerLobby().getToken().getColor()).distinct().toList();
        if(distinctColors.size()!=players.size())
            throw new InvalidPlayersNumberException();
        this.players = Collections.unmodifiableList(players);
        playersMap = new HashMap<>();
        for(Player p : players) {
            playersMap.put(p.getPlayerLobby(), p);
        }

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


    // DISCONNECTIONS


    /**
     * Disconnects the given player.
     * If this method is called before the player chooses the side in which he should play his starter card, or his secret objective,
     * it assigns them to him.
     * If this method is called when a player has played his card but not picked one yet, it assigns the first non-null
     * pickable card to the player.
     * This method should not be called before game has actually starter
     * Then it calls the corresponding method in the Player class.
     * Note that the method only sets the player as "disconnected", but it does not remove the player themself from the game
     * (that is the list of players for this match still includes the disconnected players).
     * @throws ConnectionException if the player was already not connected when this method was called
     * @throws InvalidPlayerException if player is not among this match's players
     */
    public void disconnectPlayer(PlayerLobby player) throws ConnectionException, InvalidPlayerException {
        if(!playersMap.containsKey(player))
            throw new InvalidPlayerException();
        if(!playersMap.get(player).isConnected())
            throw new ConnectionException("Player " + player + " was already disconnected");
        if(gameStatus==GameStatus.INIT){
            try {
                if(playersMap.get(player).getField().getCardSideAtCoord(Coordinates.origin())==null)
                    playStarter(player,Side.SIDEFRONT);
                if(playersMap.get(player).getPersonalObjective()==null)
                    choosePersonalObjective(player,playersMap.get(player).getPossiblePersonalObjectives().getFirst());
            } catch (GameStatusException | InvalidChoiceException | VariableAlreadySetException |
                     InvalidPlayCardException e) {//the exceptions should never be thrown inside this if
                throw new RuntimeException(e);
            }
        }
        if((gameStatus==GameStatus.IN_GAME || gameStatus==GameStatus.FINAL_PHASE) && turnActionsCounter==1) {
            CardPlayable cardToPick = fetchPickables().stream().filter(Objects::nonNull).findFirst().orElse(null);
            try {
                pickCard(cardToPick);
            } catch (GameStatusException | InvalidDrawCardException e) { //pickCard should never throw this exception because of the checks that are done above
                throw new RuntimeException(e);
            }
        }
        playersMap.get(player).disconnectPlayer();
    }

    /**
     * Reconnects the given player by calling the corresponding method in the Player class
     * @throws ConnectionException if the player was already connected when this method was called
     * @throws InvalidPlayerException if player is not among this match's players
     */
    public void reconnectPlayer(PlayerLobby player) throws ConnectionException, InvalidPlayerException {
        if(!playersMap.containsKey(player))
            throw new InvalidPlayerException();
        playersMap.get(player).reconnectPlayer();
    }

    /**
     *
     * @param player one of the players of the match
     * @return whether a player is currently connected or not
     * @throws InvalidPlayerException if player is not among this match's players
     */
    public boolean fetchIsConnected(PlayerLobby player) throws InvalidPlayerException{
        if(!playersMap.containsKey(player))
            throw new InvalidPlayerException();
        return playersMap.get(player).isConnected();
    }

    /**
     *
     * @return the number of players that are currently connected
     */
    public int countConnected(){
        int count=0;
        for(Player player : players)
            if(player.isConnected())
                count++;
        return count;
    }


    // GENERAL FETCHERS


    /**
     * List of all visible cards (that are pickable during turn phases).
     * The list is of size 6, with order: top of deck (with <code>getVisibleSide()==Side.SIDEBACK</code>),
     * and 2 visible cards (with <code>getVisibleSide()==Side.SIDEFRONT</code>), and repetion of this.
     * Elements can be null. If a deck is empty but both its cards are present, only the first element of the set of 3 will be null.
     * Besides this first element of the set, also one or both of the other ones can be null (if it remains only one or no cards
     * of this type to be picked)
     * @return a new list containing the 6 cards on the table that can be picked by players
     * The order: top of resource deck, 2 visible resource cards, top of gold deck, 2 visible gold cards
     */
    public List<CardPlayable> fetchPickables() {
        List<CardPlayable> pickableCards = new LinkedList<>(deckResources.getPickables());
        pickableCards.addAll(deckGold.getPickables());
        return pickableCards;
    }

    /**
     * List of all visible cards (that are pickable during turn phases).
     * The list is of size 6, with order: top of deck (with <code>getVisibleSide()==Side.SIDEBACK</code>),
     * and 2 visible cards (with <code>getVisibleSide()==Side.SIDEFRONT</code>), and repetion of this.
     * If a deck is empty but both its cards are present, only the first optional of the set of 3 will be empty.
     * Besides this first element of the set, also one or both of the other ones can be empty optionals
     * @return a new list containing the 6 cards on the table that can be picked by players
     * The order: top of resource deck, 2 visible resource cards, top of gold deck, 2 visible gold cards
     */
    public List<Optional<? extends CardPlayable>> fetchPickablesOptional() {
        List<Optional<? extends CardPlayable>> pickableCards;
        pickableCards = new LinkedList<>(deckResources.getPickablesOptional());
        pickableCards.addAll(deckGold.getPickablesOptional());
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
     * @return the List of coordinates in which new cards can be played
     * If game has not started yet, the list is empty
     * @throws InvalidPlayerException If player is not among this match's players
     */
    public List<Coordinates> fetchAvailableCoord(PlayerLobby player) throws InvalidPlayerException {
        if(!playersMap.containsKey(player))
            throw new InvalidPlayerException();
        return playersMap.get(player).getAvailableCoord();
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


    // INIT PHASE


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
     * @throws InvalidPlayCardException Positioning error of the card at coordinates (0,0).
     */
    public void playStarter(PlayerLobby playerLobby, Side side)
            throws GameStatusException, InvalidPlayerException, InvalidPlayCardException {
        if(gameStatus!=GameStatus.INIT)
            throw new GameStatusException(gameStatus,GameStatus.INIT);
        if(!playersMap.containsKey(playerLobby))
            throw new InvalidPlayerException("The passed player is not one of the players of the match");
        if(playersMap.get(playerLobby).isConnected()) {
            try {
                playersMap.get(playerLobby).playStarter(side);
            } catch (InvalidChoiceException e) {
                System.out.println("The passed side does not belong to the starter card assigned to the given player");
                throw new RuntimeException(e);
            }
        }
        countSetup++;
        checkInGamePhase();
    }

    /**
     * Assign to the given player the two objectives he can choose from
     * @param player one of the players of the match
     */
    private void setPossiblePersonalObjectives(Player player) {
        CardObjective cardObjective0, cardObjective1;
        try{
            cardObjective0=deckObjective.drawFromDeck();
            cardObjective1=deckObjective.drawFromDeck();
            player.initPossiblePersonalObjectives(cardObjective0,cardObjective1);
        } catch (InvalidDrawCardException e){
            throw new RuntimeException("The objective deck has no card left in it", e);
        } catch (VariableAlreadySetException e) {
            throw new RuntimeException(e);
        }
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
        if(playersMap.get(player).isConnected())
            playersMap.get(player).initObjective(cardObjective);
        countSetup++;
        checkInGamePhase();
    }

    /**
     * This method checks if the initial phase (game setup) has been completed, and if so, sets the game status to IN_GAME
     */
    private void checkInGamePhase(){
        if(countSetup==2*players.size()) {
            gameStatus = GameStatus.IN_GAME;
            currentPlayer = firstPlayer;
            turnActionsCounter = 0;
        }
    }


    // TURN-BASED PHASES


    /**
     * Sets the starter Card, sets the possible objective cards, gives the initial cards to the players.
     * Can be called only if the match has not started yet (<code>gamePhase==null</code>) and sets game phase to INIT.
     * @throws GameStatusException if this method is called when game has already started (<code>gamePhase!=null</code>)
     */
    public void startGame() throws GameStatusException {
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
     * Plays a given card side on the field of a given player, at the given coordinates
     * It only increases the turnActionsCounter if the player is not connected
     * @param cardIF Card which is being played. It must be in player's hand
     * @param side indicates whether the card is going to be played on the front or on the back
     * @param coordinates in the field of the player where the card is going to be positioned
     * @throws GameStatusException if the actual phase is different from IN_GAME or FINAL_PHASE,
     * or it's not the moment in turn for playing the card on field
     * @throws RequirementsNotMetException If the requirements for playing the specified card in player's field are not met
     * @throws InvalidPlayCardException If the player doesn't have the specified card, or generic positioning error
     */
    public void playCard(CardPlayableIF cardIF, Side side, Coordinates coordinates)
            throws GameStatusException, RequirementsNotMetException, InvalidPlayCardException {
        // Right game phase
        if(gameStatus!=GameStatus.IN_GAME && gameStatus!=GameStatus.FINAL_PHASE)
            throw new GameStatusException("We are currently in "+gameStatus+" phase, this method can only be called in the "+GameStatus.IN_GAME+" or "+GameStatus.FINAL_PHASE+" phases");
        // Right moment in turn-based phases for playing the card
        if(turnActionsCounter!=0)
            throw new GameStatusException("It's not the moment for playing the card on field");

        if(currentPlayer.isConnected()) {
            // Card must be in player's hand. In this case, I retrieve che card itself (instead on the interface of the parameter)
            CardPlayable card = null;
            for (CardPlayable c : currentPlayer.getHandCards())
                if (c.equals(cardIF)) {
                    card = c;
                }
            if (card == null) {
                throw new InvalidPlayCardException("Player doesn't have the card he's trying to play");
            }

            if (side == Side.SIDEFRONT) {
                currentPlayer.playCard(card.getSide(Side.SIDEFRONT), coordinates);
            } else {
                currentPlayer.playCard(card.getSide(Side.SIDEBACK), coordinates);
            }
        }
        turnActionsCounter++;
    }

    /**
     * Picks one of the 6 cards on the table
     * If the player isn't connected or there is no card to pick, it only increases the turnActionsCounter
     * @param cardIF A playable card that should be in the field
     * @throws InvalidDrawCardException if the passed card is not on the table
     * @throws GameStatusException if this method is called not in game phase IN_GAME or FINAL_PHASE, or
     * if it's not the right moment for picking the card from common field
     */
    public void pickCard(CardPlayableIF cardIF) throws GameStatusException, InvalidDrawCardException{
        //Right game phase
        if(gameStatus!=GameStatus.IN_GAME && gameStatus!=GameStatus.FINAL_PHASE)
            throw new GameStatusException("We are currently in "+gameStatus+" phase, this method can only be called in the "+GameStatus.IN_GAME+" or "+GameStatus.FINAL_PHASE+" phases");
        if(turnActionsCounter!=1)
            throw new GameStatusException("It's not the moment for picking the card from the common field");

        if(currentPlayer.isConnected() && fetchPickables().stream().anyMatch(Objects::nonNull)) {
            CardPlayable card;
            card = deckResources.pickCard(cardIF);
            if (card == null) {
                card = deckGold.pickCard(cardIF);
            }
            if (card == null) {
                throw new InvalidDrawCardException("The given card is not on the table");
            }
            try {
                currentPlayer.addCardToHand(card);
            } catch (PlayerHandException e) {
                throw new RuntimeException(e);
            }
        }
        turnActionsCounter++;
    }

    /**
     * This method makes the match proceed from the turn that has just been played to the next one,
     * by changing the currentPlayer and, if necessary, changing the GameStatus
     * @return False if there is no other turn to play after the one that has just been played, true otherwise
     * @throws GameStatusException if this method is called not in game phase IN_GAME or FINAL_PHASE, or
     *      * if it's not the right moment for changing turn (player still has to play and/or pick)
     */
    public boolean nextTurn() throws GameStatusException{
        // Right game phase
        if(gameStatus!=GameStatus.IN_GAME && gameStatus!=GameStatus.FINAL_PHASE)
            throw new GameStatusException("We are currently in "+gameStatus+" phase, this method can only be called in the "+GameStatus.IN_GAME+" or "+GameStatus.FINAL_PHASE+" phases");
        // Right moment to move to next possible turn
        if(turnActionsCounter!=2)
            throw new GameStatusException("It's not the moment to change turn, player still has to play and/or pick");
        turnActionsCounter = 0;

        if(gameStatus==GameStatus.IN_GAME && checkFinalPhase()){
            gameStatus = GameStatus.FINAL_PHASE;
            int currentPlayerIndex = players.indexOf(currentPlayer);
            turnsToEnd=players.size()+(firstPlayerIndex-currentPlayerIndex-1+players.size())%players.size();
        }
        else {
            if (gameStatus == GameStatus.FINAL_PHASE) {
                turnsToEnd--;
                if (turnsToEnd == 0) {
                    gameStatus = GameStatus.CALC_POINTS;
                    currentPlayer = null;
                    return false;
                }
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
     * or both the Resources and Gold decks are empty) are satisfied, false otherwise
     */
    private boolean checkFinalPhase(){
        return currentPlayer.getPoints() >= 20 || (deckResources.isDeckEmpty() && deckGold.isDeckEmpty());
    }


    // LAST PHASES


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
     * This method finds the winner among the connected players, ie the one with the most points
     * @return the winner of the match
     * @throws GameStatusException if this method is called in a phase which is not the ENDED phase, unless there is only one player left
     */
    public PlayerLobby calcWinner() throws GameStatusException{
        if(gameStatus!=GameStatus.ENDED && countConnected()!=1)
            throw new GameStatusException(gameStatus,GameStatus.ENDED);
        return players.stream().filter(Player::isConnected).max(Comparator.comparingInt(Player::getPoints))
                .map(Player::getPlayerLobby).orElseThrow();
    }


    // GETTERS

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

    /**
     * @return The first player of the match
     */
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * @return The players of the match
     * The size is >=2 and <=4, and the colors of players are all different
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * @param playerLobby Player whose field is returned
     * @return Field (interface) of the given player
     * @throws InvalidPlayerException If the player is not among the playing players in the match
     */
    public FieldIF getFieldByPlayer(PlayerLobby playerLobby) throws InvalidPlayerException {
        if(!playersMap.containsKey(playerLobby))
            throw new InvalidPlayerException();
        return playersMap.get(playerLobby).getField();
    }

}
