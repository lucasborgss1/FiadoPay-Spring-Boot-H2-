package edu.ucsal.fiadopay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ucsal.fiadopay.domain.Payment;
import edu.ucsal.fiadopay.domain.WebhookDelivery;
import edu.ucsal.fiadopay.repo.MerchantRepository;
import edu.ucsal.fiadopay.repo.WebhookDeliveryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
public class WebhookDeliveryService {
    private final WebhookDeliveryRepository deliveries;
    private final MerchantRepository merchants;
    private final ObjectMapper objectMapper;
    private final WebhookAsyncAgent asyncAgent;

    @Value("${fiadopay.webhook-secret}")
    private String secret;

    public WebhookDeliveryService(WebhookDeliveryRepository deliveries, MerchantRepository merchants, ObjectMapper objectMapper, WebhookAsyncAgent asyncAgent) {
        this.deliveries = deliveries;
        this.merchants = merchants;
        this.objectMapper = objectMapper;
        this.asyncAgent = asyncAgent;
    }

    private static String hmac(String payload, String secret){
        try {
            var mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
            return Base64.getEncoder().encodeToString(mac.doFinal(payload.getBytes()));
        } catch (Exception e){
            return "";
        }
    }

    public void sendWebhook(Payment p){
        var merchant = merchants.findById(p.getMerchantId()).orElse(null);
        if (merchant==null || merchant.getWebhookUrl()==null || merchant.getWebhookUrl().isBlank()) return;

        String payload;
        try {
            var data = Map.of(
                    "paymentId", p.getId(),
                    "status", p.getStatus().name(),
                    "occurredAt", Instant.now().toString()
            );
            var event = Map.of(
                    "id", "evt_"+ UUID.randomUUID().toString().substring(0,8),
                    "type", "payment.updated",
                    "data", data
            );
            payload = objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            // fallback mínimo: não envia webhook se falhar a serialização
            return;
        }

        var signature = hmac(payload, secret);

        var delivery = deliveries.save(WebhookDelivery.builder()
                .eventId("evt_"+UUID.randomUUID().toString().substring(0,8))
                .eventType("payment.updated")
                .paymentId(p.getId())
                .targetUrl(merchant.getWebhookUrl())
                .signature(signature)
                .payload(payload)
                .attempts(0)
                .delivered(false)
                .lastAttemptAt(null)
                .build());

        this.asyncAgent.tryDeliver(delivery.getId());
    }
}
