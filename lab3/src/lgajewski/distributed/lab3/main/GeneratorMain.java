package lgajewski.distributed.lab3.main;

import lgajewski.distributed.lab3.JMSProperties;
import lgajewski.distributed.lab3.client.Generator;

import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;

import static lgajewski.distributed.lab3.Utils.handleShutdownApplication;
import static lgajewski.distributed.lab3.Utils.initializeJndiContext;

public class GeneratorMain {

    private static int GENERATORS = 2;

    public static void main(String[] args) throws NamingException, JMSException {
        if (args.length == 1) {
            GENERATORS = Integer.valueOf(args[0]);
        }

        // Application JNDI context
        Context jndiContext = initializeJndiContext(JMSProperties.DEFAULT_JMS_PROVIDER_URL.getProperty(),
                JMSProperties.JNDI_CONTEXT_FACTORY_CLASS_NAME.getProperty());

        // create generators
        List<Generator> generators = new ArrayList<>();
        for (int i = 0; i < GENERATORS; i++) {
            generators.add(new Generator(jndiContext));
        }

        // start threads
        generators.forEach(generator -> new Thread(generator).start());

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
        generators.forEach(Generator::stop);
    }

}
