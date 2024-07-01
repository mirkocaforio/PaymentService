package it.unisalento.pasproject.paymentservice.exceptions;

import it.unisalento.pasproject.paymentservice.exceptions.global.CustomErrorException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends CustomErrorException {
    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
