package id.pdam.billing.presentation.controller;

import id.pdam.billing.application.dto.response.ApiResponse;
import id.pdam.billing.application.usecase.RegisterService;
import id.pdam.billing.domain.entity.PendaftaranBaru;
import id.pdam.billing.domain.repository.PendaftaranBaruRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/pendaftaran")
@RequiredArgsConstructor
public class AdminController {

    private final PendaftaranBaruRepository pendaftaranBaruRepository;
    private final RegisterService registerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PendaftaranBaru>>> list(
            @RequestParam(defaultValue = "MENUNGGU") String status) {
        return ResponseEntity.ok(ApiResponse.ok(
            pendaftaranBaruRepository.findAll().stream()
                .filter(p -> p.getStatus().equals(status)).toList()));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approve(@PathVariable Long id,
                                                      @RequestBody Map<String, String> body) {
        registerService.approve(id, body.get("nomorPelanggan"), body.get("password"));
        return ResponseEntity.ok(ApiResponse.ok("Pendaftaran disetujui"));
    }

    @PostMapping("/{id}/tolak")
    public ResponseEntity<ApiResponse<Void>> tolak(@PathVariable Long id) {
        registerService.tolak(id);
        return ResponseEntity.ok(ApiResponse.ok("Pendaftaran ditolak"));
    }
}
