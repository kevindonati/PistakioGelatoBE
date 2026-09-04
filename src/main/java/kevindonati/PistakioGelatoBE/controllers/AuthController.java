package kevindonati.PistakioGelatoBE.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import kevindonati.PistakioGelatoBE.entities.User;
import kevindonati.PistakioGelatoBE.payloads.LoginDTO;
import kevindonati.PistakioGelatoBE.payloads.LoginResponseDTO;
import kevindonati.PistakioGelatoBE.payloads.UserDTO;
import kevindonati.PistakioGelatoBE.payloads.UserResponseDTO;
import kevindonati.PistakioGelatoBE.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "User registration and authentication")
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(
            @RequestBody @Validated UserDTO payload
    ) {
        User savedUser = authService.register(payload);
        return new UserResponseDTO(savedUser.getId(), savedUser.getName(), savedUser.getSurname(), savedUser.getEmail(), savedUser.getPhone(), savedUser.getRole(), savedUser.isEnabled(), savedUser.getLanguage(), savedUser.getCreatedAt());
    }

    @PostMapping("/login")
    public LoginResponseDTO login(
            @RequestBody @Validated LoginDTO payload
    ) {
        return authService.login(payload);
    }
}
