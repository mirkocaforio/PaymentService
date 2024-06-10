package it.unisalento.pasproject.requestmanagmentservice.repositories;

import it.unisalento.pasproject.requestmanagmentservice.domain.Request;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RequestRepository extends MongoRepository<Request, String> {
}
