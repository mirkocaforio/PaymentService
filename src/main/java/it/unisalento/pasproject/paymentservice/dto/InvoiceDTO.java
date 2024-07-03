package it.unisalento.pasproject.paymentservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import it.unisalento.pasproject.paymentservice.domain.ItemList;
import it.unisalento.pasproject.paymentservice.service.ItemDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InvoiceDTO {
    public enum Status {
        PENDING,
        PAID,
        OVERDUE
    }

    private String invoiceNumber;
    private String invoiceDescription;
    private String userName;
    private String userSurname;
    private String userEmail;
    private String userResidenceCity;
    private String userResidenceAddress;
    private String invoicePaymentMethod;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime invoicePaymentDate;

    @JsonDeserialize(using = ItemDeserializer.class)
    private ItemList invoiceItems;
    private float invoicePartialAmount;
    private float invoiceDelayAmount;
    private float invoiceTotalAmount;
    private Status invoiceStatus;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime invoiceOverdueDate;
}
