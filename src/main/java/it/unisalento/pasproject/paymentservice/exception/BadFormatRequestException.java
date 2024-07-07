package it.unisalento.pasproject.paymentservice.exception;

import it.unisalento.pasproject.paymentservice.exception.global.CustomErrorException;
import org.springframework.http.HttpStatus;

public class BadFormatRequestException extends CustomErrorException {
    public BadFormatRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
