package lgajewski.distributed;

import lgajewski.distributed.client.Generator;
import lgajewski.distributed.client.Solver;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

import static lgajewski.distributed.JMSProperties.*;

public class JMSApplication {

    // Application JNDI context
    private static Context jndiContext;

    // JMS Administrative objects references
    private static QueueConnectionFactory queueConnectionFactory;
    private static Queue queue;

    public static void main(String[] args) throws NamingException, JMSException {
        initializeJndiContext(DEFAULT_JMS_PROVIDER_URL.getProperty(), JNDI_CONTEXT_FACTORY_CLASS_NAME.getProperty());
        initializeAdministrativeObjects(DEFAULT_QUEUE_NAME.getProperty());

        Generator generator = new Generator(queueConnectionFactory, queue, Task.SUM);
        Solver solver = new Solver(queueConnectionFactory, queue, Task.SUM);

        Thread thread1 = new Thread(solver);
        Thread thread2 = new Thread(generator);
        thread1.start();
        thread2.start();

        System.out.println("Runinng...");
    }

    private static void initializeJndiContext(String providerUrl, String jndiContextClassName) throws NamingException {
        // JNDI Context
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, jndiContextClassName);
        props.put(Context.PROVIDER_URL, providerUrl);
        jndiContext = new InitialContext(props);
        System.out.println("JNDI context initialized!");
    }

    private static void initializeAdministrativeObjects(String queueName) throws NamingException {
        // ConnectionFactory
        queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("ConnectionFactory");

        // Destination
        queue = (Queue) jndiContext.lookup(queueName);
        System.out.println("JMS administrative objects (ConnectionFactory, Destinations) initialized!");
    }

}
