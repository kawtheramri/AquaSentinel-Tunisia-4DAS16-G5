
package dto;

import entity.enums.MaintenanceStatus;
import entity.enums.Priority;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MaintenanceRequestResponseDTO(
        Long id,
        Long buildingId,
        String leakDetectionId,
        Priority priority,
        MaintenanceStatus status,
        LocalDate scheduledDate,
        LocalDateTime completedAt,
        Double estimatedLossM3Day,
        BigDecimal laborCost,
        BigDecimal partsCost,
        List<AssignmentResponseDTO> assignments,
        List<StatusHistoryResponseDTO> history
) {}
