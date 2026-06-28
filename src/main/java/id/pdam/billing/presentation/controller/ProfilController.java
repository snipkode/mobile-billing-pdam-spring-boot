package id.pdam.billing.presentation.controller;

import id.pdam.billing.application.dto.response.ApiResponse;
import id.pdam.billing.application.dto.response.UserResponse;
import id.pdam.billing.application.mapper.PelangganMapper;
import id.pdam.billing.domain.repository.PelangganRepository;
import id.pdam.billing.infrastructure.security.PrincipalHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfilController {

    private final PelangganRepository pelangganRepository;
    private final PelangganMapper pelangganMapper;
    private final PrincipalHelper principalHelper;

    @Value("${app.upload.dir:${user.home}/pdam-uploads}")
    private String uploadDir;

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@AuthenticationPrincipal UserDetails principal) {
        var pelanggan = pelangganRepository.findByNomorPelanggan(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Pelanggan tidak ditemukan"));
        return ResponseEntity.ok(ApiResponse.ok(pelangganMapper.toResponse(pelanggan)));
    }

    @PutMapping
    @CacheEvict(value = "pelanggan", key = "#principal.username")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody Map<String, String> body) {
        var pelanggan = pelangganRepository.findByNomorPelanggan(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Pelanggan tidak ditemukan"));
        if (body.containsKey("nama"))    pelanggan.setNama(body.get("nama"));
        if (body.containsKey("telepon")) pelanggan.setTelepon(body.get("telepon"));
        if (body.containsKey("email"))   pelanggan.setEmail(body.get("email"));
        pelangganRepository.save(pelanggan);
        return ResponseEntity.ok(ApiResponse.ok(pelangganMapper.toResponse(pelanggan)));
    }

    @PostMapping("/foto")
    @CacheEvict(value = "pelanggan", key = "#principal.username")
    public ResponseEntity<ApiResponse<UserResponse>> uploadFoto(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam("foto") MultipartFile file) throws IOException {
        String ext = file.getOriginalFilename() != null
            ? file.getOriginalFilename().replaceAll(".*\\.", ".") : ".jpg";
        Path dir = Paths.get(uploadDir, "foto");
        Files.createDirectories(dir);
        String filename = UUID.randomUUID() + ext;
        Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

        var pelanggan = pelangganRepository.findByNomorPelanggan(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Pelanggan tidak ditemukan"));
        pelanggan.setFotoProfil("/uploads/foto/" + filename);
        pelangganRepository.save(pelanggan);
        return ResponseEntity.ok(ApiResponse.ok(pelangganMapper.toResponse(pelanggan)));
    }
}
