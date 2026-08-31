package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.PasswordResetToken;
import kevindonati.PistakioGelatoBE.entities.User;
import kevindonati.PistakioGelatoBE.enums.UserRole;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.exceptions.UnauthorizedException;
import kevindonati.PistakioGelatoBE.payloads.AdminUserUpdateDTO;
import kevindonati.PistakioGelatoBE.payloads.ForgotPasswordDTO;
import kevindonati.PistakioGelatoBE.payloads.ResetPasswordDTO;
import kevindonati.PistakioGelatoBE.payloads.UserUpdateDTO;
import kevindonati.PistakioGelatoBE.repositories.PasswordResetTokenRepository;
import kevindonati.PistakioGelatoBE.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${frontend.url}")
    private String frontendUrl;

    private User getAuthenticatedUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return (User) authentication.getPrincipal();
    }

    public Page<User> findAll(int page, int size, String orderBy, String search, UserRole role, Boolean enabled) {
        if (size > 50) size = 50;
        if (size < 1) size = 10;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));

        Specification<User> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.conjunction();

        if (search != null &&
                !search.trim().isEmpty()) {

            String searchValue = "%" + search.trim().toLowerCase() + "%";

            Specification<User> searchSpecification =
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.or(
                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(
                                                    root.get("name")
                                            ),
                                            searchValue
                                    ),
                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(
                                                    root.get("surname")
                                            ),
                                            searchValue
                                    ),
                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(
                                                    root.get("email")
                                            ),
                                            searchValue
                                    )
                            );
            specification = specification.and(searchSpecification);
        }

        if (role != null) {
            Specification<User> roleSpecification = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("role"), role);

            specification = specification.and(roleSpecification);
        }

        if (enabled != null) {
            Specification<User> enabledSpecification =
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("enabled"),
                                    enabled
                            );
            specification = specification.and(enabledSpecification);
        }
        return userRepository.findAll(specification, pageable);
    }


    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    public User findByIdAndUpdate(UUID id, UserUpdateDTO payload) {
        User authenticatedUser = getAuthenticatedUser();
        if (!authenticatedUser.getRole().name().equals("ADMIN") &&
                !authenticatedUser.getId().equals(id)) {
            throw new UnauthorizedException("You can only update your own profile");
        }

        User foundedUser = this.findById(id);

        if (!foundedUser.getEmail().equals(payload.email()) && userRepository.existsByEmail(payload.email())) {
            throw new BadRequestException("This email is already registered");
        }
        foundedUser.setName(payload.name());
        foundedUser.setSurname(payload.surname());
        foundedUser.setEmail(payload.email());
        foundedUser.setPhone(payload.phone());
        foundedUser.setLanguage(payload.language());

        return userRepository.save(foundedUser);
    }

    public void findByIdAndDelete(UUID id) {
        User foundedUser = this.findById(id);

        userRepository.delete(foundedUser);
    }

    public User getMe() {
        return getAuthenticatedUser();
    }

    public void forgotPassword(ForgotPasswordDTO payload) {

        userRepository.findByEmail(payload.email())
                .ifPresent(user -> {

                    passwordResetTokenRepository.deleteByUserId(
                            user.getId()
                    );

                    String token =
                            UUID.randomUUID().toString();

                    PasswordResetToken resetToken =
                            new PasswordResetToken(
                                    token,
                                    user,
                                    LocalDateTime.now().plusMinutes(30)
                            );

                    passwordResetTokenRepository.save(
                            resetToken
                    );

                    String resetUrl =
                            frontendUrl
                                    + "/reset-password?token="
                                    + token;

                    emailService.sendPasswordResetEmail(
                            user,
                            resetUrl
                    );
                });
    }

    public void resetPassword(ResetPasswordDTO payload) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(payload.token())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Invalid or expired reset token"
                                )
                        );

        if (resetToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            passwordResetTokenRepository.delete(
                    resetToken
            );

            throw new BadRequestException(
                    "Invalid or expired reset token"
            );
        }

        User user = resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(
                        payload.password()
                )
        );

        userRepository.save(user);

        passwordResetTokenRepository.delete(
                resetToken
        );
    }

    public User findByIdAndUpdateByAdmin(UUID id, AdminUserUpdateDTO payload) {
        User foundedUser = this.findById(id);

        if (!foundedUser.getEmail().equals(payload.email()) && userRepository.existsByEmail(payload.email())) {
            throw new BadRequestException("This email is already registered");
        }

        foundedUser.setName(payload.name());
        foundedUser.setSurname(payload.surname());
        foundedUser.setEmail(payload.email());
        foundedUser.setPhone(payload.phone());
        foundedUser.setLanguage(payload.language());
        foundedUser.setRole(payload.role());
        foundedUser.setEnabled(payload.enabled());

        return userRepository.save(foundedUser);
    }
}
