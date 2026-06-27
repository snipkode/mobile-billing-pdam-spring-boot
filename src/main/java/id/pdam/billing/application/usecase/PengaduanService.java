package id.pdam.billing.application.usecase;

import id.pdam.billing.application.dto.request.PengaduanRequest;
import id.pdam.billing.application.dto.response.PengaduanResponse;
import id.pdam.billing.domain.entity.Pelanggan;
import id.pdam.billing.domain.entity.Pengaduan;
import id.pdam.billing.domain.repository.PelangganRepository;
import id.pdam.billing.domain.repository.PengaduanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PengaduanService {

    private final PengaduanRepository pengaduanRepository;
    private final PelangganRepository pelangganRepository;

    public PengaduanResponse buat(Long pelangganId, PengaduanRequest req) {
        Pelanggan pelanggan = pelangganRepository.findById(pelangganId)
                .orElseThrow(() -> new EntityNotFoundException("Pelanggan tidak ditemukan"));
        Pengaduan p = Pengaduan.builder()
                .pelanggan(pelanggan)
                .kategori(req.kategori())
                .deskripsi(req.deskripsi())
                .status("DIPROSES")
                .nomorTiket("TK-" + System.currentTimeMillis())
                .createdAt(LocalDateTime.now())
                .build();
        Pengaduan saved = pengaduanRepository.save(p);
        return new PengaduanResponse(saved.getId(), saved.getNomorTiket(), saved.getStatus());
    }

    public List<PengaduanResponse> getList(Long pelangganId) {
        return pengaduanRepository.findByPelangganIdOrderByCreatedAtDesc(pelangganId)
                .stream().map(p -> new PengaduanResponse(p.getId(), p.getNomorTiket(), p.getStatus()))
                .toList();
    }
}
