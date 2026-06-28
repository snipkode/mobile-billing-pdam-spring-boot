package id.pdam.billing.infrastructure.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Caffeine cache auto-evicts idle IPs after 10 minutes — prevents memory leak
    private final Cache<String, Bucket> buckets = Caffeine.newBuilder()
        .expireAfterAccess(Duration.ofMinutes(10))
        .maximumSize(10_000)
        .build();

    private Bucket newBucket() {
        return Bucket.builder()
            .addLimit(Bandwidth.builder()
                .capacity(10)
                .refillGreedy(10, Duration.ofMinutes(1))
                .build())
            .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.startsWith("/v1/auth/login") || uri.startsWith("/v1/auth/otp")
                || uri.startsWith("/v1/auth/lupa-password") || uri.startsWith("/v1/auth/register")) {
            String ip = req.getRemoteAddr();
            Bucket bucket = buckets.get(ip, k -> newBucket());
            if (!bucket.tryConsume(1)) {
                res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                res.setContentType("application/json");
                res.getWriter().write("{\"success\":false,\"message\":\"Terlalu banyak percobaan, coba lagi nanti\"}");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
