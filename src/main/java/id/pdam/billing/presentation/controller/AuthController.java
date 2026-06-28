package id.pdam.billing.presentation.controller;

import id.pdam.billing.application.dto.request.LoginRequest;
import id.pdam.billing.application.dto.response.ApiResponse;
import id.pdam.billing.application.dto.response.AuthResponse;
import id.pdam.billing.application.dto.response.UserResponse;
import id.pdam.billing.application.usecase.AuthService;
import id.pdam.billing.application.usecase.LupaPasswordService;
import id.pdam.billing.application.usecase.OtpService;
import id.pdam.billing.application.usecase.RegisterService;import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;
    private final RegisterService registerService;
    private final LupaPasswordService lupaPasswordService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(req)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(authService.me(userDetails.getUsername())));
    }

    // === MASUK DENGAN OTP ===
    // Step 1: POST /auth/login/otp  { "nomorPelanggan" } → kirim OTP ke telepon terdaftar
    @PostMapping("/login/otp")
    public ResponseEntity<ApiResponse<Void>> loginOtpKirim(@RequestBody Map<String, String> body) {
        lupaPasswordService.kirimOtpLogin(body.get("nomorPelanggan"));
        return ResponseEntity.ok(ApiResponse.ok("OTP terkirim ke nomor terdaftar"));
    }

    // Step 2: POST /auth/login/otp/verifikasi  { "nomorPelanggan", "kode" } → return token
    @PostMapping("/login/otp/verifikasi")
    public ResponseEntity<ApiResponse<AuthResponse>> loginOtpVerifikasi(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok(authService.loginWithOtp(body.get("nomorPelanggan"), body.get("kode"))));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.ok("Logout berhasil"));
    }

    // POST /auth/otp/kirim  body: { "telepon": "08xx", "tujuan": "LUPA_PASSWORD" }
    @PostMapping("/otp/kirim")
    public ResponseEntity<ApiResponse<Void>> kirimOtp(@RequestBody Map<String, String> body) {
        otpService.kirimOtp(body.get("telepon"), body.get("tujuan"));
        return ResponseEntity.ok(ApiResponse.ok("OTP terkirim"));
    }

    // POST /auth/otp/verifikasi  body: { "telepon": "08xx", "kode": "123456", "tujuan": "LUPA_PASSWORD" }
    @PostMapping("/otp/verifikasi")
    public ResponseEntity<ApiResponse<Boolean>> verifikasiOtp(@RequestBody Map<String, String> body) {
        boolean valid = otpService.verifikasiOtp(body.get("telepon"), body.get("kode"), body.get("tujuan"));
        return ResponseEntity.ok(ApiResponse.ok(valid));
    }

    // === REGISTER PELANGGAN LAMA ===
    // Step 1: POST /auth/register/lama  { "nomorPelanggan": "x", "telepon": "08xx" } → kirim OTP
    @PostMapping("/register/lama")
    public ResponseEntity<ApiResponse<Void>> registerLama(@RequestBody Map<String, String> body) {
        registerService.registerLama(body.get("nomorPelanggan"), body.get("telepon"));
        return ResponseEntity.ok(ApiResponse.ok("OTP terkirim ke nomor terdaftar"));
    }

    // Step 2: POST /auth/register/lama/password  { "telepon", "kode", "passwordBaru" }
    @PostMapping("/register/lama/password")
    public ResponseEntity<ApiResponse<Void>> setPasswordLama(@RequestBody Map<String, String> body) {
        registerService.setPasswordLama(body.get("telepon"), body.get("kode"), body.get("passwordBaru"));
        return ResponseEntity.ok(ApiResponse.ok("Password berhasil diatur, silakan login"));
    }

    // === REGISTER PELANGGAN BARU ===
    // Step 1: POST /auth/register/baru  { "nama", "telepon", "email", "alamat", "golongan" } → kirim OTP
    @PostMapping("/register/baru")
    public ResponseEntity<ApiResponse<Long>> registerBaru(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok(registerService.registerBaru(body)));
    }

    // Step 2: POST /auth/register/baru/verifikasi  { "telepon", "kode" }
    @PostMapping("/register/baru/verifikasi")
    public ResponseEntity<ApiResponse<Void>> verifikasiRegistrasiBaru(@RequestBody Map<String, String> body) {
        registerService.verifikasiRegistrasiBaru(body.get("telepon"), body.get("kode"));
        return ResponseEntity.ok(ApiResponse.ok("Nomor terverifikasi, pendaftaran menunggu persetujuan petugas"));
    }

    // === LUPA PASSWORD ===
    // Step 1: POST /auth/lupa-password  { "nomorPelanggan" } → kirim OTP ke telepon terdaftar
    @PostMapping("/lupa-password")
    public ResponseEntity<ApiResponse<Void>> lupaPassword(@RequestBody Map<String, String> body) {
        lupaPasswordService.kirimOtp(body.get("nomorPelanggan"));
        return ResponseEntity.ok(ApiResponse.ok("OTP terkirim ke nomor terdaftar"));
    }

    // Step 2: POST /auth/lupa-password/reset  { "nomorPelanggan", "kode", "passwordBaru" }
    @PostMapping("/lupa-password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody Map<String, String> body) {
        lupaPasswordService.resetPassword(body.get("nomorPelanggan"), body.get("kode"), body.get("passwordBaru"));
        return ResponseEntity.ok(ApiResponse.ok("Password berhasil direset, silakan login"));
    }
}
