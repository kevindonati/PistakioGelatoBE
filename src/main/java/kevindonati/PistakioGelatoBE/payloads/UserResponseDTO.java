package kevindonati.PistakioGelatoBE.payloads;

import kevindonati.PistakioGelatoBE.enums.Language;
import kevindonati.PistakioGelatoBE.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(UUID id,
                              String name,
                              String surname,
                              String email,
                              String phone,
                              UserRole role,
                              boolean enabled,
                              Language language,
                              LocalDateTime createdAt
) {
}
