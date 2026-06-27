package id.pdam.billing.domain.repository;

import id.pdam.billing.domain.entity.Tagihan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagihanRepository extends JpaRepository<Tagihan, Long> {
    Optional<Tagihan> findTopByPelangganIdAndStatusOrderByCreatedAtDesc(Long pelangganId, String status);
    List<Tagihan> findByPelangganIdOrderByCreatedAtDesc(Long pelangganId);
}
