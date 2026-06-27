package id.pdam.billing.application.usecase;

import id.pdam.billing.domain.entity.PendaftaranBaru;
import id.pdam.billing.domain.entity.Pelanggan;
import id.pdam.billing.domain.repository.PendaftaranBaruRepository;
import id.pdam.billing.domain.repository.PelangganRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final PelangganRepository pelangganRepository;
    private final PendaftaranBaruRepository pendaftaranBaruRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    /** Case A: Pelanggan lama — verifikasi nomor pelanggan + telepon, set password baru */
    public void registerLama(String nomorPelanggan, String telepon) {
        Pelanggan p = pelangganRepository.findByNomorPelanggan(nomorPelanggan)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nomor pelanggan tidak ditemukan"));
        if (!normalizePhone(p.getTelepon()).equals(normalizePhone(telepon)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nomor telepon tidak sesuai");
        otpService.kirimOtp(telepon, "REGISTER_LAMA");
    }

    @Transactional
    public void setPasswordLama(String telepon, String kode, String passwordBaru) {
        if (!otpService.verifikasiOtp(telepon, kode, "REGISTER_LAMA"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP tidak valid atau kadaluarsa");
        Pelanggan p = pelangganRepository.findAll().stream()
            .filter(x -> normalizePhone(x.getTelepon()).equals(normalizePhone(telepon)))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pelanggan tidak ditemukan"));
        p.setPassword(passwordEncoder.encode(passwordBaru));
        pelangganRepository.save(p);
    }

    /** Case B: Pelanggan baru — submit data, tunggu approval petugas */
    public Long registerBaru(Map<String, String> data) {
        PendaftaranBaru p = pendaftaranBaruRepository.save(PendaftaranBaru.builder()
            .nama(data.get("nama"))
            .telepon(data.get("telepon"))
            .email(data.get("email"))
            .alamat(data.get("alamat"))
            .golongan(data.getOrDefault("golongan", "Rumah Tangga A1"))
            .createdAt(LocalDateTime.now())
            .build());
        // Kirim OTP untuk verifikasi nomor telepon
        otpService.kirimOtp(data.get("telepon"), "REGISTER_BARU");
        return p.getId();
    }

    public void verifikasiRegistrasiBaru(String telepon, String kode) {
        if (!otpService.verifikasiOtp(telepon, kode, "REGISTER_BARU"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP tidak valid atau kadaluarsa");
        // Telepon terverifikasi, tinggal tunggu approval petugas
    }

    private String normalizePhone(String phone) {
        if (phone == null) return "";
        return phone.replaceAll("[^0-9]", "");
    }
}
