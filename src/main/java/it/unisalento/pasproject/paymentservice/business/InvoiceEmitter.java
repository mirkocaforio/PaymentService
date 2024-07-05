package it.unisalento.pasproject.paymentservice.business;

import it.unisalento.pasproject.paymentservice.controller.InvoiceController;
import it.unisalento.pasproject.paymentservice.domain.Invoice;
import it.unisalento.pasproject.paymentservice.domain.ItemList;
import it.unisalento.pasproject.paymentservice.domain.User;
import it.unisalento.pasproject.paymentservice.dto.GeneralRequestDTO;
import it.unisalento.pasproject.paymentservice.dto.NotificationMessageDTO;
import it.unisalento.pasproject.paymentservice.dto.TransactionRequestMessageDTO;
import it.unisalento.pasproject.paymentservice.repositories.UserRepository;
import it.unisalento.pasproject.paymentservice.service.NotificationMessageHandler;
import it.unisalento.pasproject.paymentservice.service.PaymentMessageHandler;
import it.unisalento.pasproject.paymentservice.service.WalletMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final UserRepository userRepository;
    private final CreditCardValidationStrategy creditCardValidationStrategy;
    private final InvoiceFactory invoiceFactory;
    private final NotificationMessageHandler notificationMessageHandler;
    private final PaymentMessageHandler paymentMessageHandler;
    private final WalletMessageHandler walletMessageHandler;

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceEmitter.class);

    @Autowired
    public InvoiceEmitter(UserRepository userRepository, CreditCardValidationStrategy creditCardValidationStrategy, InvoiceFactory invoiceFactory, NotificationMessageHandler notificationMessageHandler, PaymentMessageHandler paymentMessageHandler, WalletMessageHandler walletMessageHandler) {
        this.userRepository = userRepository;
        this.creditCardValidationStrategy = creditCardValidationStrategy;
        this.invoiceFactory = invoiceFactory;
        this.notificationMessageHandler = notificationMessageHandler;
        this.paymentMessageHandler = paymentMessageHandler;
        this.walletMessageHandler = walletMessageHandler;
    }

    public boolean isInvoiceGenerationDay(LocalDate registrationDate, LocalDate currentDate) {
        YearMonth registrationYearMonth = YearMonth.from(registrationDate);
        YearMonth currentYearMonth = YearMonth.from(currentDate);

        return currentYearMonth.isAfter(registrationYearMonth) && registrationDate.getDayOfMonth() == currentDate.getDayOfMonth();
    }

    public NotificationMessageDTO createNotificationMessage(User user, Invoice invoice) {
        String notificationTitle = "Invoice # " + invoice.getInvoiceNumber();
        String notificationBody = String.format("Invoice #%s has been issued on %s.\n The total amount is %.2f. The payment is due by %s.\n Please check your Invoice Section on your profile for details.\n\n GreenSpot Team",
                invoice.getInvoiceNumber(),
                invoice.getInvoicePaymentDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                invoice.getInvoiceTotalAmount(),
                invoice.getInvoiceOverdueDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));

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

        LOGGER.info("Emitting invoices for {} users", users.size());

        for (User user : users) {
            LocalDate registrationDate = user.getRegistrationDate().toLocalDate();
            LocalDateTime currentDate = LocalDateTime.now();

            //if (isInvoiceGenerationDay(registrationDate, currentDate.toLocalDate())) {
                if (creditCardValidationStrategy.isPaymentMethodValid(user)) {
                    TransactionRequestMessageDTO transactionRequestMessageDTO = new TransactionRequestMessageDTO();
                    transactionRequestMessageDTO.setUserEmail(user.getUserEmail());
                    transactionRequestMessageDTO.setFrom(currentDate.minusMonths(1));
                    transactionRequestMessageDTO.setTo(currentDate);

                    LOGGER.info("Requesting invoice items for user {}", user.getUserEmail());

                    ItemList itemList = paymentMessageHandler.requestInvoiceItems(transactionRequestMessageDTO);

                    if (itemList == null) {
                        continue;
                    }

                    LOGGER.info("Received invoice items for user: items {}", itemList.getItems().size());

                    Invoice invoice = invoiceFactory.createInvoice(user, itemList);

                    LOGGER.info("Invoice created for user: {}", invoice.getInvoiceStatus());

                    NotificationMessageDTO notificationMessageDTO = createNotificationMessage(user, invoice);

                    LOGGER.info("Notification message created for user: {}", notificationMessageDTO.getSubject());

                    notificationMessageHandler.sendNotificationMessage(notificationMessageDTO);

                    LOGGER.info("Notification message sent for user: {}", user.getUserEmail());
                }

                GeneralRequestDTO generalRequestDTO = new GeneralRequestDTO();

                generalRequestDTO.setEmail(user.getUserEmail());
                generalRequestDTO.setRequestType(GeneralRequestDTO.RequestType.REFILL);

                LOGGER.info("Requesting wallet refill for user: {}", generalRequestDTO.getEmail());

                walletMessageHandler.sendRefillMessage(generalRequestDTO);

                LOGGER.info("Wallet refill request sent for user: {}", user.getUserEmail());
            //}
        }
    }
}
