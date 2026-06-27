package id.pdam.billing.presentation.controller;

import id.pdam.billing.application.dto.request.BayarRequest;
import id.pdam.billing.application.dto.response.*;
import id.pdam.billing.application.usecase.BillingService;
import id.pdam.billing.infrastructure.security.PrincipalHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;
    private final PrincipalHelper principalHelper;

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<TagihanResponse>> getCurrent(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok(billingService.getTagihanBulanIni(principalHelper.getPelangganId(principal))));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<RiwayatResponse>>> getHistory(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok(billingService.getRiwayatPemakaian(principalHelper.getPelangganId(principal))));
    }

    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<BayarResponse>> pay(@AuthenticationPrincipal UserDetails principal,
                                                           @Valid @RequestBody BayarRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(billingService.bayar(principalHelper.getPelangganId(principal), req)));
    }
}
