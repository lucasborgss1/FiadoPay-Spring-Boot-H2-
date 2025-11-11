package edu.ucsal.fiadopay.controller;

import jakarta.validation.constraints.NotBlank;

public record RefundRequest(
    @NotBlank String paymentId
) {}
