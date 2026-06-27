package id.pdam.billing.presentation.controller;

import id.pdam.billing.application.dto.request.PengaduanRequest;
import id.pdam.billing.application.dto.response.ApiResponse;
import id.pdam.billing.application.dto.response.PengaduanResponse;
import id.pdam.billing.application.usecase.PengaduanService;
import id.pdam.billing.infrastructure.security.PrincipalHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
public class PengaduanController {

    private final PengaduanService pengaduanService;
    private final PrincipalHelper principalHelper;

    @PostMapping
    public ResponseEntity<ApiResponse<PengaduanResponse>> buat(@AuthenticationPrincipal UserDetails principal,
                                                                @Valid @RequestBody PengaduanRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(pengaduanService.buat(principalHelper.getPelangganId(principal), req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PengaduanResponse>>> getList(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok(pengaduanService.getList(principalHelper.getPelangganId(principal))));
    }
}
