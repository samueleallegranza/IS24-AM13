package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.view.View;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;

public class ViewTUIHandler implements View {
    ViewTUI currentView;

    public void setCurrentView(ViewTUI currentView) {
        this.currentView = currentView;
    }

    @Override
    public void showStartupScreen(boolean isTUI, boolean isSocket, String ip, int port) {
        setCurrentView(new ViewTUIStartup(isTUI, isSocket, ip, port));
        this.currentView.printView();
    }

    @Override
    public void showException(Exception e) {

    }

    @Override
    public void showGenericLogMessage(String msg) {

    }

    @Override
    public void showRooms(List<RoomIF> rooms) {

    }

    @Override
    public void showPlayerJoinedRoom(PlayerLobby playerLobby) {

    }

    @Override
    public void showPlayerLeftRoom(PlayerLobby player) {

    }

    @Override
    public void showStartGame(GameState state) {

    }

    @Override
    public void showStartGameReconnected(GameState state) {

    }

    @Override
    public void showPlayedStarter(PlayerLobby player) {

    }

    @Override
    public void showChosenPersonalObjective(PlayerLobby player) {

    }

    @Override
    public void showInGame() {

    }

    @Override
    public void showPlayedCard(PlayerLobby player, Coordinates coord) {

    }

    @Override
    public void showPickedCard(PlayerLobby player) {

    }

    @Override
    public void showNextTurn() {

    }

    @Override
    public void showFinalPhase() {

    }

    @Override
    public void showUpdatePoints() {

    }

    @Override
    public void showWinner() {

    }

    @Override
    public void showEndGame() {

    }

    @Override
    public void showPlayerDisconnected(PlayerLobby player) {

    }

    @Override
    public void showPlayerReconnected(PlayerLobby player) {

    }
}
