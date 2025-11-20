package edu.ucsal.fiadopay.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class InstallmentService {
    private static final BigDecimal MONTHLY_INTEREST_RATE = new BigDecimal("0.01");
    private static final BigDecimal BASE_FACTOR = new BigDecimal("1").add(MONTHLY_INTEREST_RATE);

    public BigDecimal calculateTotalWithInterest(BigDecimal amount, Integer installments) {
        if (installments == null || installments <= 1) {
            return amount;
        }

        var factor = BASE_FACTOR.pow(installments);

        return amount.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    public Double getMonthlyInterestRate(Integer installments) {
        return (installments != null && installments > 1) ? 1.0 : 0.0;
    }
}
