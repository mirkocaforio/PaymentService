package it.unisalento.pasproject.paymentservice.controller;

import it.unisalento.pasproject.paymentservice.dto.InvoiceDTO;
import it.unisalento.pasproject.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PutMapping(value="/pay/{invoiceNumber}")
    public InvoiceDTO updateInvoice(@PathVariable String invoiceNumber) {
        return paymentService.updateInvoice(invoiceNumber);
    }
}
