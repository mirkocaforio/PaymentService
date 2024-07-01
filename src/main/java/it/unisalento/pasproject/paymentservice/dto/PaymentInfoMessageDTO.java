package it.unisalento.pasproject.paymentservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentInfoMessageDTO {
    private String name;
    private String surname;
    private String email;
    private boolean isEnabled;
    private LocalDateTime registrationDate;
    private String residenceCity;
    private String residenceAddress;
    private String phoneNumber;
    private String cardNumber;
    private String cardExpiryDate;
    private String cardCvv;
}
