package id.pdam.billing.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tagihan")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Tagihan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pelanggan_id", nullable = false)
    private Pelanggan pelanggan;

    private String periode;
    private Double meterAwal;
    private Double meterAkhir;
    private Double pemakaian;
    private Long totalTagihan;
    private String status;
    private LocalDate jatuhTempo;
    private LocalDate tanggalBayar;
    private String metodeBayar;
    private LocalDateTime createdAt;
}
