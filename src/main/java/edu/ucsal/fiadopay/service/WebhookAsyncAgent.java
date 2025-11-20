package edu.ucsal.fiadopay.service;

import edu.ucsal.fiadopay.repo.WebhookDeliveryRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

@Component
public class WebhookAsyncAgent {

    private final WebhookDeliveryRepository deliveries;

    public WebhookAsyncAgent(WebhookDeliveryRepository deliveries) {
        this.deliveries = deliveries;
    }

    @Async("webhookExecutor")
    public void tryDeliver(Long deliveryId){
        var d = deliveries.findById(deliveryId).orElse(null);
        if (d==null) return;

        System.out.println("LOG: Tentando entrega do Webhook " + d.getEventId() + " na thread: " + Thread.currentThread().getName());

        try {
            var client = HttpClient.newHttpClient();
            var req = HttpRequest.newBuilder(URI.create(d.getTargetUrl()))
                    .header("Content-Type","application/json")
                    .header("X-Event-Type", d.getEventType())
                    .header("X-Signature", d.getSignature())
                    .POST(HttpRequest.BodyPublishers.ofString(d.getPayload()))
                    .build();
            var res = client.send(req, HttpResponse.BodyHandlers.ofString());


            d.setAttempts(d.getAttempts()+1);
            d.setLastAttemptAt(Instant.now());
            d.setDelivered(res.statusCode()>=200 && res.statusCode()<300);
            deliveries.save(d);

            if(!d.isDelivered() && d.getAttempts()<5){
                Thread.sleep(1000L * d.getAttempts());
                tryDeliver(deliveryId);
            }
        } catch (Exception e){
            d.setAttempts(d.getAttempts()+1);
            d.setLastAttemptAt(Instant.now());
            d.setDelivered(false);
            deliveries.save(d);
            if (d.getAttempts()<5){
                try {
                    Thread.sleep(1000L * d.getAttempts());
                } catch (InterruptedException ignored) {}
                tryDeliver(deliveryId);
            }
        }
    }
}
