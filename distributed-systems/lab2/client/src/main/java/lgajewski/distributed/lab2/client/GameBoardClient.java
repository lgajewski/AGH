package lgajewski.distributed.lab2.client;

import lgajewski.distributed.lab2.common.IEventListener;
import lgajewski.distributed.lab2.common.IGameBoard;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class GameBoardClient {

    public static void main(String[] args) {
        try {
            if (args.length != 3) {
                System.out.println("Usage: java GameBoardClient <host> <port> <nick>");
                System.exit(-1);
            }

            String name = "rmi://" + args[0] + ":" + args[1] + "/game";
            final IGameBoard nb = (IGameBoard) Naming.lookup(name);

            final String nick = args[2];

            final IEventListener listener = new EventListener(nb, nick);
            nb.register(nick, listener);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    System.out.println("\ncleanup ...");
                    try {
                        nb.unregister(nick);
                    } catch (RemoteException e) {
                        System.out.println("server already terminated");
                    }
                }
            });

        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}
