package it.unisalento.pasproject.requestmanagmentservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDTO {
    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    private String id;
    private String name;
    private String surname;
    private String email;
    private String role;
    private Status status;
}
