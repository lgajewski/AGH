package lgajewski.distributed.lab3;

import lgajewski.distributed.lab3.client.Collector;
import lgajewski.distributed.lab3.client.Generator;
import lgajewski.distributed.lab3.client.Solver;

import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class JMSApplication {

    private static final int GENERATORS = 1;
    private static final int SOLVERS = 5;
    private static final int COLLECTORS = 3;

    private static Task.RandomTask<Task> randomTask = new Task.RandomTask<>(Task.class);

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
            Task task = randomTask.random();
            solvers.add(new Solver(jndiContext, task));
        }

        // create collectors
        List<Collector> collectors = new ArrayList<>();
        for (int i = 0; i < COLLECTORS; i++) {
            Task task = randomTask.random();
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
        generators.forEach(Generator::stop);
        solvers.forEach(Solver::stop);
        collectors.forEach(Collector::stop);
    }

    private static void handleShutdownApplication() {
        Scanner scanner = new Scanner(System.in);
        String line = "";

        while (!line.equals("exit")) {
            line = scanner.nextLine();
        }

        System.out.println("Terminating!");
    }

    private static Context initializeJndiContext(String providerUrl, String jndiContextClassName) throws NamingException {
        // JNDI Context
        System.out.println("JNDI context initialization.");
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, jndiContextClassName);
        props.put(Context.PROVIDER_URL, providerUrl);
        return new InitialContext(props);
    }

}
