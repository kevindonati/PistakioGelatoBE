package kevindonati.PistakioGelatoBE.repositories;

import kevindonati.PistakioGelatoBE.entities.AdminNotificationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminNotificationStateRepository extends JpaRepository<AdminNotificationState, UUID> {
    Optional<AdminNotificationState> findByAdminId(UUID adminId);
}