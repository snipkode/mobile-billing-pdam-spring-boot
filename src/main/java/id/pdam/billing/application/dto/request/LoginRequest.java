package id.pdam.billing.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String nomorPelanggan,
        @NotBlank String password
) {}
