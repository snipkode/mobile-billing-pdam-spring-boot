package id.pdam.billing.application.dto.response;

import java.util.List;

public record TagihanResponse(
        Long id,
        String periode,
        String status,
        Double meterAwal,
        Double meterAkhir,
        Double pemakaian,
        Long totalTagihan,
        String jatuhTempo,
        List<RincianItem> rincian
) {
    public record RincianItem(String label, Long nilai, boolean danger) {}
}
