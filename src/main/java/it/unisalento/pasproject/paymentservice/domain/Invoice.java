package it.unisalento.pasproject.paymentservice.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "invoices")
public class Invoice {
    public enum Status {
        PENDING,
        PAID,
        OVERDUE
    }

    @Id
    private String invoiceNumber;
    private String invoiceDescription;
    private String userEmail;
    private String invoicePaymentMethod;
    private LocalDateTime invoicePaymentDate;
    private ItemList invoiceItems;
    private float invoiceAmount;
    private Status invoiceStatus;
    private LocalDateTime invoiceOverdueDate;
}
