package it.polimi.ingsw.am13.client.network;

import it.polimi.ingsw.am13.ParametersClient;

public class PingThread{
    private final NetworkHandler networkHandler;

    private final Thread pingThread;

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

    public void stopPing() {
        pingThread.interrupt();
    }
}
