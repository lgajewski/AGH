package lgajewski.distributed.ex3.chat;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatApp {
    private static final String HOST = "224.0.0.1";
    private static final int PORT = 7777;

    private final String username;

    private ExecutorService executors;
    private InetAddress inetAddress;

    public ChatApp(String username) throws UnknownHostException {
        this.executors = Executors.newFixedThreadPool(2);
        this.username = shorten(username, Message.MAX_USERNAME);
        this.inetAddress = InetAddress.getByName(HOST);
    }

    public void start() {
        executors.execute(new MsgSender(inetAddress, PORT, username));
        executors.execute(new MsgReceiver(inetAddress, PORT, username));

        System.out.println("[chat] running...");
    }

    public static String shorten(String string, int maxLength) {
        if (string.length() < maxLength) {
            return string;
        } else {
            return string.substring(0, maxLength);
        }
    }

}