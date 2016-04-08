package lgajewski.distributed.client;

import lgajewski.distributed.JMSClient;
import lgajewski.distributed.Task;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

public class Solver implements JMSClient, Runnable {

    // Task for generator
    private final Task task;

    // JMS Client objects - queue
    private QueueConnection queueConnection;
    private MessageConsumer queueConsumer;

    // JMS Client objects - topic
    private TopicConnection topicConnection;
    private TopicPublisher topicPublisher;

    public Solver(Context jndiContext, Task task) throws JMSException, NamingException {
        this.task = task;

        initializeJmsClientObjects(jndiContext);
    }

    private void initializeJmsClientObjects(Context jndiContext) throws JMSException, NamingException {
        // ConnectionFactory
        QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("ConnectionFactory");
        TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) jndiContext.lookup("ConnectionFactory");

        // Destination
        Queue queue = (Queue) jndiContext.lookup(task.getQueueName());
        Topic topic = (Topic) jndiContext.lookup(task.getTopicName());

        queueConnection = queueConnectionFactory.createQueueConnection();
        QueueSession session = queueConnection.createQueueSession(
                false, // non-transactional
                AUTO_ACKNOWLEDGE //Messages acknowledged after receive() method returns
        );

        queueConsumer = session.createConsumer(queue, "task = '" + task.name() + "'");
        System.out.println("JMS client objects initialized!");
    }

    @Override
    public void run() {
        try {
            listen();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void listen() throws JMSException {
        queueConnection.start();
        topicConnection.start();

        queueConsumer.setMessageListener(message -> System.out.println("[receiver] got message: " + message));
    }

    @Override
    public void stop() {
        // close the queueConnection
        if (queueConnection != null) {
            try {
                queueConnection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

        // close the topicConnection
        if (topicConnection != null) {
            try {
                topicConnection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

}
