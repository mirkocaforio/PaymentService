package it.unisalento.pasproject.paymentservice.business;

import it.unisalento.pasproject.paymentservice.domain.Invoice;
import it.unisalento.pasproject.paymentservice.domain.Item;
import it.unisalento.pasproject.paymentservice.domain.ItemList;
import it.unisalento.pasproject.paymentservice.domain.User;
import it.unisalento.pasproject.paymentservice.repositories.InvoiceRepository;
import it.unisalento.pasproject.paymentservice.service.CheckOutSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class InvoiceFactory {
    private final CheckOutSetting checkOutSetting;
    private final InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceFactory(CheckOutSetting checkOutSetting, InvoiceRepository invoiceRepository) {
        this.checkOutSetting = checkOutSetting;
        this.invoiceRepository = invoiceRepository;
    }

    public Invoice createInvoice(User user, ItemList itemList) {
        float totalAmount = Float.parseFloat((itemList.getItems().stream().mapToDouble(Item::getAmount).sum()) + "");

        if (totalAmount == 0) {
            return null;
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceDescription("Invoice generated on " + LocalDate.now() + " for the payment of tasks submitted in the last month.");
        invoice.setUserName(user.getName());
        invoice.setUserSurname(user.getSurname());
        invoice.setUserEmail(user.getUserEmail());
        invoice.setUserResidenceCity(user.getResidenceCity());
        invoice.setUserResidenceAddress(user.getResidenceAddress());
        invoice.setInvoicePaymentMethod("Credit card");
        invoice.setInvoiceItems(itemList);
        invoice.setInvoicePartialAmount(checkOutSetting.convertCreditsToMoney(totalAmount));
        invoice.setInvoiceDelayAmount(0.0F);
        invoice.setInvoiceTotalAmount(invoice.getInvoicePartialAmount() + invoice.getInvoiceDelayAmount());
        invoice.setInvoiceStatus(Invoice.Status.PENDING);
        invoice.setInvoiceOverdueDate(LocalDateTime.now().plusDays(15));

        return invoiceRepository.save(invoice);
    }
}
