package service;

import dto.*;
import entity.MaintenanceRequest;
import entity.StatusHistory;
import entity.enums.MaintenanceStatus;
import exception.ResourceNotFoundException;
import repository.MaintenanceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceRequestService {

    private final MaintenanceRequestRepository repository;

    public MaintenanceRequestResponseDTO create(MaintenanceRequestCreateDTO dto) {
        MaintenanceRequest m = new MaintenanceRequest();
        applyFields(m, dto);
        m.setStatus(MaintenanceStatus.OPEN);

        // Première ligne d'historique (association StatusHistory 1..*)
        StatusHistory h = new StatusHistory();
        h.setOldStatus(null);
        h.setNewStatus(MaintenanceStatus.OPEN);
        h.setChangedAt(LocalDateTime.now());
        h.setMaintenanceRequest(m);
        m.getHistory().add(h);

        return toDto(repository.save(m));
    }

    @Transactional(readOnly = true)
    public List<MaintenanceRequestResponseDTO> getAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public MaintenanceRequestResponseDTO getById(Long id) {
        return toDto(findOrThrow(id));
    }

    public MaintenanceRequestResponseDTO update(Long id, MaintenanceRequestCreateDTO dto) {
        MaintenanceRequest m = findOrThrow(id);
        applyFields(m, dto);
        return toDto(repository.save(m));
    }

    public void delete(Long id) {
        // les entités filles sont supprimées en cascade
        repository.delete(findOrThrow(id));
    }

    // ---------- méthodes privées ----------

    private MaintenanceRequest findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceRequest " + id + " introuvable"));
    }

    private void applyFields(MaintenanceRequest m, MaintenanceRequestCreateDTO dto) {
        m.setBuildingId(dto.buildingId());
        m.setLeakDetectionId(dto.leakDetectionId());
        m.setPriority(dto.priority());
        m.setScheduledDate(dto.scheduledDate());
        m.setEstimatedLossM3Day(dto.estimatedLossM3Day());
    }

    private MaintenanceRequestResponseDTO toDto(MaintenanceRequest m) {
        return new MaintenanceRequestResponseDTO(
                m.getId(),
                m.getBuildingId(),
                m.getLeakDetectionId(),
                m.getPriority(),
                m.getStatus(),
                m.getScheduledDate(),
                m.getCompletedAt(),
                m.getEstimatedLossM3Day(),
                m.getLaborCost(),
                m.getPartsCost(),
                m.getAssignments().stream()
                        .map(a -> new AssignmentResponseDTO(
                                a.getId(), a.getTechnicianId(),
                                a.getAssignedAt(), a.getUnassignedAt(), a.getReason()))
                        .toList(),
                m.getHistory().stream()
                        .map(h -> new StatusHistoryResponseDTO(
                                h.getId(), h.getOldStatus(), h.getNewStatus(), h.getChangedAt()))
                        .toList()
        );
    }
}