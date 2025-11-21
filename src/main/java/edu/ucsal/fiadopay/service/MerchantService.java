package edu.ucsal.fiadopay.service;

import edu.ucsal.fiadopay.controller.dto.TokenResponse;
import edu.ucsal.fiadopay.domain.Merchant;
import edu.ucsal.fiadopay.repo.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchants;

    public Merchant createNewMerchant(String name, String webhookUrl){
        if (merchants.existsByName(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Merchant name already exists");
        }

        var m = Merchant.builder()
                .name(name)
                .webhookUrl(webhookUrl)
                .clientId(UUID.randomUUID().toString())
                .clientSecret(UUID.randomUUID().toString().replace("-", ""))
                .status(Merchant.Status.ACTIVE)
                .build();

        return merchants.save(m);
    }

    public TokenResponse authenticate(String clientId, String clientSecret) {
        var merchant = merchants.findByClientId(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!merchant.getClientSecret().equals(clientSecret)
                || merchant.getStatus()!= Merchant.Status.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return new TokenResponse("FAKE-"+merchant.getId(), "Bearer", 3600);
    }

    public Merchant getMerchantFromAuthToken(String auth) {
        if (auth == null || !auth.startsWith("Bearer FAKE-")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        var raw = auth.substring("Bearer FAKE-".length());
        long id;
        try {
            id = Long.parseLong(raw);
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        var merchant = merchants.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (merchant.getStatus() != Merchant.Status.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return merchant;
    }
}