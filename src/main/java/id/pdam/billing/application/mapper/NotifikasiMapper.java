package id.pdam.billing.application.mapper;

import id.pdam.billing.application.dto.response.NotifikasiResponse;
import id.pdam.billing.domain.entity.Notifikasi;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class NotifikasiMapper {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public NotifikasiResponse toResponse(Notifikasi n) {
        LocalDate today = LocalDate.now();
        LocalDate notifDate = n.getCreatedAt().toLocalDate();

        String time = n.getCreatedAt().format(TIME_FMT);
        String group;
        if (notifDate.isEqual(today)) {
            group = "Hari Ini";
        } else if (notifDate.isEqual(today.minusDays(1))) {
            group = "Kemarin";
        } else {
            group = notifDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        }

        return new NotifikasiResponse(
                n.getId(), n.getType(), n.getTitle(), n.getBody(),
                time, group, n.isUnread()
        );
    }
}
