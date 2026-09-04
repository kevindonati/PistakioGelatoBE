package kevindonati.PistakioGelatoBE.repositories;

import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository
        extends JpaRepository<Order, UUID>,
        JpaSpecificationExecutor<Order> {

    List<Order> findByUserId(UUID id);

    Page<Order> findByUserId(UUID userId, Pageable pageable);

    Optional<Order> findByUserIdAndOrderStatus(UUID userId, OrderStatus orderStatus);

    long countByOrderStatus(OrderStatus orderStatus);

    long countByOrderStatusAndCreatedAtBetween(OrderStatus orderStatus, LocalDateTime start, LocalDateTime end);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByCreatedAtAfterAndOrderStatusNot(LocalDateTime dateTime, OrderStatus orderStatus);

    List<Order> findTop10ByCreatedAtAfterAndOrderStatusNotOrderByCreatedAtDesc(LocalDateTime dateTime, OrderStatus orderStatus);
}