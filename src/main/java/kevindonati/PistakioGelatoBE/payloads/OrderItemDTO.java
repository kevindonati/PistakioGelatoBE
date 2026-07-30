package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record OrderItemDTO(
        @NotNull(message = "The quantity is mandatory")
        @Size(min = 1, message = "The quantity can't be lower than 1")
        int quantity,

        @NotNull(message = "The unit price is mandatory")
        @DecimalMin(value = "0.00", message = "The unit price can't be lower than 0")
        double unitPrice,

        @NotNull(message = "The order is mandatory")
        UUID order,

        @NotNull(message = "The flavor is mandatory")
        UUID flavor
) {
}
