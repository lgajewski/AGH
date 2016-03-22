package lgajewski.distributed.lab2.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class GameBoardServer {

    public static void main(String[] args) {
        try {
            if (args.length != 2) {
                System.out.println("Usage: java GameBoardServer <host> <port>");
                System.exit(-1);
            }

            String name = "rmi://" + args[0] + ":" + args[1] + "/game";
            LocateRegistry.createRegistry(Integer.parseInt(args[1]));
            GameBoardImpl nbi = new GameBoardImpl();
            Naming.rebind(name, nbi);
            System.out.println("Listening..");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
