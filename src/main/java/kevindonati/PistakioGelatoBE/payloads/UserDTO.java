package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.*;
import kevindonati.PistakioGelatoBE.enums.Language;

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

        @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$",
                message = "The phone number format is invalid")
        String phone,

        @NotNull(message = "Language is mandatory")
        Language language
) {
}
