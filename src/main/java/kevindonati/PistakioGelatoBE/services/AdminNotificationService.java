package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.AdminNotificationState;
import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.entities.User;
import kevindonati.PistakioGelatoBE.enums.OrderStatus;
import kevindonati.PistakioGelatoBE.enums.UserRole;
import kevindonati.PistakioGelatoBE.exceptions.UnauthorizedException;
import kevindonati.PistakioGelatoBE.payloads.AdminNotificationResponse;
import kevindonati.PistakioGelatoBE.repositories.AdminNotificationStateRepository;
import kevindonati.PistakioGelatoBE.repositories.OrderRepository;
import kevindonati.PistakioGelatoBE.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminNotificationService {

    private final AdminNotificationStateRepository stateRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public AdminNotificationService(AdminNotificationStateRepository stateRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.stateRepository = stateRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        if (user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Only administrators can access notifications");
        }
        return user;
    }

    private AdminNotificationState getOrCreateState(User admin) {
        return stateRepository.findByAdminId(admin.getId()).orElseGet(() -> stateRepository.save(new AdminNotificationState(admin)));
    }

    @Transactional
    public AdminNotificationResponse getNotifications() {
        User admin = getAuthenticatedAdmin();

        AdminNotificationState state = getOrCreateState(admin);

        LocalDateTime ordersSince = state.getLastOrdersSeenAt();
        LocalDateTime customersSince = state.getLastCustomersSeenAt();

        long ordersCount = orderRepository.countByCreatedAtAfterAndOrderStatusNot(
                ordersSince,
                OrderStatus.CART
        );

        long customersCount = userRepository.countByRoleAndCreatedAtAfter(
                UserRole.USER,
                customersSince
        );

        List<Order> orders = orderRepository.findTop10ByCreatedAtAfterAndOrderStatusNotOrderByCreatedAtDesc(
                ordersSince,
                OrderStatus.CART
        );

        List<User> customers = userRepository.findTop10ByRoleAndCreatedAtAfterOrderByCreatedAtDesc(
                UserRole.USER,
                customersSince
        );

        return new AdminNotificationResponse(ordersCount, customersCount, orders, customers);
    }

    @Transactional
    public void markOrdersAsRead() {
        User admin = getAuthenticatedAdmin();
        AdminNotificationState state = getOrCreateState(admin);

        state.setLastOrdersSeenAt(LocalDateTime.now());
        stateRepository.save(state);
    }

    @Transactional
    public void markCustomersAsRead() {
        User admin = getAuthenticatedAdmin();
        AdminNotificationState state = getOrCreateState(admin);

        state.setLastCustomersSeenAt(LocalDateTime.now());
        stateRepository.save(state);
    }
}