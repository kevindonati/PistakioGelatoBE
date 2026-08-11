package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FlavorDTO(
        @NotBlank(message = "The name of the flavor is mandatory")
        String name,

        String description,

        @NotBlank(message = "The referral  code for the flavor is mandatory")
        String referralCode,

        String image,

        @NotNull(message = "The stock quantity for the flavor is mandatory")
        @Min(value = 0, message = "The stoch can't be lower than 0")
        int stockPortions,

        boolean available,

        boolean vegan,

        boolean lactoseFree,

        boolean glutenFree,

        boolean sugarFree,

        @NotNull(message = "The category is mandatory")
        UUID category
) {
}
