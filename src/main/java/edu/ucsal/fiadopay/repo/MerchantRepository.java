package edu.ucsal.fiadopay.repo;
import edu.ucsal.fiadopay.domain.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
  Optional<Merchant> findByClientId(String clientId);
  boolean existsByName(String name);
}
