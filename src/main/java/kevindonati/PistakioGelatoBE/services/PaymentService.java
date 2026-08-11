package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.entities.Payment;
import kevindonati.PistakioGelatoBE.entities.User;
import kevindonati.PistakioGelatoBE.enums.OrderStatus;
import kevindonati.PistakioGelatoBE.enums.PaymentStatus;
import kevindonati.PistakioGelatoBE.enums.UserRole;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.payloads.PaymentDTO;
import kevindonati.PistakioGelatoBE.repositories.PaymentRepository;
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
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    public Payment save(PaymentDTO payload) {
        Order foundedOrder = orderService.findById(payload.order());
        if (foundedOrder.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BadRequestException("The order is not waiting for payment");
        }

        if (foundedOrder.getTotal() <= 0) {
            throw new BadRequestException("The order total must be greater than zero");
        }

        if (paymentRepository.existsByOrder(foundedOrder)) {
            throw new BadRequestException("This order already has a payment");
        }

        Payment newPayment = new Payment(
                payload.providerType(),
                UUID.randomUUID().toString(),
                foundedOrder.getTotal(),
                "EUR",
                PaymentStatus.PENDING,
                foundedOrder
        );
        return paymentRepository.save(newPayment);
    }

    public Page<Payment> findAll(int page, int size, String orderBy) {
        if (size > 50) size = 50;
        if (size < 1) size = 10;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));

        User authenticatedUser = getAuthenticatedUser();

        if (authenticatedUser.getRole() == UserRole.ADMIN) {
            return paymentRepository.findAll(pageable);
        }
        return paymentRepository.findByOrderUserId(authenticatedUser.getId(), pageable);
    }

    public Payment findById(UUID id) {
        Payment foundedPayment = paymentRepository.findById(id).orElseThrow(() -> new NotFoundException("Payment with id " + id + " not found"));
        orderService.findById(foundedPayment.getOrder().getId());
        return foundedPayment;
    }

    public Payment completeStripePayment(String transactionId) {
        Payment foundedPayment = paymentRepository.findByIdTransaction(transactionId).orElseThrow(() -> new NotFoundException("Payment with transaction " + transactionId + " not found"));

        if (foundedPayment.getStatus() == PaymentStatus.COMPLETED) {
            return foundedPayment;
        }

        foundedPayment.setStatus(PaymentStatus.COMPLETED);
        orderService.confirmPaymentFromStripe(foundedPayment.getOrder().getId());

        return paymentRepository.save(foundedPayment);
    }
}
