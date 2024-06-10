package it.unisalento.pasproject.requestmanagmentservice.service;

import it.unisalento.pasproject.requestmanagmentservice.repositories.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class RequestService {
    private final MongoTemplate mongoTemplate;

    private final RequestMessageHandler requestMessageHandler;

    private final RequestRepository requestRepository;

    private static final Logger LOGGER = Logger.getLogger(RequestService.class.getName());

    @Autowired
    public RequestService(MongoTemplate mongoTemplate, RequestMessageHandler requestMessageHandler, RequestRepository requestRepository) {
        this.mongoTemplate = mongoTemplate;
        this.requestMessageHandler = requestMessageHandler;
        this.requestRepository = requestRepository;
    }
}
