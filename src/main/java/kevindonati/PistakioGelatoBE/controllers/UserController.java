package kevindonati.PistakioGelatoBE.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kevindonati.PistakioGelatoBE.entities.User;
import kevindonati.PistakioGelatoBE.enums.UserRole;
import kevindonati.PistakioGelatoBE.payloads.*;
import kevindonati.PistakioGelatoBE.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<User> findAll(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(defaultValue = "name") String orderBy,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) UserRole role,
                              @RequestParam(required = false) Boolean enabled) {

        return userService.findAll(page, size, orderBy, search, role, enabled);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public UserResponseDTO getMe() {
        User user = userService.getMe();

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.isEnabled(),
                user.getLanguage(),
                user.getCreatedAt()
        );
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public User findById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public UserResponseDTO update(@PathVariable UUID id, @RequestBody @Validated UserUpdateDTO payload) {
        User updatedUser = userService.findByIdAndUpdate(id, payload);
        return new UserResponseDTO(updatedUser.getId(), updatedUser.getName(), updatedUser.getSurname(), updatedUser.getEmail(), updatedUser.getPhone(), updatedUser.getRole(), updatedUser.isEnabled(), updatedUser.getLanguage(), updatedUser.getCreatedAt());
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        userService.findByIdAndDelete(id);
    }

    @PostMapping("/forgot-password")
    public void forgotPassword(
            @RequestBody @Validated ForgotPasswordDTO payload
    ) {
        userService.forgotPassword(payload);
    }

    @PostMapping("/reset-password")
    public void resetPassword(
            @RequestBody @Validated ResetPasswordDTO payload
    ) {
        userService.resetPassword(payload);
    }

    @PutMapping("/{id}/admin")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDTO updateByAdmin(@PathVariable UUID id, @RequestBody @Validated AdminUserUpdateDTO payload) {
        User updatedUser = userService.findByIdAndUpdateByAdmin(id, payload);

        return new UserResponseDTO(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getSurname(),
                updatedUser.getEmail(),
                updatedUser.getPhone(),
                updatedUser.getRole(),
                updatedUser.isEnabled(),
                updatedUser.getLanguage(),
                updatedUser.getCreatedAt()
        );
    }
}
