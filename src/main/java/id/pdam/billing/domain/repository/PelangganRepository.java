package id.pdam.billing.domain.repository;

import id.pdam.billing.domain.entity.Pelanggan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PelangganRepository extends JpaRepository<Pelanggan, Long> {
    Optional<Pelanggan> findByNomorPelanggan(String nomorPelanggan);
    boolean existsByNomorPelanggan(String nomorPelanggan);
}
