
package service;

import dto.PartUsageRequestDTO;
import dto.PartUsageResponseDTO;
import entity.MaintenanceRequest;
import entity.PartUsage;
import exception.ResourceNotFoundException;
import repository.MaintenanceRequestRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PartUsageService {

    private final MaintenanceRequestRepository requestRepository;

    public PartUsageResponseDTO create(Long requestId, PartUsageRequestDTO dto) {
        MaintenanceRequest request = findRequest(requestId);

        PartUsage part = new PartUsage();
        applyFields(part, dto);
        part.setMaintenanceRequest(request);
        request.getParts().add(part);

        recalculatePartsCost(request);
        requestRepository.saveAndFlush(request);

        return toDto(part);
    }

    @Transactional(readOnly = true)
    public List<PartUsageResponseDTO> getAll(Long requestId) {
        return findRequest(requestId).getParts()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PartUsageResponseDTO getById(Long requestId, Long partId) {
        return toDto(findPart(findRequest(requestId), partId));
    }

    public PartUsageResponseDTO update(
            Long requestId, Long partId, PartUsageRequestDTO dto) {

        MaintenanceRequest request = findRequest(requestId);
        PartUsage part = findPart(request, partId);

        applyFields(part, dto);
        recalculatePartsCost(request);
        requestRepository.save(request);

        return toDto(part);
    }

    public void delete(Long requestId, Long partId) {
        MaintenanceRequest request = findRequest(requestId);
        PartUsage part = findPart(request, partId);

        request.getParts().remove(part);
        recalculatePartsCost(request);
        requestRepository.save(request);
    }

    // ---------- Méthodes privées ----------

    private MaintenanceRequest findRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "MaintenanceRequest " + requestId + " introuvable"));
    }

    private PartUsage findPart(MaintenanceRequest request, Long partId) {
        return request.getParts().stream()
                .filter(p -> p.getId().equals(partId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PartUsage " + partId
                                + " introuvable pour la demande " + request.getId()));
    }

    private void applyFields(PartUsage part, PartUsageRequestDTO dto) {
        part.setPartReference(dto.partReference());
        part.setQuantity(dto.quantity());
        part.setUnitPrice(dto.unitPrice());
        part.setReservationStatus(dto.reservationStatus());
    }

    private void recalculatePartsCost(MaintenanceRequest request) {
        BigDecimal total = request.getParts().stream()
                .map(p -> p.getUnitPrice().multiply(
                        BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        request.setPartsCost(total);
    }

    private PartUsageResponseDTO toDto(PartUsage p) {
        return new PartUsageResponseDTO(
                p.getId(),
                p.getMaintenanceRequest().getId(),
                p.getPartReference(),
                p.getQuantity(),
                p.getUnitPrice(),
                p.getReservationStatus()
        );
    }
}
