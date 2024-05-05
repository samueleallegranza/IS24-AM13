package it.polimi.ingsw.am13.network.rmi;
import it.polimi.ingsw.am13.client.network.rmi.GameListenerClientRMI;
import it.polimi.ingsw.am13.controller.GameController;
import it.polimi.ingsw.am13.controller.GameListener;
import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.CardStarterIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

//TODO: scrivi documentazione
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
    public void updateGameBegins(GameController controller) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                GameControllerRMI controllerRMI = new GameControllerRMI(controller, clientLis.getPlayer());
                clientLis.updateGameBegins(controllerRMI);
                break;
            } catch (RemoteException e) {
                cnt++;
            } catch (InvalidPlayerException e) {
                //TODO: capisci se gestire meglio questa eccezione
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void updateStartGame(GameModelIF model) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updateStartGame(model);
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updatePlayedStarter(player, cardStarter);
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updateChosenPersonalObjective(PlayerLobby player) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updateChosenPersonalObjective(player);
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
    public void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayable, Side side, Coordinates coord, int points, List<Coordinates> availableCoords) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updatePlayedCard(player, cardPlayable, side, coord, points, availableCoords);
                break;
            } catch (RemoteException e) {
                cnt++;
            }
        }
    }

    @Override
    public void updatePickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                clientLis.updatePickedCard(player, updatedVisibleCards);
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
    public void updateGameModel(GameModelIF model, GameController controller) {
        int cnt = 0;
        while (cnt < NTRIES) {
            try {
                GameControllerRMI controllerRMI = new GameControllerRMI(controller, clientLis.getPlayer());
                clientLis.updateGameModel(model, controllerRMI);
                break;
            } catch (RemoteException e) {
                cnt++;
            } catch (InvalidPlayerException e) {
                //TODO capisci se gestire meglio questa eccezione
                throw new RuntimeException(e);
            }
        }
    }
}
