package id.pdam.billing.application.mapper;

import id.pdam.billing.application.dto.response.RiwayatResponse;
import id.pdam.billing.application.dto.response.TagihanResponse;
import id.pdam.billing.application.dto.response.TagihanResponse.RincianItem;
import id.pdam.billing.domain.entity.Tagihan;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class TagihanMapper {

    // Tarif tetap sederhana (dapat diganti dengan config/database)
    private static final long BEBAN_ADMIN = 5_000L;
    private static final long TARIF_PER_M3 = 3_500L;
    private static final double DENDA_RATE = 0.02;

    public TagihanResponse toResponse(Tagihan t) {
        long biayaPemakaian = Math.round((t.getPemakaian() != null ? t.getPemakaian() : 0) * TARIF_PER_M3);
        boolean terlambat = t.getJatuhTempo() != null && LocalDate.now().isAfter(t.getJatuhTempo())
                && !"LUNAS".equalsIgnoreCase(t.getStatus());
        long denda = terlambat ? Math.round(biayaPemakaian * DENDA_RATE) : 0;

        List<RincianItem> rincian = new ArrayList<>();
        rincian.add(new RincianItem("Biaya Pemakaian", biayaPemakaian, false));
        rincian.add(new RincianItem("Beban / Admin", BEBAN_ADMIN, false));
        if (terlambat) rincian.add(new RincianItem("Denda Keterlambatan", denda, true));

        return new TagihanResponse(
                t.getId(),
                t.getPeriode(),
                t.getStatus(),
                t.getMeterAwal(),
                t.getMeterAkhir(),
                t.getPemakaian(),
                t.getTotalTagihan(),
                t.getJatuhTempo() != null ? t.getJatuhTempo().toString() : null,
                rincian
        );
    }

    public RiwayatResponse toRiwayat(Tagihan t) {
        return new RiwayatResponse(
                t.getId(),
                t.getPeriode(),
                t.getPemakaian(),
                t.getTotalTagihan(),
                t.getStatus()
        );
    }
}
