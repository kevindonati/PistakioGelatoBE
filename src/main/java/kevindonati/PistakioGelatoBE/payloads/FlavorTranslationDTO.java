package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kevindonati.PistakioGelatoBE.enums.Language;

public record FlavorTranslationDTO(
        @NotNull
        Language language,

        @NotBlank(message = "Name is mandatory")
        String name,

        String description
) {
}
