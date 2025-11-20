package edu.ucsal.fiadopay.service.processor;

import edu.ucsal.fiadopay.annotation.PaymentMethod;
import edu.ucsal.fiadopay.domain.Payment;
import org.springframework.stereotype.Service;

@Service
@PaymentMethod(type = "PIX")
public class PixProcessor implements PaymentProcessor{

    @Override
    public Payment process(Payment payment) {
        payment.setStatus(Payment.Status.PENDING);
        payment.setTotalWithInterest(payment.getAmount());

        return payment;
    }
}
