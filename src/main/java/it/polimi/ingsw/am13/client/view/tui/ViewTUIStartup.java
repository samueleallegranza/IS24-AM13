package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.view.tui.menu.MenuItem;

import java.util.HashMap;
import java.util.Map;

public class ViewTUIStartup {
    private boolean isTUI;
    private boolean isSocket;
    private String ip;
    private int port;

    private final Map<String, MenuItem> menu;

    // constructor
    public ViewTUIStartup() {
        this.menu = new HashMap<>(); // empty because startup has no menu
    }

    public Map<String, MenuItem> getMenu() {
        return this.menu;
    }


    public void updateStartup(boolean isTUI, boolean isSocket, String ip, int port) {
        this.isTUI = isTUI;
        this.isSocket = isSocket;
        this.ip = ip;
        this.port = port;
        this.printView();
    }

    private void printView() {
        String options = String.format(
                "\t> %s Mode\n" +
                "\t> %s Connection type\n" +
                "\t> Server address: %s:%d\n",
                isTUI ? "TUI" : "GUI",
                isSocket ? "Socket" : "RMI",
                ip, port
        );

        System.out.print(
                "\n" +
                "      ...                         ..                               \n" +
                "   xH88\"`~ .x8X                 dF                                 \n" +
                " :8888   .f\"8888Hf        u.   '88bu.                    uL   ..   \n" +
                ":8888>  X8L  ^\"\"`   ...ue888b  '*88888bu        .u     .@88b  @88R \n" +
                "X8888  X888h        888R Y888r   ^\"*8888N    ud8888.  '\"Y888k/\"*P  \n" +
                "88888  !88888.      888R I888>  beWE \"888L :888'8888.    Y888L     \n" +
                "88888   %88888      888R I888>  888E  888E d888 '88%\"     8888     \n" +
                "88888 '> `8888>     888R I888>  888E  888E 8888.+\"        `888N    \n" +
                "`8888L %  ?888   ! u8888cJ888   888E  888F 8888L       .u./\"888&   \n" +
                " `8888  `-*\"\"   /   \"*888*P\"   .888N..888  '8888c. .+ d888\" Y888*\" \n" +
                "   \"888.      :\"      'Y\"       `\"888*\"\"    \"88888%   ` \"Y   Y\"    \n" +
                "     `\"\"***~\"`                     \"\"         \"YP'                 \n" +
                "                                                                   \n" +
                "Authors:  Samuele Allegranza, Matteo Arrigo,\n" +
                "          Lorenzo Battini, Federico Bulloni\n\n" +
                "Startup options:\n"+ options + "\n"
        );
    }
}
