package pl.edu.agh.iosr.raft.node;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.edu.agh.iosr.raft.node.protocol.Node;

@Component
public class Runner implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final FanoutExchange fanoutExchange;

    public Runner(RabbitTemplate rabbitTemplate, FanoutExchange fanoutExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.fanoutExchange = fanoutExchange;
    }

    @Override
    public void run(String... args) {
        Node node = new Node(rabbitTemplate);
//        System.out.println("Sending message to node #1..");
//        rabbitTemplate.convertAndSend(AMQPConfiguration.getRoutingKey(1), "Hello from RabbitMQ!");
//
//        System.out.println("Sending message to all nodes..");
//        rabbitTemplate.convertAndSend(fanoutExchange.getName(), null, "Hello to all!");
    }

}