package kevindonati.PistakioGelatoBE.repositories;

import kevindonati.PistakioGelatoBE.entities.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import kevindonati.PistakioGelatoBE.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<User> findTop10ByRoleAndCreatedAtAfterOrderByCreatedAtDesc(UserRole role, LocalDateTime dateTime);

    long countByRoleAndCreatedAtAfter(UserRole role, LocalDateTime dateTime);
}
