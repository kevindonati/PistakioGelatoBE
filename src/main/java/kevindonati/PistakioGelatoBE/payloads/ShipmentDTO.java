package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kevindonati.PistakioGelatoBE.enums.ShipmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentDTO(
        @NotBlank(message = "The carrier is mandatory")
        String carrier,

        @NotBlank(message = "The tracking number is mandatory")
        String trackingNumber,

        @NotNull(message = "The estimated delivery is mandatory")
        LocalDateTime estimatedDelivery,

        @NotNull(message = "The shipment status is mandatory")
        ShipmentStatus shipmentStatus,

        @NotNull(message = "The order is mandatory")
        UUID order
) {
}
