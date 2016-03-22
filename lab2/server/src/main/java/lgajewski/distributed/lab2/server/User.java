package lgajewski.distributed.lab2.server;

import lgajewski.distributed.lab2.common.IEventListener;

public class User {

    private String nick;
    private IEventListener listener;

    private User opponent;

    private Seed seed = Seed.CROSS;

    public User(String nick, IEventListener listener) {
        this.nick = nick;
        this.listener = listener;
    }

    public Seed getSeed() {
        return seed;
    }

    public void setSeed(Seed seed) {
        this.seed = seed;
    }

    public String getNick() {
        return nick;
    }

    public User getOpponent() {
        return opponent;
    }

    public void setOpponent(User opponent) {
        this.opponent = opponent;
    }

    public boolean hasOpponent() {
        return opponent != null;
    }

    public IEventListener getListener() {
        return listener;
    }

}
