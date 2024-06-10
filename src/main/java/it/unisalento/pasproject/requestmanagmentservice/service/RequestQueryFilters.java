package it.unisalento.pasproject.requestmanagmentservice.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestQueryFilters {
    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    private String name;
    private String surname;
    private String email;
    private String role;
    private Status status;
}
