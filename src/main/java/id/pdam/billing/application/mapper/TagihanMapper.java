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

    private static final long BEBAN_ADMIN = 5_000L;
    private static final long TARIF_PER_M3 = 3_500L;
    private static final double DENDA_RATE = 0.02;

    public TagihanResponse toResponse(Tagihan t) {
        long biayaPemakaian = Math.round((t.getPemakaian() != null ? t.getPemakaian() : 0) * TARIF_PER_M3);
        boolean terlambat = t.getJatuhTempo() != null && LocalDate.now().isAfter(t.getJatuhTempo())
                && !"Lunas".equalsIgnoreCase(t.getStatus());
        long denda = terlambat ? Math.round(biayaPemakaian * DENDA_RATE) : 0;

        List<RincianItem> rincian = new ArrayList<>();
        rincian.add(new RincianItem("Biaya Pemakaian Air", biayaPemakaian, false));
        rincian.add(new RincianItem("Biaya Beban/Administrasi", BEBAN_ADMIN, false));
        if (terlambat) rincian.add(new RincianItem("Denda Keterlambatan", denda, true));

        String kategori = t.getPelanggan() != null && t.getPelanggan().getGolongan() != null
                ? t.getPelanggan().getGolongan() : "Rumah Tangga";

        return new TagihanResponse(
                t.getId(),
                t.getPeriode(),
                t.getStatus(),
                t.getTotalTagihan(),
                t.getJatuhTempo() != null ? t.getJatuhTempo().toString() : null,
                t.getMeterAwal(),
                t.getMeterAkhir(),
                t.getPemakaian(),
                kategori,
                rincian
        );
    }

    public RiwayatResponse toRiwayat(Tagihan t) {
        return new RiwayatResponse(String.valueOf(t.getId()), t.getPeriode(), t.getPemakaian(), t.getTotalTagihan(), t.getStatus());
    }
}
