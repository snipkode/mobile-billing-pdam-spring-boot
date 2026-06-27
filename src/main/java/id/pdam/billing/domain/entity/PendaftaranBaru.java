package id.pdam.billing.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pendaftaran_baru")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PendaftaranBaru {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nama;
    private String telepon;
    private String email;
    private String alamat;
    private String golongan;

    @Builder.Default
    private String status = "MENUNGGU"; // MENUNGGU, DISETUJUI, DITOLAK

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
