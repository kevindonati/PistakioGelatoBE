package kevindonati.PistakioGelatoBE.repositories;

import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.entities.Payment;
import kevindonati.PistakioGelatoBE.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    boolean existsByOrder(Order order);

    Optional<Payment> findByIdTransaction(String idTransaction);

    Page<Payment> findByOrderUserId(UUID userId, Pageable pageable);

    Optional<Payment> findByStripeEventId(String stripeEventId);

    Optional<Payment> findByOrder(Order order);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status")
    double sumAmountByStatus(@Param("status") PaymentStatus status);
}
