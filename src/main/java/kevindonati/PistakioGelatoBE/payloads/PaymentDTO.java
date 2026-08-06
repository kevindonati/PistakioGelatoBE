package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.NotNull;
import kevindonati.PistakioGelatoBE.enums.ProviderType;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentDTO(
        @NotNull(message = "The provider is mandatory")
        ProviderType providerType,

        @NotNull(message = "The order is mandatory")
        UUID order
) {
}
