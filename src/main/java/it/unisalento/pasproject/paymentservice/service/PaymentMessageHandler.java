package it.unisalento.pasproject.paymentservice.service;

import it.unisalento.pasproject.paymentservice.business.exchanger.MessageExchanger;
import it.unisalento.pasproject.paymentservice.domain.ItemList;
import it.unisalento.pasproject.paymentservice.domain.User;
import it.unisalento.pasproject.paymentservice.dto.PaymentInfoMessageDTO;
import it.unisalento.pasproject.paymentservice.dto.TransactionRequestMessageDTO;
import it.unisalento.pasproject.paymentservice.repositories.InvoiceRepository;
import it.unisalento.pasproject.paymentservice.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentMessageHandler {
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final MessageExchanger messageExchanger;

    @Value("${rabbitmq.exchange.transaction.name}")
    private String transactionExchange;

    @Value("${rabbitmq.routing.requestTransaction.key}")
    private String transactionTopic;

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentMessageHandler.class);

    @Autowired
    public PaymentMessageHandler(UserRepository userRepository, InvoiceRepository invoiceRepository, MessageExchanger messageExchanger) {
        this.userRepository = userRepository;
        this.invoiceRepository = invoiceRepository;
        this.messageExchanger = messageExchanger;
    }

    public User getUserInfo(User user, PaymentInfoMessageDTO paymentInfoMessageDTO) {
        Optional.ofNullable(paymentInfoMessageDTO.getName()).ifPresent(user::setName);
        Optional.ofNullable(paymentInfoMessageDTO.getSurname()).ifPresent(user::setSurname);
        Optional.ofNullable(paymentInfoMessageDTO.getEmail()).ifPresent(user::setUserEmail);
        Optional.ofNullable(paymentInfoMessageDTO.getRegistrationDate()).ifPresent(user::setRegistrationDate);
        Optional.of(paymentInfoMessageDTO.isEnabled()).ifPresent(user::setEnabled);
        Optional.ofNullable(paymentInfoMessageDTO.getResidenceAddress()).ifPresent(user::setResidenceAddress);
        Optional.ofNullable(paymentInfoMessageDTO.getResidenceCity()).ifPresent(user::setResidenceCity);
        Optional.ofNullable(paymentInfoMessageDTO.getPhoneNumber()).ifPresent(user::setPhoneNumber);
        Optional.ofNullable(paymentInfoMessageDTO.getCardNumber()).ifPresent(user::setCardNumber);
        Optional.ofNullable(paymentInfoMessageDTO.getCardExpiryDate()).ifPresent(user::setCardExpiryDate);
        Optional.ofNullable(paymentInfoMessageDTO.getCardCvv()).ifPresent(user::setCardCvv);

        return user;
    }

    @RabbitListener(queues = "${rabbitmq.queue.user.name}")
    public void receiveUserMessage(PaymentInfoMessageDTO paymentInfoMessageDTO) {
        try {
            LOGGER.info("Received message: {}", paymentInfoMessageDTO.toString());
            Optional<User> user = userRepository.findByUserEmail(paymentInfoMessageDTO.getEmail());

            if (user.isPresent()) {
                User userToUpdate = user.get();

                userRepository.save(getUserInfo(userToUpdate, paymentInfoMessageDTO));
                LOGGER.info("User updated: {}", userToUpdate.getId());
            } else {
                User newUser = new User();

                userRepository.save(getUserInfo(newUser, paymentInfoMessageDTO));
                LOGGER.info("User created: {}", newUser.getId());
            }
        } catch (Exception e) {
            LOGGER.error("Error: {}", e.getMessage());
        }
    }

    public ItemList requestInvoiceItems(TransactionRequestMessageDTO message) {
        return messageExchanger.exchangeMessage(message, transactionTopic, transactionExchange, ItemList.class);
    }

}
