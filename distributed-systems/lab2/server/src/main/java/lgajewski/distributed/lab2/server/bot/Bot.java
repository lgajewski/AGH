package lgajewski.distributed.lab2.server.bot;

import lgajewski.distributed.lab2.common.IEventListener;
import lgajewski.distributed.lab2.server.User;

public class Bot extends User {

    public Bot(String nick, IEventListener listener) {
        super(nick, listener);
    }

}
