package id.pdam.billing.infrastructure.security;

import id.pdam.billing.domain.entity.Pelanggan;
import id.pdam.billing.domain.repository.PelangganRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PelangganRepository pelangganRepository;

    @Override
    public UserDetails loadUserByUsername(String nomorPelanggan) throws UsernameNotFoundException {
        Pelanggan p = pelangganRepository.findByNomorPelanggan(nomorPelanggan)
                .orElseThrow(() -> new UsernameNotFoundException("Pelanggan tidak ditemukan: " + nomorPelanggan));
        return new org.springframework.security.core.userdetails.User(
                p.getNomorPelanggan(), p.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + p.getRole())));
    }
}
