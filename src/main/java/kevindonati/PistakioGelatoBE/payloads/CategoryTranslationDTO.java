package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kevindonati.PistakioGelatoBE.enums.Language;

public record CategoryTranslationDTO(

        @NotNull
        Language language,

        @NotBlank
        String name,

        String description

) {
}
