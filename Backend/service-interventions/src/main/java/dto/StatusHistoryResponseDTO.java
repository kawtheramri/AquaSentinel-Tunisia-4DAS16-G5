package dto;

import entity.enums.MaintenanceStatus;
import entity.enums.Priority;

import java.time.LocalDateTime;

public record StatusHistoryResponseDTO(
        Long id,
        MaintenanceStatus oldStatus,
        MaintenanceStatus newStatus,
        LocalDateTime changedAt
) {}