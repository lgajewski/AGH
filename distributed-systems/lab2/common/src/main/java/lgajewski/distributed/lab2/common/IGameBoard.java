package lgajewski.distributed.lab2.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGameBoard extends Remote {

    void register(String user, IEventListener listener) throws RemoteException;

    void selectMode(String user, int option) throws RemoteException;

    String paint(String user) throws RemoteException;

    boolean isValidMove(String user, int row, int col) throws RemoteException;

    void unregister(String user) throws RemoteException;

    void setSeed(String user, int row, int col) throws RemoteException;
}
