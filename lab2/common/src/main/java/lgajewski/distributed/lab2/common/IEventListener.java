package lgajewski.distributed.lab2.common;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IEventListener extends Remote {
    void onRegistered() throws RemoteException;

    void onJoinedLobby() throws RemoteException;

    void onGameStarted() throws RemoteException;

    void onGameMove() throws RemoteException;

    void onBoardUpdated() throws RemoteException;

    void onGameFinished(GameState gameState) throws RemoteException;

}
