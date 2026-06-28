package id.pdam.billing.application.usecase;

import id.pdam.billing.domain.repository.PelangganRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LupaPasswordService {

    private final PelangganRepository pelangganRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    public void kirimOtp(String nomorPelanggan) {
        var p = pelangganRepository.findByNomorPelanggan(nomorPelanggan)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nomor pelanggan tidak ditemukan"));
        otpService.kirimOtp(p.getTelepon(), "LUPA_PASSWORD");
    }

    public void kirimOtpLogin(String nomorPelanggan) {
        var p = pelangganRepository.findByNomorPelanggan(nomorPelanggan)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nomor pelanggan tidak ditemukan"));
        otpService.kirimOtp(p.getTelepon(), "LOGIN_OTP");
    }

    @Transactional
    public void resetPassword(String nomorPelanggan, String kode, String passwordBaru) {
        var p = pelangganRepository.findByNomorPelanggan(nomorPelanggan)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nomor pelanggan tidak ditemukan"));
        if (!otpService.verifikasiOtp(p.getTelepon(), kode, "LUPA_PASSWORD"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP tidak valid atau kadaluarsa");
        p.setPassword(passwordEncoder.encode(passwordBaru));
        pelangganRepository.save(p);
    }
}
