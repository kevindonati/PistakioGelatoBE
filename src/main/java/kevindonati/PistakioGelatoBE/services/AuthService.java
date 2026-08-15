package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.User;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.exceptions.UnauthorizedException;
import kevindonati.PistakioGelatoBE.payloads.LoginDTO;
import kevindonati.PistakioGelatoBE.payloads.LoginResponseDTO;
import kevindonati.PistakioGelatoBE.payloads.UserDTO;
import kevindonati.PistakioGelatoBE.repositories.UserRepository;
import kevindonati.PistakioGelatoBE.security.JWTTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JWTTools jwtTools;

    public User register(UserDTO payload) {
        if (userRepository.existsByEmail(payload.email())) {
            throw new BadRequestException(
                    "The email " + payload.email() + " is already registered"
            );
        }

        String hashedPassword = passwordEncoder.encode(payload.password());
        User newUser = new User(
                payload.name(),
                payload.surname(),
                payload.email(),
                hashedPassword,
                payload.phone(),
                payload.language()
        );
        User savedUser = userRepository.save(newUser);
        emailService.sendWelcomeEmail(savedUser);

        return savedUser;
    }

    public LoginResponseDTO login(LoginDTO payload) {
        User foundedUser = userRepository.findByEmail(payload.email())
                .orElseThrow(() ->
                        new UnauthorizedException("Email or password are incorrect")
                );

        if (!passwordEncoder.matches(payload.password(), foundedUser.getPassword())) {
            throw new UnauthorizedException("Email or password are incorrect");
        }

        if (!foundedUser.isEnabled()) {
            throw new UnauthorizedException("User is not enabled");
        }
        String token = jwtTools.generoToken(foundedUser);
        return new LoginResponseDTO(token);
    }
}
