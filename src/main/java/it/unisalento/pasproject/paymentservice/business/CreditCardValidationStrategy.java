package it.unisalento.pasproject.paymentservice.business;

import it.unisalento.pasproject.paymentservice.domain.User;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Component
public class CreditCardValidationStrategy implements PaymentValidationStrategy {
    @Override
    public boolean isPaymentMethodValid(User user) {
        YearMonth currentDate = YearMonth.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth cardExpiryDate = YearMonth.parse(user.getCardExpiryDate(), formatter);
        return cardExpiryDate.isAfter(currentDate) || cardExpiryDate.equals(currentDate);
    }
}
