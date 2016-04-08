package lgajewski.distributed.lab3.client;


import lgajewski.distributed.lab3.JMSClient;
import lgajewski.distributed.lab3.Task;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Random;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

public class Generator implements JMSClient, Runnable {

    private static final int BOUND = 100;

    // Task for generator
    private final Task task;

    // JMS Client objects
    private QueueConnection connection;
    private QueueSession session;
    private QueueSender sender;

    private volatile boolean running = true;

    public Generator(Context jndiContext, Task task) throws JMSException, NamingException {
        this.task = task;

        initializeJmsClientObjects(jndiContext);
    }

    private void initializeJmsClientObjects(Context jndiContext) throws JMSException, NamingException {
        // ConnectionFactory
        QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("ConnectionFactory");

        // Destination
        Queue queue = (Queue) jndiContext.lookup(task.getQueueName());

        connection = queueConnectionFactory.createQueueConnection();
        session = connection.createQueueSession(
                false, // non-transactional
                AUTO_ACKNOWLEDGE //Messages acknowledged after receive() method returns
        );

        sender = session.createSender(queue);
        System.out.println("[G] JMS client objects initialized!");
    }

    @Override
    public void run() {
        while (running) {
            try {
                generate();
            } catch (JMSException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void generate() throws JMSException, InterruptedException {
        connection.start();

        String equation = generateEquation();
        System.out.println("[G] sending task: " + equation);

        // send a TextMessage and set task property
        Message message = session.createTextMessage(equation);
        message.setStringProperty("task", task.name());

        sender.send(message);

        Thread.sleep(2000);
    }

    @Override
    public void stop() {
        running = false;

        // close the connection
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    private String generateEquation() {
        Random random = new Random();
        int v1 = random.nextInt(BOUND);
        int v2 = random.nextInt(BOUND);

        return v1 + task.getOperator() + v2;
    }
}
