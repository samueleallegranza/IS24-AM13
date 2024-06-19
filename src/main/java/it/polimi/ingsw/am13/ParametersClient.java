package it.polimi.ingsw.am13;

/**
 * Class storing all the major parameters the client classes use, both for game and technical aspects.
 * The non-final values are parametric and could be chosen via command line, the value here are the default ones
 */
public class ParametersClient {

    // PING

    /**
     * Period the client thread waits before sending a new ping
     */
    public static final Long sleepTime = 500L;

    // DEBUG

    /**
     * If true, skips the create/join room phase, creating a room with the specifiec DEBUG_NPLAYERS number of players
     */
    public static boolean SKIP_ROOM = false;
    /**
     * If true, skips the init phase
     */
    public static boolean SKIP_INIT = false;
    /**
     * If true, skips the turn-bases phase, moving directly to the end of the game
     */
    public static boolean SKIP_TURNS = false;
    /**1
     * Number of players for the debug mode
     */
    public static int DEBUG_NPLAYERS = 2;

    // NETWORK

    /**
     * True if client chooses to connect via socket, false if it chooses RMI
     */
    public static boolean IS_SOCKET = true;
    /**
     * Ture if the client chooses to use TUI, false if it chooses GUI
     */
    public static boolean IS_TUI = false;
    /**
     * Server's ip (to which to connect)
     */
    public static String  SERVER_IP = "localhost";
    /**
     * Server's port (to which to connect)
     */
    public static int SERVER_PORT = -1;

    private static final int  SOCKET_DEFAULT_PORT = 25566;
    private static final int RMI_DEFAULT_PORT = 25567;

    /**
     * Checks if the server's port was chosen, and sets the default port if it was not.
     * It should be called after IS_SOCKET is definitively set
     */
    public static void checkServerPort() {
        if(SERVER_PORT == -1)
            SERVER_PORT = IS_SOCKET ? SOCKET_DEFAULT_PORT : RMI_DEFAULT_PORT;
    }

    // VARIOUS

    public static boolean SOUND_ENABLE = true;
}
