package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.UUID;

public record AddressDTO(
        @NotBlank(message = "The address is mandatory")
        String addressLine1,

        String addressLine2,

        @NotBlank(message = "The postal code is mandatory")
        String postalCode,

        @NotBlank(message = "The city is mandatory")
        String city,

        @NotBlank(message = "The country is mandatory")
        String country,

        UUID user
) {
}
