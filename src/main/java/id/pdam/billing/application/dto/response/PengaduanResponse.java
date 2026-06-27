package id.pdam.billing.application.dto.response;

public record PengaduanResponse(
        String id,
        String judul,
        String kategori,
        String deskripsi,
        String status,
        String fotoPath,
        String createdAt
) {}
