package pl.gajewski;

import pl.gajewski.server.ThreadPooledServer;

public class Main {

    private static final int UPTIME_SEC = 3600;

    public static void main(String[] args) {
        ThreadPooledServer threadPooledServer = new ThreadPooledServer(8080);
        Thread serverThread = new Thread(threadPooledServer);
        serverThread.start();

        try {
            Thread.sleep(UPTIME_SEC * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Stopping Server..");
        threadPooledServer.stop();
    }
}
