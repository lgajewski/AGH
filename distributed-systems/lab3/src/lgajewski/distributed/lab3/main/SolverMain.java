package lgajewski.distributed.lab3.main;

import lgajewski.distributed.lab3.JMSClient;
import lgajewski.distributed.lab3.JMSProperties;
import lgajewski.distributed.lab3.Utils;
import lgajewski.distributed.lab3.client.Solver;

import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;

import static lgajewski.distributed.lab3.Utils.getNextTask;
import static lgajewski.distributed.lab3.Utils.initializeJndiContext;

public class SolverMain {

    private static int SOLVERS = 7;

    public static void main(String[] args) throws NamingException, JMSException {
        if (args.length == 1) {
            SOLVERS = Integer.valueOf(args[0]);
        }

        // Application JNDI context
        Context jndiContext = initializeJndiContext(JMSProperties.DEFAULT_JMS_PROVIDER_URL.getProperty(),
                JMSProperties.JNDI_CONTEXT_FACTORY_CLASS_NAME.getProperty());

        // create generators
        List<Solver> solvers = new ArrayList<>();
        for (int i = 0; i < SOLVERS; i++) {
            solvers.add(new Solver(jndiContext, getNextTask(i)));
        }

        // start threads
        solvers.forEach(generator -> new Thread(generator).start());

        System.out.println("Running! Type 'exit' to shutdown application.\n");

        // register shadow hooks
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutdown Hook!");
            }
        });

        // shutdown
        Utils.handleShutdownApplication();

        // cleanUp
        jndiContext.close();
        solvers.forEach(JMSClient::stop);
    }

}
