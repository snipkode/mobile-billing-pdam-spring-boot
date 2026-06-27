package id.pdam.billing.application.usecase;

import id.pdam.billing.application.dto.request.LoginRequest;
import id.pdam.billing.application.dto.response.AuthResponse;
import id.pdam.billing.application.mapper.PelangganMapper;
import id.pdam.billing.domain.entity.Pelanggan;
import id.pdam.billing.domain.repository.PelangganRepository;
import id.pdam.billing.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PelangganRepository pelangganRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PelangganMapper pelangganMapper;

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.nomorPelanggan(), req.password()));
        Pelanggan pelanggan = pelangganRepository.findByNomorPelanggan(req.nomorPelanggan())
                .orElseThrow(() -> new UsernameNotFoundException("Pelanggan tidak ditemukan"));
        String token = jwtService.generateToken(pelanggan.getNomorPelanggan());
        return new AuthResponse(token, pelangganMapper.toResponse(pelanggan));
    }
}
