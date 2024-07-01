package it.unisalento.pasproject.paymentservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingDTO {
    private String id;
    private double mediumEnergyCost;
    private double mediumResourceConsumption;
    private double changeConstant;
}
