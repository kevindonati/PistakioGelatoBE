package kevindonati.PistakioGelatoBE.payloads;

import java.util.UUID;

public record FlavorDetailsDTO(
        UUID id,
        String name,
        String description,
        String referralCode,
        String image,
        int stockPortions,
        boolean available,
        boolean vegan,
        boolean lactoseFree,
        boolean glutenFree,
        boolean sugarFree,
        UUID category
) {
}
