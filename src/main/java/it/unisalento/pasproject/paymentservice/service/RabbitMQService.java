package it.unisalento.pasproject.paymentservice.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The RabbitMQService class provides methods for managing RabbitMQ operations.
 * It includes methods for binding and unbinding a queue to a topic in RabbitMQ.
 * It uses a RabbitTemplate for performing RabbitMQ operations.
 */
@Service
public class RabbitMQService {

    /**
     * The RabbitTemplate used for RabbitMQ operations.
     */
    private final RabbitTemplate rabbitTemplate;

    /**
     * Constructor for the RabbitMQService.
     * @param rabbitTemplate The RabbitTemplate to be used for RabbitMQ operations.
     */
    @Autowired
    public RabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Binds a queue to a topic in RabbitMQ.
     * @param queueName The name of the queue to bind.
     * @param topicName The name of the topic to bind the queue to.
     * @param routingKey The routing key to use for the binding.
     */
    public void bindQueueToTopic(String queueName, String topicName, String routingKey) {
        rabbitTemplate.execute(channel -> {
            channel.queueDeclare(queueName, false, false, false, null);
            channel.exchangeDeclare(topicName, "topic");
            channel.queueBind(queueName, topicName, routingKey);
            return null;
        });
    }

    /**
     * Unbinds a queue from a topic in RabbitMQ.
     * @param queueName The name of the queue to unbind.
     * @param topicName The name of the topic to unbind the queue from.
     * @param routingKey The routing key that was used for the binding.
     */
    public void unbindQueueFromTopic(String queueName, String topicName, String routingKey) {
        rabbitTemplate.execute(channel -> {
            channel.queueUnbind(queueName, topicName, routingKey);
            return null;
        });
    }
}