package lgajewski.distributed.lab4.server.locator;

import Ice.*;
import Ice.Object;
import lgajewski.distributed.lab4.impl.UserI;

import java.util.logging.Logger;

public class ServantLocator2 implements ServantLocator {

    private static final Logger log = Logger.getGlobal();

    private long userId = 0;

    public ServantLocator2() {
        log.info("## ServantLocator2() ##");
    }

    public Object locate(Current curr, LocalObjectHolder cookie) throws UserException {
        log.info("## ServantLocator2 #" + curr.id + " .locate() ##");

        return new UserI(userId++);
    }

    public void finished(Current curr, Object servant, java.lang.Object cookie) throws UserException {
        log.info("## ServantLocator1 #" + curr.id + " .finished() ##");
    }

    public void deactivate(String category) {
        log.info("## ServantLocator1 #" + category + " .deactivate() ##");
    }
}
