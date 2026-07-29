package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.NotBlank;

public record CategoryDTO(
        @NotBlank(message = "The name of the category is mandatory")
        String name,

        String description,

        String image
) {
}
