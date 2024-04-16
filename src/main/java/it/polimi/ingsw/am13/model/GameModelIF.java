package it.polimi.ingsw.am13.model;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.GameStatusException;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.am13.model.player.FieldIF;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Immutable version of {@link GameModel} class. <br>
 * It is used to provide a read-only version of the game status to the view,
 * by providing only the necessary methods to fetch the data.
 */
public interface GameModelIF {

    int getGameId();

    List<PlayerLobby> fetchPlayers();

    PlayerLobby fetchFirstPlayer();

    PlayerLobby fetchCurrentPlayer();

    GameStatus fetchGameStatus();

    Map<PlayerLobby, Integer> fetchPoints();

    List<CardPlayableIF> fetchPickables();
    List<Optional<? extends CardPlayableIF>> fetchPickablesOptional();

    List<CardObjectiveIF> fetchCommonObjectives();

    List<CardObjectiveIF> fetchPersonalObjectives(PlayerLobby player) throws InvalidPlayerException, GameStatusException;

    CardStarterIF fetchStarter(PlayerLobby player) throws InvalidPlayerException;

    List<CardPlayableIF> fetchHandPlayable(PlayerLobby player) throws InvalidPlayerException;

    CardObjectiveIF fetchHandObjective(PlayerLobby player) throws InvalidPlayerException;

    List<Coordinates> fetchAvailableCoord(PlayerLobby player) throws InvalidPlayerException;

    FieldIF fetchPlayerField(PlayerLobby player) throws InvalidPlayerException;

}
