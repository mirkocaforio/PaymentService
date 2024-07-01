package it.unisalento.pasproject.paymentservice.repositories;

import it.unisalento.pasproject.paymentservice.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUserEmail(String email);
}
