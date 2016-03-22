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
            IGameBoard nb = (IGameBoard) Naming.lookup(name);

            String nick = args[2];

            IEventListener listener = new EventListener(nb, nick);
            nb.register(nick, listener);

        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
