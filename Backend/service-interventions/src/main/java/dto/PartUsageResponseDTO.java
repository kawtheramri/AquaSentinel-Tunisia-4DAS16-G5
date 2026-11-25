package dto;

import java.math.BigDecimal;

public record PartUsageResponseDTO(
        Long id,
        Long maintenanceRequestId,
        String partReference,
        Integer quantity,
        BigDecimal unitPrice,
        String reservationStatus
) {}