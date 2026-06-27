package id.pdam.billing.application.dto.response;

public record RiwayatResponse(
        Long id,
        String bulan,
        Double usage,
        Long tagihan,
        String status
) {}
