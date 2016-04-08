package lgajewski.distributed.lab3.client;

import lgajewski.distributed.lab3.JMSClient;
import lgajewski.distributed.lab3.Task;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.regex.Pattern;

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
    private TopicSession topicSession;

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

        // connection
        queueConnection = queueConnectionFactory.createQueueConnection();
        topicConnection = topicConnectionFactory.createTopicConnection();

        QueueSession queueSession = queueConnection.createQueueSession(
                false, // non-transactional
                AUTO_ACKNOWLEDGE //Messages acknowledged after receive() method returns
        );

        topicSession = topicConnection.createTopicSession(
                false, // non-transactional
                AUTO_ACKNOWLEDGE //Messages acknowledged after receive() method returns
        );

        queueConsumer = queueSession.createConsumer(queue, "task = '" + task.name() + "'");
        topicPublisher = topicSession.createPublisher(topic);

        System.out.println("[S] JMS client objects initialized!");
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

        queueConsumer.setMessageListener(message -> {
            System.out.println("\t[S] got message: " + message);

            try {

                if (message != null && message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;

                    String solved = solveEquation(textMessage.getText());
                    System.out.println("\t[S] solved equation: " + solved);
                    Message result = topicSession.createTextMessage(solved);

                    System.out.println("\t[S] Forwarding to topic: " + task.getTopicName());
                    topicPublisher.send(result);

                } else {
                    System.out.println("\t[S] unknown message");
                }

            } catch (JMSException e) {
                e.printStackTrace();
                ;
            }


        });
    }

    private String solveEquation(String eq) {
        String[] numbers = eq.split(Pattern.quote(task.getOperator()));
        if (numbers.length != 2) {
            return eq + " = ?";
        }

        Double v1 = Double.valueOf(numbers[0]);
        Double v2 = Double.valueOf(numbers[1]);

        Object result;

        switch (task) {
            case SUM:
                result = (v1 + v2);
                break;
            case SUBTRACT:
                result = (v1 - v2);
                break;
            case MULTIPLY:
                result = v1 * v2;
                break;
            case DIVIDE:
                result = v1 / v2;
                break;
            case DIV:
                result = v1.intValue() / v2.intValue();
                break;
            case MOD:
                result = v1 % v2;
                break;
            case POW:
                result = Math.pow(v1, v2);
                break;
            default:
                result = "?";
        }

        return eq + " = " + result;
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
