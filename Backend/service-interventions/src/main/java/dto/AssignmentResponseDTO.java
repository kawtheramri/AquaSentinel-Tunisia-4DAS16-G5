package dto;

import java.time.LocalDateTime;

public record AssignmentResponseDTO(
        Long id,
        Long technicianId,
        LocalDateTime assignedAt,
        LocalDateTime unassignedAt,
        String reason
) {}