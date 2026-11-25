package dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PartUsageRequestDTO(
        @NotBlank(message = "partReference est obligatoire") String partReference,
        @NotNull(message = "quantity est obligatoire")
        @Min(value = 1, message = "quantity doit être >= 1") Integer quantity,
        @NotNull(message = "unitPrice est obligatoire")
        @DecimalMin(value = "0.0", message = "unitPrice doit être >= 0") BigDecimal unitPrice,
        String reservationStatus
) {}