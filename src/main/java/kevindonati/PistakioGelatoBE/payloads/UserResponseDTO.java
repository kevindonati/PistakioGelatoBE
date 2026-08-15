package kevindonati.PistakioGelatoBE.payloads;

import kevindonati.PistakioGelatoBE.enums.UserRole;

import java.util.UUID;

public record UserResponseDTO(UUID id,
                              String name,
                              String surname,
                              String email,
                              String phone,
                              UserRole role,
                              boolean enabled
) {
}
