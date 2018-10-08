package lgajewski.distributed.lab2.client;

import lgajewski.distributed.lab2.common.GameState;
import lgajewski.distributed.lab2.common.IEventListener;
import lgajewski.distributed.lab2.common.IGameBoard;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;


public class EventListener extends UnicastRemoteObject implements IEventListener {

    private static final Scanner in = new Scanner(System.in);

    private final IGameBoard gameBoard;
    private final String nick;

    protected EventListener(IGameBoard iGameBoard, String nick) throws RemoteException {
        this.gameBoard = iGameBoard;
        this.nick = nick;
    }

    public void playerMove() throws RemoteException {
        boolean valid = false;
        try {
            do {
                System.out.print("enter your move (row[1-3] column[1-3]): ");
                int row, col;
                row = in.nextInt() - 1;
                col = in.nextInt() - 1;
                if (gameBoard.isValidMove(nick, row, col)) {
                    gameBoard.setSeed(nick, row, col);
                    valid = true;
                } else {
                    System.out.println("This move at (" + (row + 1) + "," + (col + 1) + ") is not valid. Try again...");
                }
            } while (!valid);
        } catch (Exception e) {
            System.err.println("error: incorrect input.. exiting");
            System.exit(-1);
        }
    }

    @Override
    public void onRegistered() throws RemoteException {
        System.out.println("User registered!");

        System.out.println("[1] Wait for opponent...");
        System.out.println("[2] Play with a computer");
        Scanner scanner = new Scanner(System.in);
        int option;

        try {
            option = scanner.nextInt();
        } catch (Exception e) {
            option = 3;
        }
        switch (option) {
            case 1:
            case 2:
                gameBoard.selectMode(nick, option);
                break;
            default:
                System.out.println("Unrecognized option! Try again..");
                System.exit(0);
        }
    }

    @Override
    public void onJoinedLobby() throws RemoteException {
        System.out.println("Joined lobby. Waiting for an opponent..");
    }

    @Override
    public void onGameStarted() throws RemoteException {
        System.out.println("Game just started!");
        System.out.println(gameBoard.paint(nick));
    }

    @Override
    public void onGameMove() throws RemoteException {
        playerMove();
    }

    @Override
    public void onBoardUpdated() throws RemoteException {
        System.out.println(gameBoard.paint(nick));
    }

    @Override
    public void onGameFinished(GameState gameState) throws RemoteException {
        System.out.println("Game finished! " + gameState);
        System.exit(0);
    }
}
