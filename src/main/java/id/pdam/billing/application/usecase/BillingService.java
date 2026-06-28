package id.pdam.billing.application.usecase;

import id.pdam.billing.application.dto.request.BayarRequest;
import id.pdam.billing.application.dto.response.BayarResponse;
import id.pdam.billing.application.dto.response.RiwayatResponse;
import id.pdam.billing.application.dto.response.TagihanResponse;
import id.pdam.billing.application.mapper.TagihanMapper;
import id.pdam.billing.domain.entity.Tagihan;
import id.pdam.billing.domain.repository.TagihanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final TagihanRepository tagihanRepository;
    private final TagihanMapper tagihanMapper;

    @Cacheable(value = "tagihan", key = "#pelangganId")
    public TagihanResponse getTagihanBulanIni(Long pelangganId) {
        Tagihan tagihan = tagihanRepository
                .findTopByPelangganIdAndStatusOrderByCreatedAtDesc(pelangganId, "Belum Lunas")
                .or(() -> tagihanRepository.findByPelangganIdOrderByCreatedAtDesc(pelangganId).stream().findFirst())
                .orElseThrow(() -> new EntityNotFoundException("Tidak ada tagihan aktif"));
        return tagihanMapper.toResponse(tagihan);
    }

    @Cacheable(value = "riwayat", key = "#pelangganId")
    public List<RiwayatResponse> getRiwayatPemakaian(Long pelangganId) {
        return tagihanRepository.findByPelangganIdOrderByCreatedAtDesc(pelangganId)
                .stream().map(tagihanMapper::toRiwayat).toList();
    }

    @Transactional
    @CacheEvict(value = {"tagihan", "riwayat"}, key = "#pelangganId")
    public BayarResponse bayar(Long pelangganId, BayarRequest req) {
        Tagihan tagihan = tagihanRepository.findById(req.tagihanId())
                .filter(t -> t.getPelanggan().getId().equals(pelangganId))
                .orElseThrow(() -> new EntityNotFoundException("Tagihan tidak ditemukan"));
        tagihan.setStatus("Lunas");
        tagihan.setTanggalBayar(LocalDate.now());
        tagihan.setMetodeBayar(req.metode());
        tagihanRepository.save(tagihan);
        return new BayarResponse(true, "TRX-" + System.currentTimeMillis());
    }
}
