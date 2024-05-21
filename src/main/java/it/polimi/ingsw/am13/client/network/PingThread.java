package it.polimi.ingsw.am13.client.network;

public class PingThread{
    private static final Long sleepTime = 500L;
    private final NetworkHandler networkHandler;

    private final Thread pingThread;

    public PingThread(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
        pingThread = new Thread(() -> {
            while(!Thread.interrupted()) {
                this.networkHandler.ping();
                try {
                    Thread.sleep(sleepTime);
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
