package it.polimi.ingsw.am13.network.rmi;
import it.polimi.ingsw.am13.client.network.rmi.GameListenerClientRMI;
import it.polimi.ingsw.am13.controller.GameController;
import it.polimi.ingsw.am13.controller.GameListener;
import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a game listener for RMI connection.
 * For each type of update, it tries asynchronously to call the corresponding method of {@link GameListenerClientRMI},
 * that is in the client (RMI remote call), for a fixed maximum number of times
 */
public class GameListenerServerRMI implements GameListener {

    // TODO: può succedere che anche se mi arrivano i ping (non disconnetto il player), non riesco a inviare l'update?
    // TODO: in generale pensa meglio a come gestire le RemoteException


    //TODO: fai fare update a nuovo Thread, per problema di latenza

    private static final int NTRIES = 4;
    
    private long ping;
    private final GameListenerClientRMI clientLis;
    
    public GameListenerServerRMI(GameListenerClientRMI clientLis) {
        this.clientLis = clientLis;
        ping = System.currentTimeMillis();
    }

    @Override
    public PlayerLobby getPlayer() {
        return null;
    }

    @Override
    public Long getPing() {
        return ping;
    }


    @Override
    public void updatePing() {
        ping = System.currentTimeMillis();
    }
    
    @Override
    public void updatePlayerJoinedRoom(PlayerLobby player) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updatePlayerJoinedRoom(player);
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updatePlayerLeftRoom(PlayerLobby player) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updatePlayerLeftRoom(player);
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updateStartGame(GameModelIF model, GameController controller) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updateStartGame(model, controller);
                break;
            } catch (RemoteException e) {
                cnt++;
            } catch (InvalidPlayerException e) {
                //It should not happen
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter, List<Coordinates> availableCoords) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updatePlayedStarter(player, cardStarter, availableCoords);
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updateChosenPersonalObjective(PlayerLobby player, CardObjectiveIF chosenObj) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updateChosenPersonalObjective(player, chosenObj);
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updateNextTurn(PlayerLobby player) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updateNextTurn(player);
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayable, Coordinates coord, int points, List<Coordinates> availableCoords) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updatePlayedCard(player, cardPlayable, coord, points, availableCoords);
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updatePickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards, CardPlayableIF pickedCard) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updatePickedCard(player, new ArrayList<>(updatedVisibleCards), pickedCard);
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updatePoints(Map<PlayerLobby, Integer> pointsMap) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updatePoints(pointsMap);
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updateWinner(PlayerLobby winner) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updateWinner(winner);
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updateEndGame() {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updateEndGame();
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updatePlayerDisconnected(PlayerLobby player) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updatePlayerDisconnected(player);
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updatePlayerReconnected(PlayerLobby player) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updatePlayerReconnected(player);
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updateFinalPhase() {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updateFinalPhase();
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updateInGame() {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updateInGame();
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updateGameModel(GameModelIF model) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updateGameModel(model);
                break;
            } catch (RemoteException e) {
                cnt++;
            } catch (InvalidPlayerException e) {
                // It should never happen
                //TODO ripensaci
                throw new RuntimeException(e);
            }
        }
    }
}
