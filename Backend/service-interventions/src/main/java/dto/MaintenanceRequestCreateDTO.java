package dto;

import entity.enums.Priority;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MaintenanceRequestCreateDTO(
        @NotNull(message = "buildingId est obligatoire") Long buildingId,
        String leakDetectionId,
        @NotNull(message = "priority est obligatoire") Priority priority,
        LocalDate scheduledDate,
        Double estimatedLossM3Day
) {}