package it.unisalento.pasproject.paymentservice.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String surname;
    private String userEmail;
    private LocalDateTime registrationDate;
    private boolean isEnabled;
    private String residenceCity;
    private String residenceAddress;
    private String phoneNumber;
    private String cardNumber;
    private String cardExpiryDate;
    private String cardCvv;
}
