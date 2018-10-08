package lgajewski.distributed.lab3;

import lgajewski.distributed.lab3.client.Collector;
import lgajewski.distributed.lab3.client.Generator;
import lgajewski.distributed.lab3.client.Solver;

import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;

import static lgajewski.distributed.lab3.Utils.*;

public class JMSApplication {

    private static final int GENERATORS = 2;
    private static final int SOLVERS = 7;
    private static final int COLLECTORS = 7;

    public static void main(String[] args) throws NamingException, JMSException {
        // Application JNDI context
        Context jndiContext = initializeJndiContext(JMSProperties.DEFAULT_JMS_PROVIDER_URL.getProperty(),
                JMSProperties.JNDI_CONTEXT_FACTORY_CLASS_NAME.getProperty());

        // create generators
        List<Generator> generators = new ArrayList<>();
        for (int i = 0; i < GENERATORS; i++) {
            generators.add(new Generator(jndiContext));
        }

        // create solvers
        List<Solver> solvers = new ArrayList<>();
        for (int i = 0; i < SOLVERS; i++) {
            Task task = getNextTask(i);
            solvers.add(new Solver(jndiContext, task));
        }

        // create collectors
        List<Collector> collectors = new ArrayList<>();
        for (int i = 0; i < COLLECTORS; i++) {
            Task task = getNextTask(i);
            collectors.add(new Collector(jndiContext, task));
        }

        // start threads
        generators.forEach(generator -> new Thread(generator).start());
        solvers.forEach(solver -> new Thread(solver).start());
        collectors.forEach(collector -> new Thread(collector).start());

        System.out.println("Running! Type 'exit' to shutdown application.\n");

        // register shadow hooks
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutdown Hook!");
            }
        });

        // shutdown
        handleShutdownApplication();

        // cleanUp
        jndiContext.close();
        generators.forEach(JMSClient::stop);
        solvers.forEach(JMSClient::stop);
        collectors.forEach(JMSClient::stop);
    }

}
