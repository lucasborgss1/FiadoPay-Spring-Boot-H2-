package edu.ucsal.fiadopay.service;

import edu.ucsal.fiadopay.controller.PaymentRequest;
import edu.ucsal.fiadopay.controller.PaymentResponse;
import edu.ucsal.fiadopay.domain.Payment;
import edu.ucsal.fiadopay.repo.PaymentRepository;
import edu.ucsal.fiadopay.scanner.PaymentProcessorScanner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {
  private final PaymentRepository payments;
  private final WebhookDeliveryService webhookService;
  private final PaymentProcessorScanner processorScanner;
  private final PaymentProcessingService processingService;
  private final MerchantService merchantService;

  public PaymentService(MerchantService merchantService, PaymentRepository payments, WebhookDeliveryService webhookService, PaymentProcessorScanner processorScanner, PaymentProcessingService processingService) {
    this.merchantService = merchantService;
    this.payments = payments;
    this.webhookService = webhookService;
    this.processorScanner = processorScanner;
    this.processingService = processingService;
  }

  @Transactional
  public PaymentResponse createPayment(String auth, String idemKey, PaymentRequest req){
    var merchant = merchantService.getMerchantFromAuthToken(auth);
    var mid = merchant.getId();

    if (idemKey != null) {
      var existing = payments.findByIdempotencyKeyAndMerchantId(idemKey, mid);
      if(existing.isPresent()) return toResponse(existing.get());
    }

    var payment = Payment.builder()
            .id("pay_"+UUID.randomUUID().toString().substring(0,8))
            .merchantId(mid)
            .method(req.method().toUpperCase())
            .amount(req.amount())
            .currency(req.currency())
            .installments(req.installments()==null?1:req.installments())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .idempotencyKey(idemKey)
            .metadataOrderId(req.metadataOrderId())
            .build();

    var processor = processorScanner.getProcessor(payment.getMethod());
    payment = processor.process(payment);

    payments.save(payment);

    if (payment.getStatus() == Payment.Status.PENDING){
      processingService.processPaymentAsync(payment.getId());
    }

    return toResponse(payment);
  }

  public PaymentResponse getPayment(String id){
    return toResponse(payments.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
  }

  public Map<String,Object> refund(String auth, String paymentId){
    var merchant = merchantService.getMerchantFromAuthToken(auth);
    var p = payments.findById(paymentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if (!merchant.getId().equals(p.getMerchantId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
    p.setStatus(Payment.Status.REFUNDED);
    p.setUpdatedAt(Instant.now());
    payments.save(p);
    webhookService.sendWebhook(p);
    return Map.of("id","ref_"+UUID.randomUUID(),"status","PENDING");
  }

  private PaymentResponse toResponse(Payment p){
    return new PaymentResponse(
            p.getId(), p.getStatus().name(), p.getMethod(),
            p.getAmount(), p.getInstallments(), p.getMonthlyInterest(),
            p.getTotalWithInterest()
    );
  }
}
