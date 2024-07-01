package it.unisalento.pasproject.paymentservice.exceptions;

import it.unisalento.pasproject.paymentservice.exceptions.global.CustomErrorException;
import org.springframework.http.HttpStatus;

public class PaymentMethodInvalidException extends CustomErrorException {
    public PaymentMethodInvalidException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
