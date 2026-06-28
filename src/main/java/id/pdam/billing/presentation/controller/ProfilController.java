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
        var p = pelangganRepository.findByNomorPelanggan(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Pelanggan tidak ditemukan"));
        return ResponseEntity.ok(ApiResponse.ok(pelangganMapper.toResponse(p)));
    }

    @PutMapping
    @CacheEvict(value = "pelanggan", key = "#principal.username")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody Map<String, String> body) {
        var p = pelangganRepository.findByNomorPelanggan(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Pelanggan tidak ditemukan"));
        if (body.containsKey("nama"))    p.setNama(body.get("nama"));
        if (body.containsKey("telepon")) p.setTelepon(body.get("telepon"));
        if (body.containsKey("email"))   p.setEmail(body.get("email"));
        pelangganRepository.save(p);
        return ResponseEntity.ok(ApiResponse.ok(pelangganMapper.toResponse(p)));
    }

    @PostMapping("/foto")
    @CacheEvict(value = "pelanggan", key = "#principal.username")
    public ResponseEntity<ApiResponse<UserResponse>> uploadFoto(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam("foto") MultipartFile file) throws IOException {
        var p = pelangganRepository.findByNomorPelanggan(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Pelanggan tidak ditemukan"));
        p.setFotoProfil("/uploads/foto/" + saveFile(file, "foto"));
        pelangganRepository.save(p);
        return ResponseEntity.ok(ApiResponse.ok(pelangganMapper.toResponse(p)));
    }

    @PostMapping("/ktp")
    @CacheEvict(value = "pelanggan", key = "#principal.username")
    public ResponseEntity<ApiResponse<UserResponse>> uploadKtp(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam("foto") MultipartFile file) throws IOException {
        var p = pelangganRepository.findByNomorPelanggan(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Pelanggan tidak ditemukan"));
        p.setFotoKtp("/uploads/ktp/" + saveFile(file, "ktp"));
        p.setVerified(false); // reset — tunggu admin review
        pelangganRepository.save(p);
        return ResponseEntity.ok(ApiResponse.ok(pelangganMapper.toResponse(p)));
    }

    private String saveFile(MultipartFile file, String subdir) throws IOException {
        String ext = file.getOriginalFilename() != null
            ? file.getOriginalFilename().replaceAll(".*\\.", ".") : ".jpg";
        Path dir = Paths.get(uploadDir, subdir);
        Files.createDirectories(dir);
        String filename = UUID.randomUUID() + ext;
        Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }
}
