package it.unisalento.pasproject.paymentservice.exception;

import it.unisalento.pasproject.paymentservice.exception.global.CustomErrorException;
import org.springframework.http.HttpStatus;

public class MissingDataException extends CustomErrorException {
    public MissingDataException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
