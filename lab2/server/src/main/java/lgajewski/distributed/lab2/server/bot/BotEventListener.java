package lgajewski.distributed.lab2.server.bot;

import lgajewski.distributed.lab2.common.GameState;
import lgajewski.distributed.lab2.common.IEventListener;
import lgajewski.distributed.lab2.common.IGameBoard;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class BotEventListener extends UnicastRemoteObject implements IEventListener {

    private final IGameBoard gameBoard;
    private final String nick;

    public BotEventListener(IGameBoard iGameBoard, String nick) throws RemoteException {
        this.gameBoard = iGameBoard;
        this.nick = nick;
    }

    @Override
    public void onRegistered() throws RemoteException {
    }

    @Override
    public void onJoinedLobby() throws RemoteException {
    }

    @Override
    public void onGameStarted() throws RemoteException {
    }

    @Override
    public void onGameMove() throws RemoteException {
        int row = 0;
        int col = 0;
        do {
            row = new Random().nextInt(3) + 1;
            col = new Random().nextInt(3) + 1;
        } while (!gameBoard.isValidMove(nick, row, col));


        System.out.println(nick + ": setSeed [" + row + "," + col + "]");
        gameBoard.setSeed(nick, row, col);
    }

    @Override
    public void onBoardUpdated() throws RemoteException {
    }

    @Override
    public void onGameFinished(GameState gameState) throws RemoteException {
        System.out.println("bot status: " + gameState);
    }
}