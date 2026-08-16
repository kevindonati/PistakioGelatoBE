package kevindonati.PistakioGelatoBE.payloads;

import java.util.UUID;

public record TubDetailsDTO(
        UUID id,
        String name,
        String description,
        int weight,
        double price,
        String image,
        boolean available
) {
}
