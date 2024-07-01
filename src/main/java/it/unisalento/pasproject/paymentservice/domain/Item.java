package it.unisalento.pasproject.paymentservice.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {
    private String senderEmail;
    private String description;
    private double amount;
}
