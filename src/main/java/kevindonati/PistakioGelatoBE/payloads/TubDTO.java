package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TubDTO(

        @NotNull(message = "The weight is mandatory")
        @Min(value = 1, message = "The weight must be greater than 0")
        Integer weight,

        @NotNull(message = "The price is mandatory")
        @DecimalMin(value = "0.00", inclusive = false, message = "The price must be greater than 0")
        double price,

        String image,

        @NotNull(message = "The availability is mandatory")
        Boolean available,

        @NotNull(message = "Translations are mandatory")
        List<TubTranslationDTO> translations
) {
}
