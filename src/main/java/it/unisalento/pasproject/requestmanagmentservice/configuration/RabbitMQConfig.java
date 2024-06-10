package it.unisalento.pasproject.requestmanagmentservice.configuration;

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

    // ------  SECURITY  ------ //

    // Needed by authentication service
    @Value("${rabbitmq.queue.security.name}")
    private String securityResponseQueue;

    @Value("${rabbitmq.exchange.security.name}")
    private String securityExchange;

    @Value("${rabbitmq.routing.security.key}")
    private String securityRequestRoutingKey;

    /**
     * Defines the security response queue.
     *
     * @return a new Queue instance
     */
    @Bean
    public Queue securityResponseQueue() {
        return new Queue(securityResponseQueue);
    }

    /**
     * Defines the security exchange.
     *
     * @return a new TopicExchange instance
     */
    @Bean
    public TopicExchange securityExchange() {
        return new TopicExchange(securityExchange);
    }

    /**
     * Defines the binding between the security response queue and the security exchange.
     *
     * @return a new Binding instance
     */
    @Bean
    public Binding securityBinding() {
        return BindingBuilder
                .bind(securityResponseQueue())
                .to(securityExchange())
                .with(securityRequestRoutingKey);
    }

    // ------  END SECURITY  ------ //

    // ------  REQUEST  ------ //
    @Value("${rabbitmq.queue.request.name}")
    private String requestQueue;

    @Value("${rabbitmq.exchange.request.name}")
    private String requestExchange;

    @Value("${rabbitmq.routing.request.key}")
    private String requestRoutingKey;

    /**
     * Defines the security response queue.
     *
     * @return a new Queue instance
     */
    @Bean
    public Queue requestQueue() {
        return new Queue(requestQueue);
    }

    /**
     * Defines the security exchange.
     *
     * @return a new TopicExchange instance
     */
    @Bean
    public TopicExchange requestExchange() {
        return new TopicExchange(requestExchange);
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
                .with(requestRoutingKey);
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
