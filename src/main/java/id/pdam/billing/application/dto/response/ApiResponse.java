package id.pdam.billing.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        String timestamp,
        String path
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "Success", data, Instant.now().toString(), null);
    }

    public static ApiResponse<Void> ok(String message) {
        return new ApiResponse<>(true, message, null, Instant.now().toString(), null);
    }

    public static ApiResponse<Void> error(String message, String path) {
        return new ApiResponse<>(false, message, null, Instant.now().toString(), path);
    }
}
