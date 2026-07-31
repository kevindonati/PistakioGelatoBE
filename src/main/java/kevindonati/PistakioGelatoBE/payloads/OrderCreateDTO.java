package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderCreateDTO(
        @NotNull(message = "User is mandatory")
        UUID user
) {
}
