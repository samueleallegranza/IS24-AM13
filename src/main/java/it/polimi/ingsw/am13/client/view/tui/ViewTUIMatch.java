package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.view.tui.menu.MenuItem;
import it.polimi.ingsw.am13.model.card.CardSidePlayableIF;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;
import java.util.Map;

public class ViewTUIMatch implements ViewTuiMenuDisplayer {

    private GameState gameState;
    private PlayerLobby thisPlayer;

    public ViewTUIMatch() {
        this.gameState = null;
    }

    @Override
    public Map<String, MenuItem> getMenu() {
        return null;
    }

    public void printMatch() {
        // print player header
        System.out.println(sectionPlayers());
    }

    // --------------------------------------------------------------------
    // ----------------------------- SECTIONS -----------------------------
    // --------------------------------------------------------------------

    public String sectionPlayers() {
        return "";
    }


    // --------------------------------------------------------------------
    // ------------------------------ UTILS -------------------------------
    // --------------------------------------------------------------------

}
