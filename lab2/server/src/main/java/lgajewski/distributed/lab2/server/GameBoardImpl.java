package lgajewski.distributed.lab2.server;

import lgajewski.distributed.lab2.common.GameState;
import lgajewski.distributed.lab2.common.IEventListener;
import lgajewski.distributed.lab2.common.IGameBoard;
import lgajewski.distributed.lab2.server.bot.Bot;
import lgajewski.distributed.lab2.server.bot.BotEventListener;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameBoardImpl extends UnicastRemoteObject implements IGameBoard {

    private Map<String, User> users;

    protected GameBoardImpl() throws RemoteException {
        this.users = new ConcurrentHashMap<>();
    }

    @Override
    public void register(String nick, IEventListener listener) throws RemoteException {
        System.out.println("[GameBoard] user registered: " + nick);

        User newUser = new User(nick, listener);
        users.put(nick, newUser);

        listener.onRegistered();
    }

    private void findOpponent(User lastUser, User newUser) throws RemoteException {
        if (lastUser != null && !lastUser.hasOpponent() && !lastUser.getNick().equals(newUser.getNick())) {
            // start a game
            lastUser.setOpponent(newUser);
            newUser.setOpponent(lastUser);

            Board board = new Board();
            lastUser.setBoard(board);
            newUser.setBoard(board);

            lastUser.setSeed(Seed.CROSS);
            newUser.setSeed(Seed.NOUGHT);

            lastUser.getListener().onGameStarted();
            newUser.getListener().onGameStarted();

            lastUser.getListener().onGameMove();
        } else {
            newUser.getListener().onJoinedLobby();
        }
    }

    @Override
    public void selectMode(String nick, int option) throws RemoteException {
        System.out.println("[GameBoard] user selected mode: " + option);
        User user = users.get(nick);
        if (user != null) {
            switch (option) {
                case 1:
                    User lastUser = getWaitingUser();
                    findOpponent(lastUser, user);
                    break;
                case 2:
                    Bot bot = new Bot("bot-" + nick, new BotEventListener(this, "bot-" + nick));
                    users.put(bot.getNick(), bot);
                    findOpponent(bot, user);
                    break;
            }
        }
    }

    private User getWaitingUser() {
        for (User user : users.values()) {
            if (!user.hasOpponent()) {
                return user;
            }
        }

        return null;
    }

    @Override
    public String paint(String nick) throws RemoteException {
        User user = users.get(nick);
        if (user != null) {
            return user.getBoard().paint();
        }
        return "<>";
    }

    @Override
    public boolean isValidMove(String nick, int row, int col) throws RemoteException {
        User user = users.get(nick);
        return user != null && user.getBoard().isValidMove(row, col);
    }

    @Override
    public void unregister(String nick) throws RemoteException {
        System.out.println("[GameBoard] user unregistered: " + nick);
        User user = users.get(nick);

        if (user == null) return;

        if (user.hasOpponent()) {
            user.getOpponent().getListener().onGameFinished(GameState.DRAW);
            user.getOpponent().setOpponent(null);
            user.getOpponent().setBoard(null);
        }

        users.remove(nick);
    }

    @Override
    public void setSeed(String nick, int row, int col) throws RemoteException {
        System.out.println("[GameBoard] user set seed: " + row + ", " + col);
        User user = users.get(nick);

        if (user == null) return;

        Board board = user.getBoard();
        board.setSeed(row, col, user.getSeed());

        // notify each user
        user.getListener().onBoardUpdated();
        user.getOpponent().getListener().onBoardUpdated();

        if (board.getGameState() != GameState.PLAYING) {
            user.getListener().onGameFinished(board.getGameState());
            user.getOpponent().getListener().onGameFinished(board.getGameState());
        } else {
            // notify another user
            user.getOpponent().getListener().onGameMove();
        }


    }


}
