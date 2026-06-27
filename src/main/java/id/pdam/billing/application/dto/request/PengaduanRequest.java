package id.pdam.billing.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PengaduanRequest(
        @NotBlank String kategori,
        @NotBlank String deskripsi
) {}
