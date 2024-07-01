package it.unisalento.pasproject.paymentservice.business;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PaymentInvoiceSupervisor {
    private final InvoiceEmitter invoiceEmitter;

    @Autowired
    public PaymentInvoiceSupervisor(InvoiceEmitter invoiceEmitter) {
        this.invoiceEmitter = invoiceEmitter;
    }

    @PostConstruct
    @Scheduled(cron = "0 0 6 * * ?")
    public void checkInvoiceGeneration() {
        invoiceEmitter.emitInvoice();
    }
}
