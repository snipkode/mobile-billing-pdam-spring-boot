package id.pdam.billing.application.dto.response;

import java.util.List;

public record TagihanResponse(
        Long id,
        String periode,
        String status,
        Long total,
        String jatuhTempo,
        Double meterAwal,
        Double meterAkhir,
        Double pemakaian,
        String kategori,
        List<RincianItem> rincian
) {
    public record RincianItem(String label, Long nilai, boolean danger) {}
}
