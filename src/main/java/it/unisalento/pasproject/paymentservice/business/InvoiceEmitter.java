package it.unisalento.pasproject.paymentservice.business;

import it.unisalento.pasproject.paymentservice.domain.Invoice;
import it.unisalento.pasproject.paymentservice.domain.Item;
import it.unisalento.pasproject.paymentservice.domain.ItemList;
import it.unisalento.pasproject.paymentservice.domain.User;
import it.unisalento.pasproject.paymentservice.dto.GeneralRequestDTO;
import it.unisalento.pasproject.paymentservice.dto.NotificationMessageDTO;
import it.unisalento.pasproject.paymentservice.dto.TransactionRequestMessageDTO;
import it.unisalento.pasproject.paymentservice.repositories.InvoiceRepository;
import it.unisalento.pasproject.paymentservice.repositories.UserRepository;
import it.unisalento.pasproject.paymentservice.service.CheckOutSetting;
import it.unisalento.pasproject.paymentservice.service.NotificationMessageHandler;
import it.unisalento.pasproject.paymentservice.service.PaymentMessageHandler;
import it.unisalento.pasproject.paymentservice.service.WalletMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static it.unisalento.pasproject.paymentservice.service.NotificationConstants.INVOICE_NOTIFICATION_TYPE;

@Component
public class InvoiceEmitter {
    private final CheckOutSetting checkOutSetting;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationMessageHandler notificationMessageHandler;
    private final PaymentMessageHandler paymentMessageHandler;
    private final WalletMessageHandler walletMessageHandler;

    @Autowired
    public InvoiceEmitter(CheckOutSetting checkOutSetting, UserRepository userRepository, InvoiceRepository invoiceRepository, NotificationMessageHandler notificationMessageHandler, PaymentMessageHandler paymentMessageHandler, WalletMessageHandler walletMessageHandler) {
        this.checkOutSetting = checkOutSetting;
        this.userRepository = userRepository;
        this.invoiceRepository = invoiceRepository;
        this.notificationMessageHandler = notificationMessageHandler;
        this.paymentMessageHandler = paymentMessageHandler;
        this.walletMessageHandler = walletMessageHandler;
    }

    public boolean isInvoiceGenerationDay(LocalDate registrationDate, LocalDate currentDate) {
        YearMonth registrationYearMonth = YearMonth.from(registrationDate);
        YearMonth currentYearMonth = YearMonth.from(currentDate);

        if (currentYearMonth.isAfter(registrationYearMonth)) {
            return registrationDate.getDayOfMonth() == currentDate.getDayOfMonth();
        }

        return false;
    }

    public boolean isPaymentMethodValid(User user) {
        YearMonth currentDate = YearMonth.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth cardExpiryDate = YearMonth.parse(user.getCardExpiryDate(), formatter);

        return cardExpiryDate.isAfter(currentDate) || cardExpiryDate.equals(currentDate);
    }

    public Invoice crateInvoice(User user, ItemList itemList) {
        float totalAmount = Float.parseFloat((itemList.getItems().stream().mapToDouble(Item::getAmount).sum()) + "");

        if (totalAmount == 0) {
            return null;
        }

        Invoice invoice = new Invoice();

        invoice.setInvoiceDescription("Invoice generated on " + LocalDate.now() + " for the payment of tasks submitted in the last month.");
        invoice.setUserEmail(user.getUserEmail());
        invoice.setInvoicePaymentMethod("Credit card");
        invoice.setInvoicePaymentDate(LocalDateTime.now());
        invoice.setInvoiceItems(itemList);
        invoice.setInvoiceAmount(checkOutSetting.convertCreditsToMoney(totalAmount));
        invoice.setInvoiceStatus(Invoice.Status.PENDING);
        invoice.setInvoiceOverdueDate(LocalDateTime.now().plusDays(15));

        invoice = invoiceRepository.save(invoice);

        return invoice;

    }

    public NotificationMessageDTO createNotificationMessage(User user, Invoice invoice) {
        String notificationTitle = "Invoice # " + invoice.getInvoiceNumber();
        String notificationBody = String.format("Invoice #%s has been issued on %s.\n The total amount is %.2f. The payment is due by %s.\n Please check your Invoice Section on your profile for details.\n\n GreenSpot Team",
                invoice.getInvoiceNumber(),
                invoice.getInvoicePaymentDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                invoice.getInvoiceAmount(),
                invoice.getInvoiceOverdueDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));

        //TODO: CAPIRE SE MANDARE ATTACHMENT O MENO

        return notificationMessageHandler.buildNotificationMessage(
                user.getUserEmail(),
                notificationBody,
                notificationTitle,
                null,
                INVOICE_NOTIFICATION_TYPE,
                true,
                false
        );
    }

    //TODO: DA SISTEMARE CON LA LISTA DI ITEM DA RECUPERARE DAL TRANSACTION SERVICE
    public void emitInvoice() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            LocalDate registrationDate = user.getRegistrationDate().toLocalDate();
            LocalDateTime currentDate = LocalDateTime.now();

            if (isInvoiceGenerationDay(registrationDate, currentDate.toLocalDate())) {
                if (isPaymentMethodValid(user)) {
                    TransactionRequestMessageDTO transactionRequestMessageDTO = new TransactionRequestMessageDTO();
                    transactionRequestMessageDTO.setUserEmail(user.getUserEmail());
                    transactionRequestMessageDTO.setFrom(currentDate.minusMonths(1));
                    transactionRequestMessageDTO.setTo(currentDate);

                    ItemList itemList = paymentMessageHandler.requestInvoiceItems(transactionRequestMessageDTO);

                    if (itemList == null) {
                        continue;
                    }

                    Invoice invoice = crateInvoice(user, itemList);

                    NotificationMessageDTO notificationMessageDTO = createNotificationMessage(user, invoice);

                    notificationMessageHandler.sendNotificationMessage(notificationMessageDTO);
                }

                GeneralRequestDTO generalRequestDTO = new GeneralRequestDTO();

                generalRequestDTO.setEmail(user.getUserEmail());
                generalRequestDTO.setRequestType(GeneralRequestDTO.RequestType.REFILL);

                walletMessageHandler.sendRefillMessage(generalRequestDTO);
            }
        }
    }
}
