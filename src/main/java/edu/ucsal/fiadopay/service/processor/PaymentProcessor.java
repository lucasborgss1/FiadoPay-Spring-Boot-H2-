package edu.ucsal.fiadopay.service.processor;

import edu.ucsal.fiadopay.domain.Payment;

public interface PaymentProcessor {
    Payment process(Payment payment);
}
