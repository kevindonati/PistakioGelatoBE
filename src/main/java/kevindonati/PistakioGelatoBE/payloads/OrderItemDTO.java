package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderItemDTO(
        @NotNull(message = "The quantity is mandatory")
        @Min(value = 1, message = "The quantity can't be lower than 1")
        int quantity,

        @NotNull(message = "The order is mandatory")
        UUID order,

        @NotNull(message = "The flavor is mandatory")
        UUID flavor,

        @NotNull
        UUID tub
) {
}
