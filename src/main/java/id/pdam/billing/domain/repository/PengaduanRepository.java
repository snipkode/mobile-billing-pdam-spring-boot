package id.pdam.billing.domain.repository;

import id.pdam.billing.domain.entity.Pengaduan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PengaduanRepository extends JpaRepository<Pengaduan, Long> {
    List<Pengaduan> findByPelangganIdOrderByCreatedAtDesc(Long pelangganId);
    Optional<Pengaduan> findByNomorTiket(String nomorTiket);
}
