package it.polimi.ingsw.am13;

/**
 * Class storing all the major parameters the server classes use, both for game and technical aspects.
 * The non-final values are parametric and could be chosen via command line, the value here are the default ones
 */
public class ParametersServer {

    // CONTROLLER (ping, timeout handling)

    /**
     * Constant storing the timeout used to detect the disconnection of a client (in ms)
     */
    public static Long timeout = 4000L;
    /**
     * Constant storing the sleep time in ms of the threads managing connections (i.e. how often they repeat the checks)
     */
    public static Long sleepTime = 500L;
    /**
     * How much time (in ms) the controller waits for a client to reconnect when only 1 player or fewer are connected
     */
    public static Long timeToWaitReconnection = 60000L;
    /**
     * Flag indication if a player who remained alone must be declared winner
     * Debug purposes only
     */
    public static boolean alonePlayerWin = true;
    /**
     * Flag indicating if the server must check the pings of the clients.
     * Debug purposes only
     */
    public static boolean checkPings = true;

    // NETWORK (ip, port)

    /**
     * IP Addressed of the server
     */
    public static String SERVER_IP = "localhost";
    /**
     * Socket port number
     */
    public static int SOCKET_PORT = 25566;
    /**
     * RMI port number
     */
    public static int RMI_PORT = 25567;
    /**
     * RMI name for bind of remote interface {@link it.polimi.ingsw.am13.network.rmi.LobbyRMIIF}
     */
    public static String LOBBY_RMI_NAME = "lobby_rmi";

    // GAME

    /**
     * Number of points that, when first reached by a player, triggers the final phase
     */
    public static int POINTS_FOR_FINAL_PHASE = 20;
    /**
     * If true (default) the requirements for gold cards are checked. Otherwise, the card can always be placed in field
     */
    public static boolean CHECK_REQUIREMENTS = true;

}
