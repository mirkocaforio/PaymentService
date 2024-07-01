package it.unisalento.pasproject.paymentservice.exceptions;

import it.unisalento.pasproject.paymentservice.exceptions.global.CustomErrorException;
import org.springframework.http.HttpStatus;

public class InvoiceNotFoundException extends CustomErrorException {
    public InvoiceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
