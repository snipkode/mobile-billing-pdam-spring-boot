package id.pdam.billing.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_token")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OtpToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String telepon;

    @Column(nullable = false)
    private String kode;

    @Column(nullable = false)
    private String tujuan; // LUPA_PASSWORD, REGISTER_LAMA, REGISTER_BARU

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Builder.Default
    private boolean used = false;
}
