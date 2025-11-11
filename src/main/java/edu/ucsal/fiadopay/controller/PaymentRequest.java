package edu.ucsal.fiadopay.controller;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record PaymentRequest(
    @NotBlank @Pattern(regexp = "(?i)CARD|PIX|DEBIT|BOLETO") String method,
    @NotBlank String currency,
    @NotNull @DecimalMin(value = "0.01") @Digits(integer = 17, fraction = 2) BigDecimal amount,
    @Min(1) @Max(12) Integer installments,
    @Size(max = 255) String metadataOrderId
) {}
