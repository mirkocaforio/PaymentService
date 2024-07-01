package it.unisalento.pasproject.paymentservice.exceptions;

import it.unisalento.pasproject.paymentservice.exceptions.global.CustomErrorException;
import org.springframework.http.HttpStatus;

public class UserNotAuthorizedException extends CustomErrorException {

    public UserNotAuthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
