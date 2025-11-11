package edu.ucsal.fiadopay.repo;
import edu.ucsal.fiadopay.domain.WebhookDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
public interface WebhookDeliveryRepository extends JpaRepository<WebhookDelivery, Long> { }
