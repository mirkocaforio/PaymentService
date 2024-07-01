package it.unisalento.pasproject.paymentservice.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQConfig is a configuration class that sets up the RabbitMQ message broker.
 * It defines the queues, exchanges, and bindings used in the application, as well as the message converter and AMQP template.
 */
@Configuration
public class RabbitMQConfig {
    // ------  USERS INFO  ------ //
    @Value("${rabbitmq.queue.users.name}")
    private String usersQueue;

    @Value("${rabbitmq.exchange.users.name}")
    private String usersExchange;

    @Value("${rabbitmq.routing.users.key}")
    private String usersRoutingKey;

    /**
     * Defines the security response queue.
     *
     * @return a new Queue instance
     */
    @Bean
    public Queue requestQueue() {
        return new Queue(usersQueue);
    }

    /**
     * Defines the security exchange.
     *
     * @return a new TopicExchange instance
     */
    @Bean
    public TopicExchange requestExchange() {
        return new TopicExchange(usersExchange);
    }

    /**
     * Defines the binding between the security response queue and the security exchange.
     *
     * @return a new Binding instance
     */
    @Bean
    public Binding requestBinding() {
        return BindingBuilder
                .bind(requestQueue())
                .to(requestExchange())
                .with(usersRoutingKey);
    }

    /**
     * Creates a message converter for JSON messages.
     *
     * @return a new Jackson2JsonMessageConverter instance.
     */
    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Creates an AMQP template for sending messages.
     *
     * @param connectionFactory the connection factory to use.
     * @return a new RabbitTemplate instance.
     */
    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
