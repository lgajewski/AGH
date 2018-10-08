package lgajewski.distributed.lab4.server.locator;

import Ice.*;
import Ice.Object;
import lgajewski.distributed.lab4.impl.UserI;
import lgajewski.distributed.lab4.server.serialize.Serializer;

import java.util.logging.Logger;

public class ServantLocator1 implements Ice.ServantLocator {

    private static final Logger log = Logger.getGlobal();

    private final ObjectAdapter adapter;

    private Serializer serializer = new Serializer();

    private int userId = 5;

    public ServantLocator1(ObjectAdapter adapter) {
        log.info("## ServantLocator1() ##");

        this.adapter = adapter;
    }

    public Object locate(Current curr, LocalObjectHolder cookie) throws UserException {
        log.info("## ServantLocator1 #" + curr.id.category + "/" + curr.id.name + ".locate() ##");

        String key = curr.id.name;

        Identity identity = new Identity(curr.id.name, curr.id.category);
        Object servant = adapter.find(identity);

        if (servant == null) {
            servant = (Object) serializer.deserialize(key);

            if (servant == null) {
                servant = new UserI(userId++);
            }

            adapter.add(servant, new Identity(curr.id.name, curr.id.category));
        }

        return servant;
    }

    public void finished(Current curr, Object servant, java.lang.Object cookie) throws UserException {
        log.info("## ServantLocator1 #" + curr.id.category + "/" + curr.id.name + " .finished() ##");
    }

    public void deactivate(String category) {
        log.info("## ServantLocator1 #" + category + " .deactivate() ##");
    }
}
