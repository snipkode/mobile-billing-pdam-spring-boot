package id.pdam.billing.domain.repository;

import id.pdam.billing.domain.entity.Notifikasi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotifikasiRepository extends JpaRepository<Notifikasi, Long> {
    List<Notifikasi> findByPelangganIdOrderByCreatedAtDesc(Long pelangganId);
    long countByPelangganIdAndUnreadTrue(Long pelangganId);
}
