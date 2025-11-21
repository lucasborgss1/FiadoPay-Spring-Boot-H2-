package edu.ucsal.fiadopay.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record RefundRequest(
    @NotBlank String paymentId
) {}
