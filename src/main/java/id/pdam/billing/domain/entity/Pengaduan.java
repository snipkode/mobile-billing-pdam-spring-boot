package id.pdam.billing.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pengaduan")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Pengaduan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pelanggan_id", nullable = false)
    private Pelanggan pelanggan;

    private String kategori;
    private String deskripsi;
    private String status;

    @Column(unique = true, nullable = false)
    private String nomorTiket;

    private LocalDateTime createdAt;
}
