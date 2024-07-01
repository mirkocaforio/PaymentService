package it.unisalento.pasproject.paymentservice.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentQueryFilters {
    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    private String userEmail;
    private String invoicePaymentMethod;
    private LocalDateTime invoicePaymentDate;
    private float invoiceAmount;
    private Status invoiceStatus;
    private LocalDateTime invoiceOverdueDate;
}
