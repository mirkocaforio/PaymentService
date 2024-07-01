package it.unisalento.pasproject.paymentservice.service;


import it.unisalento.pasproject.paymentservice.business.exchanger.MessageExchangeStrategy;
import it.unisalento.pasproject.paymentservice.business.exchanger.MessageExchanger;
import it.unisalento.pasproject.paymentservice.dto.UserDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static it.unisalento.pasproject.paymentservice.security.SecurityConstants.ROLE_ADMIN;

/**
 * The UserCheckService class provides methods for checking user details.
 * It includes methods for loading user details by username, checking user roles, and checking if a user is enabled.
 * It uses a MessageExchanger for exchanging messages and a MessageExchangeStrategy for defining the message exchange strategy.
 */
@Service
public class UserCheckService {
    /**
     * The MessageExchanger used for exchanging messages.
     */
    private final MessageExchanger messageExchanger;

    /**
     * The name of the security exchange.
     */
    @Value("${rabbitmq.exchange.security.name}")
    private String securityExchange;

    /**
     * The routing key for security requests.
     */
    @Value("${rabbitmq.routing.security.key}")
    private String securityRequestRoutingKey;

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCheckService.class);

    @Autowired
    public UserCheckService(MessageExchanger messageExchanger, @Qualifier("RabbitMQExchange") MessageExchangeStrategy messageExchangeStrategy) {
        this.messageExchanger = messageExchanger;
        this.messageExchanger.setStrategy(messageExchangeStrategy);
    }

    /**
     * Loads user details by username.
     * @param email The email of the user.
     * @return The UserDetailsDTO of the user.
     * @throws UsernameNotFoundException if the user is not found.
     */
    public UserDetailsDTO loadUserByUsername(String email) throws UsernameNotFoundException {
        // MQTT call to CQRS to get user details
        UserDetailsDTO user = null;

        try {
            user = messageExchanger.exchangeMessage(email,securityRequestRoutingKey,securityExchange,UserDetailsDTO.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        if(user != null) {
            LOGGER.info(String.format("User %s found with role: %s and enabled %s", user.getEmail(), user.getRole(), user.getEnabled()));
        }

        return user;
    }

    /**
     * Checks if a user is enabled.
     * @param enable The enabled status of the user.
     * @return The enabled status of the user.
     */
    public Boolean isEnable(Boolean enable) {
        return enable;
    }

    /**
     * Check if the current user is the user with the given email
     * @param email the email of the user to check
     * @return true if the current user is the user with the given email, false otherwise
     */
    public Boolean isCorrectUser(String email){
        return email.equals(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    /**
     * Check if the current user is an administrator
     * @return true if the current user is an administrator, false otherwise
     */
    public Boolean isAdministrator(){
        String currentRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        return currentRole.equalsIgnoreCase(ROLE_ADMIN);
    }
}
