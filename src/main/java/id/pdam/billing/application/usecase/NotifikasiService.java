package id.pdam.billing.application.usecase;

import id.pdam.billing.application.dto.response.NotifikasiResponse;
import id.pdam.billing.application.mapper.NotifikasiMapper;
import id.pdam.billing.domain.repository.NotifikasiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotifikasiService {

    private final NotifikasiRepository notifikasiRepository;
    private final NotifikasiMapper notifikasiMapper;

    public List<NotifikasiResponse> getNotifikasi(Long pelangganId) {
        return notifikasiRepository.findByPelangganIdOrderByCreatedAtDesc(pelangganId)
                .stream().map(notifikasiMapper::toResponse).toList();
    }

    @Transactional
    public void markAllRead(Long pelangganId) {
        notifikasiRepository.findByPelangganIdOrderByCreatedAtDesc(pelangganId)
                .forEach(n -> n.setUnread(false));
    }
}
