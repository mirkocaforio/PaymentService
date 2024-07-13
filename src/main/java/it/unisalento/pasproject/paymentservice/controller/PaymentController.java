package it.unisalento.pasproject.paymentservice.controller;

import it.unisalento.pasproject.paymentservice.dto.InvoiceDTO;
import it.unisalento.pasproject.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import static it.unisalento.pasproject.paymentservice.security.SecurityConstants.ROLE_UTENTE;

@RestController
@RequestMapping ("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PutMapping(value="/pay/{invoiceNumber}")
    @Secured(ROLE_UTENTE)
    public InvoiceDTO updateInvoice(@PathVariable String invoiceNumber) {
        return paymentService.updateInvoice(invoiceNumber);
    }
}
