package kevindonati.PistakioGelatoBE.repositories;

import kevindonati.PistakioGelatoBE.entities.Flavor;
import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.entities.OrderItem;
import kevindonati.PistakioGelatoBE.entities.Tub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findByOrderId(UUID id);

    Optional<OrderItem> findByOrderAndFlavorAndTub(Order order, Flavor flavor, Tub tub);

    List<OrderItem> findByOrder(Order order);

    Page<OrderItem> findByOrderUserId(UUID userId, Pageable pageable);

    boolean existsByTub(Tub tub);
}
