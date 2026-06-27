package id.pdam.billing.infrastructure.seeder;

import id.pdam.billing.domain.entity.*;
import id.pdam.billing.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Profile("h2")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final PelangganRepository pelangganRepo;
    private final TagihanRepository tagihanRepo;
    private final NotifikasiRepository notifikasiRepo;
    private final PengaduanRepository pengaduanRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (pelangganRepo.existsByNomorPelanggan("12345678")) return;

        Pelanggan p = pelangganRepo.save(Pelanggan.builder()
                .nomorPelanggan("12345678")
                .nama("Budi Setiawan")
                .email("budi@email.com")
                .telepon("+62 812-3456-7890")
                .alamat("Jl. Merdeka No. 10, Bandung")
                .password(passwordEncoder.encode("password123"))
                .role("PELANGGAN")
                .aktif(true)
                .createdAt(LocalDateTime.now())
                .build());

        tagihanRepo.save(Tagihan.builder().pelanggan(p).periode("Oktober 2023")
                .meterAwal(1240.0).meterAkhir(1265.0).pemakaian(25.0).totalTagihan(245500L)
                .status("BELUM_LUNAS").jatuhTempo(LocalDate.of(2023, 10, 20))
                .createdAt(LocalDateTime.now()).build());

        tagihanRepo.save(Tagihan.builder().pelanggan(p).periode("September 2023")
                .meterAwal(1216.0).meterAkhir(1240.0).pemakaian(24.0).totalTagihan(134500L)
                .status("LUNAS").jatuhTempo(LocalDate.of(2023, 9, 20))
                .tanggalBayar(LocalDate.of(2023, 9, 18)).metodeBayar("Transfer Bank")
                .createdAt(LocalDateTime.now().minusMonths(1)).build());

        tagihanRepo.save(Tagihan.builder().pelanggan(p).periode("Agustus 2023")
                .meterAwal(1196.0).meterAkhir(1216.0).pemakaian(20.0).totalTagihan(112200L)
                .status("LUNAS").jatuhTempo(LocalDate.of(2023, 8, 20))
                .tanggalBayar(LocalDate.of(2023, 8, 17)).metodeBayar("Transfer Bank")
                .createdAt(LocalDateTime.now().minusMonths(2)).build());

        notifikasiRepo.save(Notifikasi.builder().pelanggan(p).type("tagihan")
                .title("Tagihan Baru Terbit").body("Tagihan Oktober 2023 sebesar Rp 245.500 telah tersedia.")
                .unread(true).createdAt(LocalDateTime.now().minusHours(1)).build());

        notifikasiRepo.save(Notifikasi.builder().pelanggan(p).type("informasi")
                .title("Info Gangguan Layanan").body("Perbaikan pipa transmisi di wilayah Bandung Tengah.")
                .unread(true).createdAt(LocalDateTime.now().minusHours(3)).build());

        notifikasiRepo.save(Notifikasi.builder().pelanggan(p).type("promo")
                .title("Promo Cashback 10%").body("Potongan tagihan PDAM via aplikasi Mitra. S/d akhir bulan.")
                .unread(false).createdAt(LocalDateTime.now().minusDays(1)).build());

        notifikasiRepo.save(Notifikasi.builder().pelanggan(p).type("tagihan")
                .title("Pengingat Pembayaran").body("Tersisa 3 hari sebelum jatuh tempo.")
                .unread(false).createdAt(LocalDateTime.now().minusDays(1).minusHours(2)).build());

        pengaduanRepo.save(Pengaduan.builder().pelanggan(p)
                .kategori("Air Tidak Mengalir").deskripsi("Air tidak mengalir selama 2 hari.")
                .status("SELESAI").nomorTiket("TK-1697000000000")
                .createdAt(LocalDateTime.now().minusDays(7)).build());
    }
}
