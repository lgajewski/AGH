package lgajewski.distributed.ex1.server;

import java.io.IOException;

public class Main {

    private static final int PORT = 31415;

    public static void main(String[] args) throws IOException {
        Server server = new Server(PORT);
        while (server.isRunning()) {
            try {
                server.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}