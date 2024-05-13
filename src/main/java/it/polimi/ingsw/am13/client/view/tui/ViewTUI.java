package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.view.View;
import it.polimi.ingsw.am13.client.view.tui.menu.MenuItem;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewTUI implements View {

    ViewTUIStartup viewStartup;
    ViewTUILobby viewLobby;
    ViewTUIRoom viewRoom;
    ViewTUIMatch viewMatch;

    Map<String, MenuItem> currentMenu;

    public ViewTUI() {
        this.viewStartup = new ViewTUIStartup();
        this.viewLobby = new ViewTUILobby();
        this.viewRoom = new ViewTUIRoom();
        this.viewMatch = new ViewTUIMatch();
        this.currentMenu = new HashMap<>();
    }

    @Override
    public void showStartupScreen(boolean isTUI, boolean isSocket, String ip, int port) {
        viewStartup.updateStartup(isTUI, isSocket, ip, port);
        this.currentMenu = viewStartup.getMenu();
    }

    @Override
    public void showException(Exception e) {

    }

    @Override
    public void showGenericLogMessage(String msg) {

    }

    @Override
    public void showRooms(List<RoomIF> rooms) {
        viewLobby.printRooms(rooms);
    }

    @Override
    public void showJoinedRoom() {
        viewRoom.printJoinedRoom();
    }
    @Override
    public void showPlayerJoinedRoom(PlayerLobby player) {
        viewRoom.printPlayerJoinedRoom(player);
    }

    @Override
    public void showPlayerLeftRoom(PlayerLobby player) {
        viewRoom.printPlayerLeftRoom(player);
    }

    @Override
    public void showLeftRoom() {
        viewLobby.printLeftRoom();
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
