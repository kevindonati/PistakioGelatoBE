package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kevindonati.PistakioGelatoBE.enums.PaymentStatus;
import kevindonati.PistakioGelatoBE.enums.ProviderType;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentDTO(
        @NotNull(message = "The provider is mandatory")
        ProviderType providerType,

        @NotNull(message = "The amount is mandatory")
        @DecimalMin(value = "0.00", message = "The amount can't be lower than 0.00")
        double amount,

        @NotNull(message = "The order is mandatory")
        UUID order
) {
}
