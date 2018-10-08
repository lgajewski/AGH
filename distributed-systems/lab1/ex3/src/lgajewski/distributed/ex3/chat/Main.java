package lgajewski.distributed.ex3.chat;

import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) throws UnknownHostException {
        if (args.length != 1) {
            System.out.println("Usage: <username>");
            return;
        }

        new ChatApp(args[0]).start();
    }
}
