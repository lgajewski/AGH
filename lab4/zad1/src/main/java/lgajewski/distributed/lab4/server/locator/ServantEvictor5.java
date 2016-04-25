package lgajewski.distributed.lab4.server.locator;

import Ice.Current;
import Ice.LocalObjectHolder;
import Ice.Object;
import evictor.EvictorBase;
import lgajewski.distributed.lab4.impl.UserI;
import lgajewski.distributed.lab4.server.serialize.Serializer;

import java.util.logging.Logger;


public class ServantEvictor5 extends EvictorBase {

    private static final Logger log = Logger.getGlobal();

    private static final int N = 5;

    private Serializer serializer = new Serializer();

    private int userId = N;

    public ServantEvictor5() {
        super(N);

        log.info("## ServantEvictor5(" + N + ") ##");
    }

    @Override
    public Object add(Current c, LocalObjectHolder cookie) {
        log.info("## ServantEvictor5 # add(" + c.id + ") #");

        java.lang.Object object = serializer.deserialize(c.id.name);

        if (object == null) {
            object = new UserI(userId++);
        }

        return (Object) object;
    }

    @Override
    public void evict(Object servant, java.lang.Object cookie) {
        UserI u = (UserI) servant;
        log.info("## ServantEvictor5 # evict(user" + u.getId() + ") #");

        serializer.serialize("user" + u.getId(), u);
    }

}
