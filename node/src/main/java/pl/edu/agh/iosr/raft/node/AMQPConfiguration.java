package pl.edu.agh.iosr.raft.node;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.edu.agh.iosr.raft.node.protocol.Node;

@Configuration
public class AMQPConfiguration {

    private final static String FANOUT_EXCHANGE_NAME = "raft-exchange-fanout";

    private final static String QUEUE_BASE_NAME = "raft-queue-direct-";
    private final static String QUEUE_NAME = QUEUE_BASE_NAME + Application.nodeId;

    @Bean
    Queue queue() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE_NAME);
    }

    @Bean
    Binding fanoutBinding(Queue queue, FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_NAME);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Node node) {
        return new MessageListenerAdapter(node, "receiveMessage");
    }

    public static String getRoutingKey(int nodeId) {
        return QUEUE_BASE_NAME + nodeId;
    }

}
