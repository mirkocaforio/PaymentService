package it.unisalento.pasproject.paymentservice.controller;

import it.unisalento.pasproject.paymentservice.dto.InvoiceListDTO;
import it.unisalento.pasproject.paymentservice.service.PaymentQueryFilters;
import it.unisalento.pasproject.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import static it.unisalento.pasproject.paymentservice.security.SecurityConstants.ROLE_UTENTE;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {
    private final PaymentService paymentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    public InvoiceController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping(value="/find/all")
    @Secured({ROLE_UTENTE})
    public InvoiceListDTO getAllInvoice() {
        return paymentService.getAllInvoices();
    }

    @GetMapping(value="/find")
    @Secured({ROLE_UTENTE})
    public InvoiceListDTO getInvoiceByFilters(@ModelAttribute PaymentQueryFilters filters) {
        return paymentService.getInvoicesByFilters(filters);
    }
}
