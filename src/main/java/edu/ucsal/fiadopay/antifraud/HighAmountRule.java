package edu.ucsal.fiadopay.antifraud;

import edu.ucsal.fiadopay.annotation.AntiFraudRule;
import edu.ucsal.fiadopay.domain.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@AntiFraudRule(name = "HighAmount", threshold = 5000.00)
public class HighAmountRule implements FraudRule{

    @Override
    public boolean evaluate(Payment payment) {
        var annotation = this.getClass().getAnnotation(AntiFraudRule.class);

        BigDecimal limit = BigDecimal.valueOf(annotation.threshold());

        boolean isFraud = payment.getTotalWithInterest().compareTo(limit) > 0;

        if (isFraud) {
            System.out.println("LOG: ALERTA FRAUDE: Regra HighAmount violada para o pagamento " + payment.getId());
        }
        return isFraud;
    }
}
