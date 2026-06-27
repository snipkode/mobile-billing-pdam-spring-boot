package id.pdam.billing.infrastructure.waha;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WahaService {

    @Value("${app.waha.url}") private String wahaUrl;
    @Value("${app.waha.api-key}") private String apiKey;
    @Value("${app.waha.session}") private String session;

    private final RestTemplate restTemplate;

    public void sendOtp(String telepon, String kode) {
        // Format nomor: 08xx -> 628xx@c.us
        String chatId = formatChatId(telepon);
        String text = "Kode OTP PDAM Anda: *" + kode + "*\nBerlaku 5 menit. Jangan bagikan ke siapapun.";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Api-Key", apiKey);

        Map<String, Object> body = Map.of(
            "session", session,
            "chatId", chatId,
            "text", text
        );

        restTemplate.exchange(
            wahaUrl + "/api/sendText",
            HttpMethod.POST,
            new HttpEntity<>(body, headers),
            Void.class
        );
    }

    private String formatChatId(String telepon) {
        String nomor = telepon.replaceAll("[^0-9]", "");
        if (nomor.startsWith("0")) nomor = "62" + nomor.substring(1);
        else if (!nomor.startsWith("62")) nomor = "62" + nomor;
        return nomor + "@c.us";
    }
}
