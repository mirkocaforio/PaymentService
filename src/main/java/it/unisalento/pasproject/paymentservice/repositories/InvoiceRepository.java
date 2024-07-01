package it.unisalento.pasproject.paymentservice.repositories;

import it.unisalento.pasproject.paymentservice.domain.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {
}
