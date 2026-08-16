package kevindonati.PistakioGelatoBE.payloads;

import java.util.UUID;

public record CategoryDetailsDTO(
        UUID id,
        String name,
        String description,
        String image
) {
}
