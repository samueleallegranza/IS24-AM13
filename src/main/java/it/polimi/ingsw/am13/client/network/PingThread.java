package it.polimi.ingsw.am13.client.network;

import it.polimi.ingsw.am13.ParametersClient;

/**
 * This class contains a thread that regularly sends pings to the server (by calling the ping method
 * in its {@link NetworkHandler} attribute)
 */
public class PingThread{
    /**
     * Network handler associated to this client, which is used to send pings
     */
    private final NetworkHandler networkHandler;

    /**
     * The thread that regularly sends a ping
     */
    private final Thread pingThread;

    /**
     * Creates a class, by setting the networkHandler and starting the pingThread
     * @param networkHandler associated to this client, which is used to send pings
     */
    public PingThread(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
        pingThread = new Thread(() -> {
            while(!Thread.interrupted()) {
                this.networkHandler.ping();
                try {
                    Thread.sleep(ParametersClient.sleepTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        pingThread.start();
    }

    /**
     * Interrupts the ping thread
     */
    public void stopPing() {
        pingThread.interrupt();
    }
}
