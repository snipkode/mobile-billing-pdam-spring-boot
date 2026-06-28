package id.pdam.billing.application.usecase;

import id.pdam.billing.application.dto.request.LoginRequest;
import id.pdam.billing.application.dto.response.AuthResponse;
import id.pdam.billing.application.dto.response.UserResponse;
import id.pdam.billing.application.mapper.PelangganMapper;
import id.pdam.billing.domain.entity.Pelanggan;
import id.pdam.billing.domain.repository.PelangganRepository;
import id.pdam.billing.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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
    private final OtpService otpService;

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.nomorPelanggan(), req.password()));
        Pelanggan pelanggan = pelangganRepository.findByNomorPelanggan(req.nomorPelanggan())
                .orElseThrow(() -> new UsernameNotFoundException("Pelanggan tidak ditemukan"));
        String token = jwtService.generateToken(pelanggan.getNomorPelanggan());
        return new AuthResponse(token, pelangganMapper.toResponse(pelanggan));
    }

    @Cacheable(value = "pelanggan", key = "#nomorPelanggan")
    public UserResponse me(String nomorPelanggan) {
        return pelangganMapper.toResponse(
                pelangganRepository.findByNomorPelanggan(nomorPelanggan)
                        .orElseThrow(() -> new UsernameNotFoundException("Pelanggan tidak ditemukan")));
    }

    public AuthResponse loginWithOtp(String nomorPelanggan, String kode) {
        Pelanggan pelanggan = pelangganRepository.findByNomorPelanggan(nomorPelanggan)
                .orElseThrow(() -> new UsernameNotFoundException("Pelanggan tidak ditemukan"));
        if (!otpService.verifikasiOtp(pelanggan.getTelepon(), kode, "LOGIN_OTP"))
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, "OTP tidak valid atau kadaluarsa");
        String token = jwtService.generateToken(pelanggan.getNomorPelanggan());
        return new AuthResponse(token, pelangganMapper.toResponse(pelanggan));
    }
}
