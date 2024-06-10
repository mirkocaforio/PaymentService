package it.unisalento.pasproject.requestmanagmentservice.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "requests")
public class Request {
    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    @Id
    private String id;
    private String name;
    private String surname;
    private String email;
    private String role;
    private Status status;
}
