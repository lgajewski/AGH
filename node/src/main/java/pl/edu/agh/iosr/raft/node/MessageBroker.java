package pl.edu.agh.iosr.raft.node;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.agh.iosr.raft.node.properties.AmqpProperties;

@Component
public class MessageBroker {

    private final RabbitTemplate rabbitTemplate;
    private final AmqpProperties amqpProperties;

    @Autowired
    public MessageBroker(RabbitTemplate rabbitTemplate, AmqpProperties amqpProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.amqpProperties = amqpProperties;
    }

    public void sendMessage(Message msg) {
        rabbitTemplate.convertAndSend(amqpProperties.getExchange(), msg.getRoutingKey(), msg);
    }

}
