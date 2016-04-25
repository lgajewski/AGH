package lgajewski.distributed.lab4.server;

import Ice.Current;
import Ice.LocalObjectHolder;
import Ice.Object;
import demo.Name;
import evictor.EvictorBase;
import lgajewski.distributed.lab4.impl.CustomerI;
import lgajewski.distributed.lab4.server.serialize.Serializer;

import java.util.logging.Logger;


public class ServantEvictor extends EvictorBase {

    private static final Logger log = Logger.getGlobal();

    private static final int N = 5;

    private Serializer serializer = new Serializer();

    private int userId = N;

    public ServantEvictor() {
        super(N);

        log.info("## ServantEvictor(" + N + ") ##");
    }

    @Override
    public Object add(Current c, LocalObjectHolder cookie) {
        log.info("## ServantEvictor # add(" + c.id.category + "/" + c.id.name + ") #");

        java.lang.Object object = serializer.deserialize("customer" + c.id.name);

        if (object == null) {
            object = new CustomerI(userId++, new Name("Name", "Surname"));
        }

        return (Object) object;
    }

    @Override
    public void evict(Object servant, java.lang.Object cookie) {
        CustomerI customer = (CustomerI) servant;
        log.info("## ServantEvictor # evict(customer" + customer.getUniqueId() + ") #");

        serializer.serialize("customer" + customer.getUniqueId(), customer);
    }

}
