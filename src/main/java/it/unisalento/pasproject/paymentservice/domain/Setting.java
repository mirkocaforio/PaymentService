package it.unisalento.pasproject.paymentservice.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "setting")
public class Setting {
    private String id;
    private double mediumEnergyCost;
    private float mediumResourceConsumption;
    private float changeConstant;
}
