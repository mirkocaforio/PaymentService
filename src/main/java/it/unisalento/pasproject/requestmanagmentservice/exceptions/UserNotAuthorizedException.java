package it.unisalento.pasproject.requestmanagmentservice.exceptions;

import it.unisalento.pasproject.requestmanagmentservice.exceptions.global.CustomErrorException;
import org.springframework.http.HttpStatus;

public class UserNotAuthorizedException extends CustomErrorException {

    public UserNotAuthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
