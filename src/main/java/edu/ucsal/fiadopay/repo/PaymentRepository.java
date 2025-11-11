package edu.ucsal.fiadopay.repo;
import edu.ucsal.fiadopay.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PaymentRepository extends JpaRepository<Payment, String> {
  Optional<Payment> findByIdempotencyKeyAndMerchantId(String ik, Long mid);
}
