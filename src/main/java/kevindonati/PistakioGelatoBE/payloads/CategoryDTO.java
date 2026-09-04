package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CategoryDTO(
        String image,

        @NotNull(message = "Translations are mandatory")
        List<CategoryTranslationDTO> translations

) {
}
