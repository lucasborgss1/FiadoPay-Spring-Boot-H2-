package edu.ucsal.fiadopay.controller;

import edu.ucsal.fiadopay.controller.dto.MerchantCreateRequest;
import edu.ucsal.fiadopay.domain.Merchant;
import edu.ucsal.fiadopay.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fiadopay/admin/merchants")
@RequiredArgsConstructor
public class MerchantAdminController {
  private final MerchantService merchantService;

  @PostMapping
  public Merchant create(@Valid @RequestBody MerchantCreateRequest dto) {
    return merchantService.createNewMerchant(dto.name(), dto.webhookUrl());
  }
}
