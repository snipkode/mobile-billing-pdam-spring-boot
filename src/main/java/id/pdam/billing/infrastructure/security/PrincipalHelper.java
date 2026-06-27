package id.pdam.billing.infrastructure.security;

import id.pdam.billing.domain.repository.PelangganRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrincipalHelper {

    private final PelangganRepository pelangganRepository;

    public Long getPelangganId(UserDetails principal) {
        return pelangganRepository.findByNomorPelanggan(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Pelanggan tidak ditemukan"))
                .getId();
    }
}
