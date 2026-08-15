package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.User;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.exceptions.UnauthorizedException;
import kevindonati.PistakioGelatoBE.payloads.UserDTO;
import kevindonati.PistakioGelatoBE.payloads.UserUpdateDTO;
import kevindonati.PistakioGelatoBE.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return (User) authentication.getPrincipal();
    }

    public Page<User> findAll(int page, int size, String orderBy) {
        if (size > 50) size = 50;
        if (size < 1) size = 10;
        if (page < 0) page = 0;

        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));
        return userRepository.findAll(pageable);
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

        return userRepository.save(foundedUser);
    }

    public void findByIdAndDelete(UUID id) {
        User foundedUser = this.findById(id);

        userRepository.delete(foundedUser);
    }
}
