package edu.ucsal.fiadopay.service.processor;

import edu.ucsal.fiadopay.annotation.PaymentMethod;
import edu.ucsal.fiadopay.antifraud.FraudRule;
import edu.ucsal.fiadopay.scanner.AntiFraudScanner;
import edu.ucsal.fiadopay.domain.Payment;
import edu.ucsal.fiadopay.service.InstallmentService;
import org.springframework.stereotype.Service;

@Service
@PaymentMethod(type = "CARD")
public class CardProcessor implements PaymentProcessor{
    private final InstallmentService installmentService;
    private final AntiFraudScanner antiFraudScanner;

    public CardProcessor(InstallmentService installmentService, AntiFraudScanner antiFraudScanner) {
        this.installmentService = installmentService;
        this.antiFraudScanner = antiFraudScanner;
    }

    @Override
    public Payment process(Payment payment) {
        if (payment.getInstallments() != null && payment.getInstallments() > 1){
            payment.setMonthlyInterest(installmentService.getMonthlyInterestRate(payment.getInstallments()));
            payment.setTotalWithInterest(installmentService.calculateTotalWithInterest(payment.getAmount(), payment.getInstallments()));
        } else {
            payment.setTotalWithInterest(payment.getAmount());
        }

        boolean isFraud = false;
        for (FraudRule rule : antiFraudScanner.getRules()) {
            if (rule.evaluate(payment)) {
                isFraud = true;
                break;
            }
        }

        if (isFraud) {
            payment.setStatus(Payment.Status.DECLINED);
        } else {
            payment.setStatus(Payment.Status.PENDING);
        }
        return payment;
    }
}
