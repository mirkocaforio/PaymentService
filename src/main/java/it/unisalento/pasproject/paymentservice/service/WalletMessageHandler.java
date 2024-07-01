package it.unisalento.pasproject.paymentservice.service;

import it.unisalento.pasproject.paymentservice.business.producer.MessageProducer;
import it.unisalento.pasproject.paymentservice.dto.GeneralRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WalletMessageHandler {
    private final MessageProducer messageProducer;

    @Value("${rabbitmq.exchange.data.name}")
    private String walletExchange;

    @Value("${rabbitmq.routing.refill.name}")
    private String walletTopic;

    @Autowired
    public WalletMessageHandler(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    public void sendRefillMessage(GeneralRequestDTO requestDTO) {
        messageProducer.sendMessage(requestDTO, walletTopic, walletExchange);
    }
}
