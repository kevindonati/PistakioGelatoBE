package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CheckoutDTO(
        @NotNull(message = "Address is mandatory")
        UUID address,

        @Size(max = 500, message = "Notes cannot exceed 500 characters")
        String notes
) {
}
