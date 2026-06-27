package id.pdam.billing.presentation.controller;

import id.pdam.billing.application.dto.response.ApiResponse;
import id.pdam.billing.application.dto.response.NotifikasiResponse;
import id.pdam.billing.application.usecase.NotifikasiService;
import id.pdam.billing.infrastructure.security.PrincipalHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotifikasiController {

    private final NotifikasiService notifikasiService;
    private final PrincipalHelper principalHelper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotifikasiResponse>>> getAll(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok(notifikasiService.getNotifikasi(principalHelper.getPelangganId(principal))));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(@AuthenticationPrincipal UserDetails principal,
                                                       @PathVariable Long id) {
        notifikasiService.markRead(principalHelper.getPelangganId(principal), id);
        return ResponseEntity.ok(ApiResponse.ok("Notifikasi ditandai dibaca"));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> readAll(@AuthenticationPrincipal UserDetails principal) {
        notifikasiService.markAllRead(principalHelper.getPelangganId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Semua notifikasi ditandai dibaca"));
    }
}
