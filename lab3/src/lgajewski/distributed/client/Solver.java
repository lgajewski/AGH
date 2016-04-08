package lgajewski.distributed.client;

import lgajewski.distributed.JMSClient;
import lgajewski.distributed.Task;

import javax.jms.*;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

public class Solver implements JMSClient, Runnable {

    // Task for generator
    private final Task task;

    // JMS Administrative objects references
    private QueueConnectionFactory queueConnectionFactory;
    private Queue incomingMessagesQueue;

    // JMS Client objects
    private QueueConnection connection;
    private MessageConsumer consumer;

    private volatile boolean running = true;

    public Solver(QueueConnectionFactory queueConnectionFactory, Queue incomingMessagesQueue, Task task) throws JMSException {
        this.queueConnectionFactory = queueConnectionFactory;
        this.incomingMessagesQueue = incomingMessagesQueue;
        this.task = task;

        initializeJmsClientObjects();
    }

    private void initializeJmsClientObjects() throws JMSException {
        connection = queueConnectionFactory.createQueueConnection();
        QueueSession session = connection.createQueueSession(
                false, // non-transactional
                AUTO_ACKNOWLEDGE //Messages acknowledged after receive() method returns
        );

        consumer = session.createConsumer(incomingMessagesQueue, "task = '" + task.name() + "'");
        System.out.println("JMS client objects initialized!");
    }

    @Override
    public void run() {
        try {
            listen();
        } catch (JMSException e) {
            e.printStackTrace();
        }

        while (running) {}
    }

    private void listen() throws JMSException {
        connection.start();

        consumer.setMessageListener(message -> System.out.println("[receiver] got message: " + message));
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

}
