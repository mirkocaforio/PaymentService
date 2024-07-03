package it.unisalento.pasproject.paymentservice.business;

import it.unisalento.pasproject.paymentservice.domain.User;

public interface PaymentValidationStrategy {
    boolean isPaymentMethodValid(User user);
}
