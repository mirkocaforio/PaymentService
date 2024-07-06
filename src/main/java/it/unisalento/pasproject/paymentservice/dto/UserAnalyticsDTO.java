package it.unisalento.pasproject.paymentservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAnalyticsDTO {
    private String userEmail;
    private int month;
    private int year;
    private int totalInvoices;
    private float partialAmount;
    private float delayAmount;
    private float totalAmount;
    private float averagePartialAmount;
    private float averageDelayAmount;
    private float averageTotalAmount;
    private float delayPercentage;
}
