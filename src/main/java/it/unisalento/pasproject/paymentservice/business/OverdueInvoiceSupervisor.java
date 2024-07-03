package it.unisalento.pasproject.paymentservice.business;

import it.unisalento.pasproject.paymentservice.domain.Invoice;
import it.unisalento.pasproject.paymentservice.repositories.InvoiceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OverdueInvoiceSupervisor {
    private final InvoiceRepository invoiceRepository;

    @Autowired
    public OverdueInvoiceSupervisor(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @PostConstruct
    @Scheduled(cron = "0 0 3 * * ?")
    public void checkOverdueInvoices() {
        List<Invoice> pendingInvoices = invoiceRepository.findByInvoiceStatus(Invoice.Status.OVERDUE);

        LocalDateTime now = LocalDateTime.now();

        for (Invoice invoice : pendingInvoices) {
            if (invoice.getInvoiceOverdueDate().isBefore(now)) {
                invoice.setInvoiceStatus(Invoice.Status.OVERDUE);
                invoiceRepository.save(invoice);
            }
        }
    }
}
