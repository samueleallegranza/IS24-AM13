package it.polimi.ingsw.am13.controller;

import it.polimi.ingsw.am13.model.GameModel;
import it.polimi.ingsw.am13.model.GameStatus;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;
import java.util.Objects;

//TODO it might be necessary to add some synchronized (should be correct now, but I am still not sure)
/**
 * This is a controller for a single game/match
 */
public class GameController implements Runnable {
    /**
     * The model of the game
     */
    private final GameModel gameModel;
    /**
     * Constant storing the timeout used to detect the disconnection of a client (in ms)
     */
    private static final Long timeout=4000L;
    /**
     * Constant storing the sleep time in ms of the threads managing connections (i.e. how often they repeat the checks)
     */
    private static final Long sleepTime=500L;
    /**
     * How much time (in ms) the controller waits for a client to reconnect when only 1 player or fewer are connected
     */
    private static final Long timeToWaitReconnection=10000L;

    /**
     * Thread used to manage the reconnection timer which is started when there only 1 player left
     */
    private Thread reconnectionThread;
    /**
     * Creates a new instance of <code>GameController</code> with the specified players.
     * The players used here to create the model are the definitive players, and nobody can be added afterwards.
     * It also starts the game
     * @param gameId Class match with all the information regarding the match itself and how to precess it
     * @param listenerHandler Handler of the GameListeners corresponding to the players who will take part in the game
     * @throws InvalidPlayersNumberException If lists nicks, colors have size <2 or >4, or one of the colors is black,
     * or there are duplicate chosen colors
     */
    GameController(int gameId, ListenerHandler listenerHandler) throws InvalidPlayersNumberException {
        gameModel=new GameModel(gameId, listenerHandler);
        try {
            gameModel.startGame(this);
        } catch (GameStatusException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the ping of the given game listener by setting it to the current time
     * @param playerLobby one of players of the match
     */
    public synchronized void updatePing(PlayerLobby playerLobby){
        for(GameListener gameListener : gameModel.getListeners())
            if(gameListener.getPlayer()==playerLobby) {
                gameListener.updatePing();
                break;
            }
    }

    /**
     * This method checks the pings of each game listener every <code>sleepTime</code> ms
     * and disconnects players whose game listener ping is too old
     */
    public void run(){
        while(!Thread.interrupted()) {
            List<GameListener> listeners = gameModel.getListeners();
            for (GameListener gameListener : listeners) {
                if (System.currentTimeMillis() - gameListener.getPing() > timeout) {
                    try {
                        disconnectPlayer(gameListener.getPlayer());
                        if(gameModel.countConnected()==0){
                            stopReconnectionTimer();
                            Lobby.getInstance().endGame(getGameId());
                            return;
                        }
                    } catch (InvalidPlayerException | ConnectionException | LobbyException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Disconnects the player corresponding to the game listener and starts the reconnection timer if there is only one player left
     * @param player Player to disconnect
     * @throws InvalidPlayerException if the player corresponding to gameListener is not one of the players of the match
     * @throws ConnectionException if the player had already been disconnected
     * @throws LobbyException if gameListener didn't belong to ListenerHandler
     */
    public void disconnectPlayer(PlayerLobby player) throws InvalidPlayerException, ConnectionException, LobbyException {
        gameModel.disconnectPlayer(player);
        if(gameModel.countConnected()==1 && gameModel.fetchGameStatus()!=null && reconnectionThread==null)
            startReconnectionTimer();
    }

    /**
     * Reconnects the player corresponding to the game listener and triggers the notification of this.
     * If more than one player is connected, it stops the reconnection timer
     * If two players are connected, it advances to the next turn.
     * Note that for a client to reconnect, they should invoke {@link Lobby}'s reconnection method
     * @param gameListener one of the listeners of ListenerHandler
     * @throws InvalidPlayerException if the player corresponding to gameListener is not one of the players of the match
     * @throws ConnectionException if the player was already connected when this method was called
     * @throws GameStatusException if any of the methods called directly or indirectly by this method are called in wrong game phase
     */
    void reconnectPlayer(GameListener gameListener) throws InvalidPlayerException, ConnectionException, GameStatusException {
        gameModel.reconnectPlayer(gameListener, this);
        //TODO: per una migliore gestione potresti spostare la notify qui invece che in model
        int numberConnectedPlayers=gameModel.countConnected();
        if(numberConnectedPlayers>1) {
            stopReconnectionTimer();
            if (numberConnectedPlayers==2 && (gameModel.fetchGameStatus()==GameStatus.IN_GAME && gameModel.fetchGameStatus()==GameStatus.FINAL_PHASE))
                nextTurn();
        }
    }

    /**
     * Starts the reconnection timer.
     * When it expires, it checks the number of connected players.
     * If there are none, it deletes the game.
     * If there is only one, it calculates the winner (who will be the only connected player) and then deletes the game.
     * Otherwise, it resets the timer by setting the reconnectionThread to null.
     */
    private void startReconnectionTimer(){
        reconnectionThread=new Thread(
                ()-> {
                    long startingtimer = System.currentTimeMillis();

                    while (reconnectionThread != null && !reconnectionThread.isInterrupted() && System.currentTimeMillis() - startingtimer < timeToWaitReconnection) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            //We can stop waiting since someone interrupted this thread
                        }
                    }
                    int numberConnectedPlayers=gameModel.countConnected();
                    if(numberConnectedPlayers==0){
                        try {
                            Lobby.getInstance().endGame(getGameId());
                        } catch (LobbyException e) {
                            throw new RuntimeException(e);
                        }
                    } else
                    {
                        if (numberConnectedPlayers==1) {
                            try {
                                gameModel.calcWinner();
                            } catch (GameStatusException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        else {
                            reconnectionThread=null;
                        }
                        try {
                            Lobby.getInstance().endGame(getGameId());
                        } catch (LobbyException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
    }

    /**
     * Stops the reconnection timer by setting the reconnection timer by interrupting the reconnection thread and then
     * setting it to null
     */
    private void stopReconnectionTimer(){
        if(reconnectionThread!=null){
            reconnectionThread.interrupt();
            reconnectionThread=null;
        }
    }

    /**
     * @return Unique number indicating the actual game model (hence the game/match to its all entirety)
     */
    public int getGameId(){
        return gameModel.getGameId();
    }

    /**
     * Method callable only once per player during INIT phase.
     * It plays the starter card of the given player on the passed side
     * @param player Player who has chosen which side of the starting card he wants to play
     * @param side The side of the starting card
     * @throws InvalidPlayerException If the player is not among the playing players in the match
     * @throws GameStatusException If game phase is not INIT
     * @throws InvalidPlayCardException Positioning error of the card at coordinates (0,0).
     */
    public synchronized void playStarter(PlayerLobby player, Side side) throws InvalidPlayerException, InvalidPlayCardException, GameStatusException {
        gameModel.playStarter(player,side);
    }

    /**
     * Method callable only during INIT phase. Sets the personal objective of the player according to his choice.
     * The objective card must be between the 2 possible cards given to the player. They can be retrieved with
     * <code>fetchPersonalObjectives(player)</code>
     * @param player one of the players of the match
     * @param cardObj the objective chosen by the player
     * @throws GameStatusException if this method isn't called in the INIT phase
     * @throws InvalidPlayerException if the player is not one of the players of this match
     * @throws InvalidChoiceException if the objective card does not belong to the list of the possible objective cards for the player
     * @throws VariableAlreadySetException if this method has been called before for the player
     */
    public synchronized void choosePersonalObjective(PlayerLobby player, CardObjectiveIF cardObj)
            throws InvalidPlayerException, InvalidChoiceException, VariableAlreadySetException, GameStatusException {
        gameModel.choosePersonalObjective(player, cardObj);
    }

    /**
     * Plays a given card side on the field, at the given coordinates
     * @param card Card which is being played. It must be in player's hand
     * @param side Indicates whether the card is going to be played on the front or on the back
     * @param coord Coordinates in the field of the player where the card is going to be positioned
     * @throws RequirementsNotMetException If the requirements for playing the specified card in player's field are not met
     * @throws InvalidPlayCardException If the player doesn't have the specified card
     * @throws GameStatusException If this method is called in the INIT or CALC POINTS phase
     * @throws InvalidPlayerException If the passed player is not the current player
     */
    public void playCard(PlayerLobby playerLobby, CardPlayableIF card, Side side, Coordinates coord)
            throws RequirementsNotMetException, InvalidPlayCardException, GameStatusException, InvalidPlayerException {
        if(!gameModel.fetchCurrentPlayer().equals(playerLobby))
            throw new InvalidPlayerException("The passed player is not the current player");
        gameModel.playCard(card, side, coord);
        if(gameModel.fetchPickables().stream().noneMatch(Objects::nonNull))
            try {
                pickCard(gameModel.fetchCurrentPlayer(), null);
            } catch (InvalidDrawCardException e){
                throw new RuntimeException();
            }
    }

    /**
     * Picks one of the 6 cards on the table, and makes the game proceed to the next Turn.
     * If there is no other turn to play after this one, it adds the objective points and calculates the winner.
     * @param card A playable card that should be in the field
     * @throws InvalidDrawCardException if the passed card is not on the table
     * @throws GameStatusException if this method is called in the INIT or CALC POINTS phase
     * @throws InvalidPlayerException If the passed player is not the current player
     */
    public void pickCard(PlayerLobby playerLobby, CardPlayableIF card) throws InvalidDrawCardException, GameStatusException, InvalidPlayerException {
        if(!gameModel.fetchCurrentPlayer().equals(playerLobby))
            throw new InvalidPlayerException("The passed player is not the current player");
        gameModel.pickCard(card);
        if(gameModel.countConnected()>1)
            nextTurn();
    }

    /**
     * Advances to the next turn.
     * If the game is finished, it also adds the objective points and calculates the winner.
     * If, after advancing to the next turn, the current player is not collected, it calls playCard and pickCard
     * with null parameters so that the game can proceed correctly
     * @throws GameStatusException if any of the methods it calls is called in the wrong game phase
     */
    private void nextTurn() throws GameStatusException {
        boolean hasNextTurn=gameModel.nextTurn();
        if(!hasNextTurn){
            gameModel.addObjectivePoints();
            gameModel.calcWinner();
        }
        try {
            if(!gameModel.fetchIsConnected(gameModel.fetchCurrentPlayer())){
                //playCard and pickCard should not throw any exception since the player is not connected
                //(so the methods are only called to make the game proceed, they won't actually change the state of the player or the table)
                playCard(gameModel.fetchCurrentPlayer(),null,null,null);
                pickCard(gameModel.fetchCurrentPlayer(),null);
            }
        } catch (InvalidPlayerException | RequirementsNotMetException | InvalidPlayCardException | InvalidDrawCardException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @return the players of the match
     */
    public List<PlayerLobby> getPlayers(){
        return gameModel.fetchPlayersLobby();
    }
}
