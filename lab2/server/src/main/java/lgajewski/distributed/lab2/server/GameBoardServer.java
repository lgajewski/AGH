package lgajewski.distributed.lab2.server;

import lgajewski.distributed.lab2.common.IGameBoard;

import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

public class GameBoardServer {

    public static void main(String[] args) {
        try {
            if (args.length != 2) {
                System.out.println("Usage: java GameBoardServer <host> <port>");
                System.exit(-1);
            }

            String name = "rmi://" + args[0] + ":" + args[1] + "/game";

            GameBoardImpl nbi = new GameBoardImpl();
            IGameBoard nb = (IGameBoard) UnicastRemoteObject.exportObject(nbi, 0);
            Naming.rebind(name, nb);
            System.out.println("Listening..");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
