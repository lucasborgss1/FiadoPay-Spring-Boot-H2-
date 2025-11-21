package edu.ucsal.fiadopay.controller;

import edu.ucsal.fiadopay.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fiadopay/auth")
@RequiredArgsConstructor
public class AuthController {
  private final MerchantService merchantService;

  @PostMapping("/token")
  public TokenResponse token(@RequestBody @Valid TokenRequest req) {
    return merchantService.authenticate(req.client_id(), req.client_secret());
  }
}
