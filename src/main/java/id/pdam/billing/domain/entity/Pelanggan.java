package id.pdam.billing.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pelanggan")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Pelanggan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nomorPelanggan;

    private String nama;
    private String email;
    private String telepon;
    private String alamat;
    private String password;

    private String golongan;
    private String fotoProfil;
    private String fotoKtp;

    @Builder.Default
    private boolean verified = false;

    @Builder.Default
    private String statusMeter = "Aktif";

    @Builder.Default
    private String role = "PELANGGAN";

    @Builder.Default
    private boolean aktif = true;

    private LocalDateTime createdAt;
}
