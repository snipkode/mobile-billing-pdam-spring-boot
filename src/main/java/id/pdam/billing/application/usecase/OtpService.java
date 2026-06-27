package id.pdam.billing.application.usecase;

import id.pdam.billing.domain.entity.OtpToken;
import id.pdam.billing.domain.repository.OtpTokenRepository;
import id.pdam.billing.infrastructure.waha.WahaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final WahaService wahaService;

    @Value("${app.otp.expiry-minutes}") private int expiryMinutes;

    private static final SecureRandom RANDOM = new SecureRandom();

    public void kirimOtp(String telepon, String tujuan) {
        String kode = String.format("%06d", RANDOM.nextInt(1_000_000));
        otpTokenRepository.save(OtpToken.builder()
            .telepon(telepon)
            .kode(kode)
            .tujuan(tujuan)
            .expiredAt(LocalDateTime.now().plusMinutes(expiryMinutes))
            .build());
        wahaService.sendOtp(telepon, kode);
    }

    public boolean verifikasiOtp(String telepon, String kode, String tujuan) {
        return otpTokenRepository
            .findTopByTeleponAndTujuanAndUsedFalseOrderByExpiredAtDesc(telepon, tujuan)
            .filter(otp -> otp.getKode().equals(kode))
            .filter(otp -> otp.getExpiredAt().isAfter(LocalDateTime.now()))
            .map(otp -> { otp.setUsed(true); otpTokenRepository.save(otp); return true; })
            .orElse(false);
    }
}
