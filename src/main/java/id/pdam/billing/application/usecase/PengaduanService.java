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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PengaduanService {

    private static final Path UPLOAD_DIR = Paths.get("uploads/pengaduan");

    private final PengaduanRepository pengaduanRepository;
    private final PelangganRepository pelangganRepository;

    public PengaduanResponse buat(Long pelangganId, PengaduanRequest req, MultipartFile foto) {
        Pelanggan pelanggan = pelangganRepository.findById(pelangganId)
                .orElseThrow(() -> new EntityNotFoundException("Pelanggan tidak ditemukan"));

        String fotoPath = null;
        if (foto != null && !foto.isEmpty()) {
            fotoPath = saveFoto(foto);
        }

        Pengaduan p = Pengaduan.builder()
                .pelanggan(pelanggan)
                .kategori(req.kategori())
                .deskripsi(req.deskripsi())
                .status("DIPROSES")
                .nomorTiket("TK-" + System.currentTimeMillis())
                .fotoPath(fotoPath)
                .createdAt(LocalDateTime.now())
                .build();

        Pengaduan saved = pengaduanRepository.save(p);
        return new PengaduanResponse(saved.getNomorTiket(), saved.getKategori(), saved.getStatus());
    }

    public List<PengaduanResponse> getList(Long pelangganId) {
        return pengaduanRepository.findByPelangganIdOrderByCreatedAtDesc(pelangganId)
                .stream().map(p -> new PengaduanResponse(p.getNomorTiket(), p.getKategori(), p.getStatus()))
                .toList();
    }

    private String saveFoto(MultipartFile foto) {
        try {
            Files.createDirectories(UPLOAD_DIR);
            String ext = foto.getOriginalFilename() != null && foto.getOriginalFilename().contains(".")
                    ? foto.getOriginalFilename().substring(foto.getOriginalFilename().lastIndexOf('.')) : ".jpg";
            String filename = UUID.randomUUID() + ext;
            Files.copy(foto.getInputStream(), UPLOAD_DIR.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return "uploads/pengaduan/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan foto: " + e.getMessage());
        }
    }
}
