package id.pdam.billing.presentation.controller;

import id.pdam.billing.application.dto.response.ApiResponse;
import id.pdam.billing.application.dto.response.PengaduanResponse;
import id.pdam.billing.application.usecase.PengaduanService;
import id.pdam.billing.application.usecase.RegisterService;
import id.pdam.billing.domain.entity.PendaftaranBaru;
import id.pdam.billing.domain.repository.PendaftaranBaruRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    // === PENDAFTARAN ===
    @GetMapping("/pendaftaran")
    public ResponseEntity<ApiResponse<List<PendaftaranBaru>>> listPendaftaran(
            @RequestParam(defaultValue = "MENUNGGU") String status) {
        return ResponseEntity.ok(ApiResponse.ok(
            pendaftaranBaruRepository.findAll().stream()
                .filter(p -> p.getStatus().equals(status)).toList()));
    }

    @PostMapping("/pendaftaran/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approve(@PathVariable Long id,
                                                      @RequestBody Map<String, String> body) {
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
        return ResponseEntity.ok(ApiResponse.ok(
            pengaduanService.updateStatus(nomorTiket, body.get("status"))));
    }
}
