package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserDTO(
        @NotBlank(message = "The name is mandatory")
        String name,

        @NotBlank(message = "The surname is mandatory")
        String surname,

        @NotBlank(message = "The email is mandatory")
        @Email(message = "Invalid email")
        String email,

        @NotBlank(message = "The email is mandatory")
        @Size(min = 8, message = "Password must contain at least 8 characters")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&._#\\-])[A-Za-z\\d@$!%*?&._#\\-]{8,}$",
                message = "The password must contain at least one uppercase letter, one lowercase letter, one number and one special character"
        )
        String password,

        String phone
) {
}
