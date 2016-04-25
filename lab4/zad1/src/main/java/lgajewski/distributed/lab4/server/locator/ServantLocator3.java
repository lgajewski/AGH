package lgajewski.distributed.lab4.server.locator;

import Ice.*;
import Ice.Object;
import lgajewski.distributed.lab4.impl.UserI;
import lgajewski.distributed.lab4.server.LRU;

import java.util.logging.Logger;

public class ServantLocator3 implements ServantLocator {

    private static final int N = 5;

    private UserI[] servants;

    private static final Logger log = Logger.getGlobal();

    public ServantLocator3() {
        log.info("## ServantLocator3() ##");
        this.servants = new UserI[N];
    }

    public Object locate(Current curr, LocalObjectHolder cookie) throws UserException {
        log.info("## ServantLocator3 #" + curr.id.category + "/" + curr.id.name + " .locate() ##");

        return getNextServant();
    }

    public void finished(Current curr, Object servant, java.lang.Object cookie) throws UserException {
        log.info("## ServantLocator1 #" + curr.id.category + "/" + curr.id.name + " .finished() ##");
    }

    public void deactivate(String category) {
        log.info("## ServantLocator1 #" + category + " .deactivate() ##");
    }

    private Object getNextServant() {
        int index = LRU.getIndexLRU(servants);
        UserI servant = servants[index];

        if (servant == null) {
            servant = new UserI(index);
            servants[index] = servant;
        }

        return servant;
    }

}
