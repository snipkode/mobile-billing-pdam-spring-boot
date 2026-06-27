package id.pdam.billing.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifikasi")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Notifikasi {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pelanggan_id", nullable = false)
    private Pelanggan pelanggan;

    private String type;
    private String title;
    private String body;

    @Builder.Default
    private boolean unread = true;

    private LocalDateTime createdAt;
}
