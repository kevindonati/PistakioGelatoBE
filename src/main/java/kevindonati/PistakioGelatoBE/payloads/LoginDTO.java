package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email")
        String email,

        @NotBlank(message = "Password is mandatory")
        String password
) {
}
