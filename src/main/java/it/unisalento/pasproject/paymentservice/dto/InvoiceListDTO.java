package it.unisalento.pasproject.paymentservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InvoiceListDTO {
    private List<InvoiceDTO> invoicesList;

    public InvoiceListDTO() {
        this.invoicesList = new ArrayList<>();
    }
}
