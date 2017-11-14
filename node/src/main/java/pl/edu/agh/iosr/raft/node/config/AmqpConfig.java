package pl.edu.agh.iosr.raft.node.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.edu.agh.iosr.raft.node.properties.AmqpProperties;

@Configuration
public class AmqpConfig {

    private final AmqpProperties amqpProperties;

    @Autowired
    public AmqpConfig(AmqpProperties amqpProperties) {
        this.amqpProperties = amqpProperties;
    }

    @Bean
    Queue queue() {
        return new Queue(amqpProperties.getQueue(), false);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(amqpProperties.getExchange());
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(amqpProperties.getRoutingKey());
    }

    @Bean
    MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

}
