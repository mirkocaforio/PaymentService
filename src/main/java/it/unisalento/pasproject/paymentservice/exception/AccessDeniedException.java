package it.unisalento.pasproject.paymentservice.exception;

import it.unisalento.pasproject.paymentservice.exception.global.CustomErrorException;
import org.springframework.http.HttpStatus;

public class AccessDeniedException extends CustomErrorException {
    public AccessDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
