package it.unisalento.pasproject.paymentservice.repositories;

import it.unisalento.pasproject.paymentservice.domain.Setting;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SettingRepository extends MongoRepository<Setting, String> {
}
