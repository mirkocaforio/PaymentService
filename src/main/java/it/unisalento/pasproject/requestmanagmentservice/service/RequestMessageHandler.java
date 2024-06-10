package it.unisalento.pasproject.requestmanagmentservice.service;

import it.unisalento.pasproject.requestmanagmentservice.business.exchanger.MessageExchanger;
import it.unisalento.pasproject.requestmanagmentservice.business.producer.MessageProducer;
import it.unisalento.pasproject.requestmanagmentservice.repositories.RequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestMessageHandler {
    private final RequestRepository requestRepository;
    private final MessageProducer messageProducer;
    private final MessageExchanger messageExchanger;

    private String requestResponseExchange;

    private String requestResponseTopic;

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestMessageHandler.class);

    @Autowired
    public RequestMessageHandler(RequestRepository requestRepository, MessageProducer messageProducer, MessageExchanger messageExchanger) {
        this.requestRepository = requestRepository;
        this.messageProducer = messageProducer;
        this.messageExchanger = messageExchanger;
    }
}
