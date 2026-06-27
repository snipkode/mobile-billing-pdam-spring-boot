package id.pdam.billing.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BayarRequest(
        @NotNull Long tagihanId,
        @NotBlank String metode
) {}
