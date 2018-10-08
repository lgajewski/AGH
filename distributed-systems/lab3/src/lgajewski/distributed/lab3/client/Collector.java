package lgajewski.distributed.lab3.client;

import lgajewski.distributed.lab3.JMSClient;
import lgajewski.distributed.lab3.Task;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

public class Collector implements JMSClient, Runnable {

    // Task for generator
    private final Task task;

    // JMS Client objects - topic
    private TopicConnection topicConnection;
    private TopicSubscriber topicSubscriber;

    public Collector(Context jndiContext, Task task) throws JMSException, NamingException {
        this.task = task;

        initializeJmsClientObjects(jndiContext);
    }

    private void initializeJmsClientObjects(Context jndiContext) throws JMSException, NamingException {
        // ConnectionFactory
        TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) jndiContext.lookup("ConnectionFactory");

        // Destination
        Topic topic = (Topic) jndiContext.lookup(task.getTopicName());

        // connection
        topicConnection = topicConnectionFactory.createTopicConnection();
        TopicSession topicSession = topicConnection.createTopicSession(
                false, // non-transactional
                AUTO_ACKNOWLEDGE //Messages acknowledged after receive() method returns
        );

        topicSubscriber = topicSession.createDurableSubscriber(topic, task.name());

        System.out.format("[C - %s] JMS client objects initialized!\n", task.name());
    }

    @Override
    public void run() {
        try {
            subscribe();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void subscribe() throws JMSException {
        topicConnection.start();

        topicSubscriber.setMessageListener(message -> {
            if (message != null) {
                System.out.format("\t\t[C - %s] collected result: %s\n", task.name(), message);
            }
        });
    }

    @Override
    public void stop() {
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

