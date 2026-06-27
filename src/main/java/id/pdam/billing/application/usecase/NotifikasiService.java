package id.pdam.billing.application.usecase;

import id.pdam.billing.application.dto.response.NotifikasiResponse;
import id.pdam.billing.application.mapper.NotifikasiMapper;
import id.pdam.billing.domain.repository.NotifikasiRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotifikasiService {

    private final NotifikasiRepository notifikasiRepository;
    private final NotifikasiMapper notifikasiMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public List<NotifikasiResponse> getNotifikasi(Long pelangganId) {
        return notifikasiRepository.findByPelangganIdOrderByCreatedAtDesc(pelangganId)
                .stream().map(notifikasiMapper::toResponse).toList();
    }

    @Transactional
    public void markRead(Long pelangganId, Long notifId) {
        var notif = notifikasiRepository.findById(notifId)
                .filter(n -> n.getPelanggan().getId().equals(pelangganId))
                .orElseThrow(() -> new EntityNotFoundException("Notifikasi tidak ditemukan"));
        notif.setUnread(false);
    }

    @Transactional
    public void markAllRead(Long pelangganId) {
        notifikasiRepository.findByPelangganIdOrderByCreatedAtDesc(pelangganId)
                .forEach(n -> n.setUnread(false));
    }

    // Called internally when a new notification is created — push to user's topic
    public void push(Long pelangganId, NotifikasiResponse notif) {
        messagingTemplate.convertAndSend("/topic/notifikasi/" + pelangganId, notif);
    }
}
