package id.pdam.billing.domain.repository;

import id.pdam.billing.domain.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByTeleponAndTujuanAndUsedFalseOrderByExpiredAtDesc(String telepon, String tujuan);
}
