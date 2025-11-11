package edu.ucsal.fiadopay.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class WebhookDelivery {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventId;     // evt_xxx
    private String eventType;   // payment.updated
    private String paymentId;
    private String targetUrl;   // merchant webhook
    private String signature;   // HMAC
    private int attempts;
    private boolean delivered;
    private Instant lastAttemptAt;

    @Lob
    private String payload;
}
