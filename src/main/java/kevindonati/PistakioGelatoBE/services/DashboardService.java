package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.enums.OrderStatus;
import kevindonati.PistakioGelatoBE.enums.PaymentStatus;
import kevindonati.PistakioGelatoBE.payloads.DashboardStatsDTO;
import kevindonati.PistakioGelatoBE.repositories.OrderRepository;
import kevindonati.PistakioGelatoBE.repositories.PaymentRepository;
import kevindonati.PistakioGelatoBE.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public DashboardStatsDTO getStats() {
        long totalOrders = orderRepository.count() - orderRepository.countByOrderStatus(OrderStatus.CART);

        long totalCustomers = userRepository.count();

        double revenue = paymentRepository.sumAmountByStatus(PaymentStatus.COMPLETED);

        long pendingPayments = orderRepository.countByOrderStatus(OrderStatus.PENDING_PAYMENT);

        long preparingOrders = orderRepository.countByOrderStatus(OrderStatus.PREPARING);

        long shippedOrders = orderRepository.countByOrderStatus(OrderStatus.SHIPPED);

        long deliveredOrders = orderRepository.countByOrderStatus(OrderStatus.DELIVERED);

        return new DashboardStatsDTO(
                totalOrders,
                totalCustomers,
                revenue,
                pendingPayments,
                preparingOrders,
                shippedOrders,
                deliveredOrders
        );
    }
}