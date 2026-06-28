package id.pdam.billing.application.dto.response;

public record UserResponse(
        Long id,
        String nama,
        String nomorPelanggan,
        String email,
        String telepon,
        String alamat,
        String golongan,
        String statusMeter,
        String fotoProfil
) {}
