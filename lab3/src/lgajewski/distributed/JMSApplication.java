package lgajewski.distributed;

import lgajewski.distributed.client.Generator;
import lgajewski.distributed.client.Solver;

import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.Scanner;

import static lgajewski.distributed.JMSProperties.DEFAULT_JMS_PROVIDER_URL;
import static lgajewski.distributed.JMSProperties.JNDI_CONTEXT_FACTORY_CLASS_NAME;

public class JMSApplication {

    public static void main(String[] args) throws NamingException, JMSException {
        // Application JNDI context
        Context jndiContext = initializeJndiContext(DEFAULT_JMS_PROVIDER_URL.getProperty(),
                JNDI_CONTEXT_FACTORY_CLASS_NAME.getProperty());

//        initializeAdministrativeObjects(DEFAULT_QUEUE_NAME.getProperty());

        Generator generator = new Generator(jndiContext, Task.SUM);
        Solver solver = new Solver(jndiContext, Task.SUM);

        Thread thread1 = new Thread(solver);
        Thread thread2 = new Thread(generator);
        thread1.start();
        thread2.start();

        System.out.println("Running! Type 'exit' to shutdown application.\n");

        // register shadow hooks
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            }
        });

        // shutdown
        handleShutdownApplication();

        // cleanUp
        jndiContext.close();
        generator.stop();
        solver.stop();
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
