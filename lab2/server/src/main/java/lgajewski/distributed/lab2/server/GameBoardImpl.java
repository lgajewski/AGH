package lgajewski.distributed.lab2.server;

import lgajewski.distributed.lab2.common.GameState;
import lgajewski.distributed.lab2.common.IEventListener;
import lgajewski.distributed.lab2.common.IGameBoard;
import lgajewski.distributed.lab2.server.bot.Bot;
import lgajewski.distributed.lab2.server.bot.BotEventListener;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameBoardImpl implements IGameBoard {

    private Map<String, Board> boardMap;
    private List<User> users;

    public GameBoardImpl() {
        this.boardMap = new ConcurrentHashMap<>();
        this.users = new ArrayList<>();
    }

    @Override
    public void register(String nick, IEventListener listener) throws RemoteException {
        System.out.println("[GameBoard] user registered: " + nick);

        User newUser = new User(nick, listener);
        users.add(newUser);

        newUser.getListener().onRegistered();
    }

    private void findOpponent(User lastUser, User newUser) throws RemoteException {
        if (lastUser != null && !lastUser.hasOpponent()) {
            // start a game
            lastUser.setOpponent(newUser);
            newUser.setOpponent(lastUser);

            boardMap.put(newUser.getNick(), boardMap.get(lastUser.getNick()));

            lastUser.getListener().onGameStarted();
            newUser.getListener().onGameStarted();

            lastUser.setSeed(Seed.CROSS);
            newUser.setSeed(Seed.NOUGHT);

            firstMove(lastUser);
        } else {
            // join a lobby
            boardMap.put(newUser.getNick(), new Board());

            newUser.getListener().onJoinedLobby();
        }
    }

    @Override
    public void selectMode(String nick, int option) throws RemoteException {
        User user = null;
        for (User u : users) {
            if (u.getNick().equals(nick)) {
                user = u;
            }
        }
        if (user != null) {
            switch (option) {
                case 1:
                    User lastUser = getWaitingUser();
                    findOpponent(lastUser, user);
                    break;
                case 2:
                    Bot bot = new Bot("bot-" + nick, new BotEventListener(this, "bot-" + nick));
                    users.add(bot);
                    boardMap.put(bot.getNick(), new Board());
                    findOpponent(bot, user);
                    break;
            }
        }
    }

    private void firstMove(User user) throws RemoteException {
        user.getListener().onGameMove();
    }

    private User getWaitingUser() {
        if (boardMap.isEmpty()) return null;

        for (User user : users) {
            if (!user.hasOpponent()) {
                return user;
            }
        }

        return null;
    }

    @Override
    public String paint(String nick) throws RemoteException {
        for (User user : users) {
            if (user.getNick().equals(nick)) {
                return boardMap.get(user.getNick()).paint();
            }
        }
        return null;
    }

    @Override
    public boolean isValidMove(String nick, int row, int col) throws RemoteException {
        for (User user : users) {
            if (user.getNick().equals(nick)) {
                return boardMap.get(user.getNick()).isValidMove(row, col);
            }
        }
        return false;
    }

    @Override
    public void unregister(String nick) throws RemoteException {
        System.out.println("[GameBoard] user unregistered: " + nick);
        User u = null;
        for (User user : users) {
            if (user.getOpponent() != null && user.getOpponent().getNick().equals(nick)) {
                u = user.getOpponent();
                user.setOpponent(null);
            }
        }

        if (u != null) {
            users.remove(u);
        }

        boardMap.remove(nick);
    }

    @Override
    public void setSeed(String nick, int row, int col) throws RemoteException {
        for (User user : users) {
            if (user.getNick().equals(nick)) {
                Board board = boardMap.get(user.getNick());
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


    }


}
