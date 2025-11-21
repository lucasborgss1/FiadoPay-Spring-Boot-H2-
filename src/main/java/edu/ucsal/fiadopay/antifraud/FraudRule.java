package edu.ucsal.fiadopay.antifraud;

import edu.ucsal.fiadopay.domain.Payment;

public interface FraudRule {
    boolean evaluate(Payment payment);
}
