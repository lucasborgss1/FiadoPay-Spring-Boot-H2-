package edu.ucsal.fiadopay.controller;

import edu.ucsal.fiadopay.domain.Merchant;
import edu.ucsal.fiadopay.repo.MerchantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/fiadopay/admin/merchants")
@RequiredArgsConstructor
public class MerchantAdminController {
  private final MerchantRepository merchants;

  @PostMapping
  public Merchant create(@Valid @RequestBody MerchantCreateDTO dto) {
    if (merchants.existsByName(dto.name())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Merchant name already exists");
    }
    var m = Merchant.builder()
        .name(dto.name())
        .webhookUrl(dto.webhookUrl())
        .clientId(UUID.randomUUID().toString())
        .clientSecret(UUID.randomUUID().toString().replace("-", ""))
        .status(Merchant.Status.ACTIVE)
        .build();
    return merchants.save(m);
  }
}
