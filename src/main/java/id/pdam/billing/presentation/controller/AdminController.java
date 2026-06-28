package id.pdam.billing.presentation.controller;

import id.pdam.billing.application.dto.response.ApiResponse;
import id.pdam.billing.application.dto.response.PengaduanResponse;
import id.pdam.billing.application.dto.response.UserResponse;
import id.pdam.billing.application.mapper.PelangganMapper;
import id.pdam.billing.application.usecase.PengaduanService;
import id.pdam.billing.application.usecase.RegisterService;
import id.pdam.billing.domain.entity.PendaftaranBaru;
import id.pdam.billing.domain.repository.PelangganRepository;
import id.pdam.billing.domain.repository.PendaftaranBaruRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PendaftaranBaruRepository pendaftaranBaruRepository;
    private final RegisterService registerService;
    private final PengaduanService pengaduanService;
    private final PelangganRepository pelangganRepository;
    private final PelangganMapper pelangganMapper;
    private final PasswordEncoder passwordEncoder;

    // === PENDAFTARAN ===
    @GetMapping("/pendaftaran")
    public ResponseEntity<ApiResponse<List<PendaftaranBaru>>> listPendaftaran(
            @RequestParam(defaultValue = "MENUNGGU") String status) {
        return ResponseEntity.ok(ApiResponse.ok(
            pendaftaranBaruRepository.findAll().stream()
                .filter(p -> p.getStatus().equals(status)).toList()));
    }

    @PostMapping("/pendaftaran/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approve(@PathVariable Long id, @RequestBody Map<String, String> body) {
        registerService.approve(id, body.get("nomorPelanggan"), body.get("password"));
        return ResponseEntity.ok(ApiResponse.ok("Pendaftaran disetujui"));
    }

    @PostMapping("/pendaftaran/{id}/tolak")
    public ResponseEntity<ApiResponse<Void>> tolak(@PathVariable Long id) {
        registerService.tolak(id);
        return ResponseEntity.ok(ApiResponse.ok("Pendaftaran ditolak"));
    }

    // === PENGADUAN ===
    @GetMapping("/pengaduan")
    public ResponseEntity<ApiResponse<List<PengaduanResponse>>> listPengaduan() {
        return ResponseEntity.ok(ApiResponse.ok(pengaduanService.getAllAdmin()));
    }

    @PatchMapping("/pengaduan/{nomorTiket}/status")
    public ResponseEntity<ApiResponse<PengaduanResponse>> updateStatus(
            @PathVariable String nomorTiket, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok(pengaduanService.updateStatus(nomorTiket, body.get("status"))));
    }

    // === USER MANAGEMENT ===
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> listUsers(
            @RequestParam(required = false) String search) {
        var all = pelangganRepository.findAll().stream()
            .filter(p -> search == null || search.isBlank() ||
                p.getNama().toLowerCase().contains(search.toLowerCase()) ||
                p.getNomorPelanggan().contains(search))
            .map(pelangganMapper::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(all));
    }

    @PatchMapping("/users/{id}/toggle-aktif")
    @CacheEvict(value = "pelanggan", allEntries = true)
    public ResponseEntity<ApiResponse<UserResponse>> toggleAktif(@PathVariable Long id) {
        var p = pelangganRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Pelanggan tidak ditemukan"));
        p.setAktif(!p.isAktif());
        pelangganRepository.save(p);
        return ResponseEntity.ok(ApiResponse.ok(pelangganMapper.toResponse(p)));
    }

    @PatchMapping("/users/{id}/reset-password")
    @CacheEvict(value = "pelanggan", allEntries = true)
    public ResponseEntity<ApiResponse<Void>> resetPassword(@PathVariable Long id,
                                                            @RequestBody Map<String, String> body) {
        var p = pelangganRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Pelanggan tidak ditemukan"));
        p.setPassword(passwordEncoder.encode(body.get("password")));
        pelangganRepository.save(p);
        return ResponseEntity.ok(ApiResponse.ok("Password direset"));
    }
    @GetMapping("/pelanggan/unverified")
    public ResponseEntity<ApiResponse<List<UserResponse>>> unverified() {
        return ResponseEntity.ok(ApiResponse.ok(
            pelangganRepository.findAll().stream()
                .filter(p -> !p.isVerified() && p.getFotoKtp() != null)
                .map(pelangganMapper::toResponse).toList()));
    }

    @PostMapping("/pelanggan/{id}/verifikasi")
    @CacheEvict(value = "pelanggan", allEntries = true)
    public ResponseEntity<ApiResponse<Void>> verifikasi(@PathVariable Long id) {
        var p = pelangganRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Pelanggan tidak ditemukan"));
        p.setVerified(true);
        pelangganRepository.save(p);
        return ResponseEntity.ok(ApiResponse.ok("Pelanggan terverifikasi"));
    }
}
