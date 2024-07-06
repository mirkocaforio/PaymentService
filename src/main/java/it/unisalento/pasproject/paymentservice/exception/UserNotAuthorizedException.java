package it.unisalento.pasproject.paymentservice.exception;

import it.unisalento.pasproject.paymentservice.exception.global.CustomErrorException;
import org.springframework.http.HttpStatus;

public class UserNotAuthorizedException extends CustomErrorException {

    public UserNotAuthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
