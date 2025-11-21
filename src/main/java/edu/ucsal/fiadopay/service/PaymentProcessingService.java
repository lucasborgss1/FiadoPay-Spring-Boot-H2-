package edu.ucsal.fiadopay.service;

import edu.ucsal.fiadopay.domain.Payment;
import edu.ucsal.fiadopay.repo.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PaymentProcessingService {

    private final PaymentRepository payments;
    private final WebhookDeliveryService webhookService;

    @Value("${fiadopay.processing-delay-ms}")
    private long delay;
    @Value("${fiadopay.failure-rate}")
    private double failRate;

    public PaymentProcessingService(PaymentRepository payments, WebhookDeliveryService webhookService) {
        this.payments = payments;
        this.webhookService = webhookService;
    }

    @Async("paymentExecutor")
    public void processPaymentAsync(String paymentId){
        try { Thread.sleep(delay); } catch (InterruptedException ignored) {}
        var p = payments.findById(paymentId).orElse(null);
        if (p==null) return;

        System.out.println("LOG: Processando pagamento " + p.getId() + " na thread: " + Thread.currentThread().getName());

        var approved = Math.random() > failRate;
        p.setStatus(approved ? Payment.Status.APPROVED : Payment.Status.DECLINED);
        p.setUpdatedAt(Instant.now());
        payments.save(p);

        webhookService.sendWebhook(p);
    }
}
