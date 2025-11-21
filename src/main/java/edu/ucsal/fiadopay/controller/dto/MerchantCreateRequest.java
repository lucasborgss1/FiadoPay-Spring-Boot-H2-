package edu.ucsal.fiadopay.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MerchantCreateRequest(
    @NotBlank @Size(max = 120) String name,
    @NotBlank String webhookUrl
) {}
