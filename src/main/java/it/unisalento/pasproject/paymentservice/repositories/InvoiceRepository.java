package it.unisalento.pasproject.paymentservice.repositories;

import it.unisalento.pasproject.paymentservice.domain.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    List<Invoice> findByInvoiceStatus(Invoice.Status status);
}
