package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordDTO(
        @NotBlank
        String token,

        @NotBlank
        @Size(min = 8, message = "Password must contain at least 8 characters")
        String password
) {
}