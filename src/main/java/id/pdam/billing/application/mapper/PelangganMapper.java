package id.pdam.billing.application.mapper;

import id.pdam.billing.application.dto.response.UserResponse;
import id.pdam.billing.domain.entity.Pelanggan;
import org.springframework.stereotype.Component;

@Component
public class PelangganMapper {

    public UserResponse toResponse(Pelanggan p) {
        return new UserResponse(
                p.getId(), p.getNama(), p.getNomorPelanggan(),
                p.getEmail(), p.getTelepon(),
                p.getAlamat(), p.getGolongan(), p.getStatusMeter(), p.getFotoProfil()
        );
    }
}
