package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateDTO(
        @NotBlank(message = "Name is mandatory")
        String name,

        @NotBlank(message = "Surname is mandatory")
        String surname,

        @Email(message = "Invalid email")
        @NotBlank(message = "Email is mandatory")
        String email,

        String phone
) {
}
