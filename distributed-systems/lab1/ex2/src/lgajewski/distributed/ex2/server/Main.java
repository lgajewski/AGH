package lgajewski.distributed.ex2.server;

import java.io.IOException;

public class Main {

    private static final String TMP_DIR = "tmp";
    private static final int PORT = 1234;

    private static final Server.FileDuplicatePolicy POLICY = Server.FileDuplicatePolicy.REPLACE;

    public static void main(String[] args) throws IOException {
        Server server = new Server(TMP_DIR, PORT, POLICY);
        while (server.isRunning()) {
            try {
                server.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}